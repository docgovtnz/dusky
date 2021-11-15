package com.fronde.server.services.upgrade;

import static com.fronde.server.utils.ServiceManager.TIMEOUT_MILLIS;

import com.fronde.server.services.application.ApplicationService;
import com.fronde.server.utils.CommandLineExecutor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UpgradeService {

  private static final Logger logger = LoggerFactory.getLogger(UpgradeService.class);


  public static final File CURRENT_DIR = new File(".");
  public static final File NEXT_RELEASE_DIR = new File("NextRelease");
  public static final File OLD_RELEASE_DIR = new File("OldRelease");
  public static final long DISK_SPACE_REQUIRED = 500000000L; // Number of bytes of free disk space required before upgrade is allowed

  // WARNING: Can't do auto-upgrade with Dusky.exe (can't copy over it while running)
  private final String[][] FILE_DATA = new String[][]{
      {"Dusky.jar", "."},
      {"Dusky.xml", "client"},
      {"init-sync-gateway.bat", "."},
      {"ReadMe.txt", "."}
  };

  @Value("${application.mode}")
  protected String serverMode;

  @Autowired
  protected ApplicationService applicationService;

  @Autowired
  protected UpgradeClient upgradeClient;

  private Map<String, String> fileLocationMap;


  public Map<String, String> getFileLocationMap() {
    if (fileLocationMap == null) {
      fileLocationMap = new HashMap();
      for (String[] nextFileData : FILE_DATA) {
        fileLocationMap.put(nextFileData[0], nextFileData[1]);
      }
    }
    return fileLocationMap;
  }

  public List<String> getFileList() {
    List<String> fileList = new ArrayList<>(getFileLocationMap().keySet());
    Collections.sort(fileList);
    return fileList;
  }

  public FileMetaDataList localDownloadList() {
    FileMetaDataList fileMetaDataList = upgradeClient.remoteDownloadList();
    fileMetaDataList.setLocalVersion(applicationService.getStatus().getVersion());
    //fileMetaDataList.setLocalVersion("6.4.0");
    fileMetaDataList.getList().forEach(this::checkLocalStatus);
    return fileMetaDataList;
  }

  private void checkLocalStatus(FileMetaData fileMetaData) {
    try {
      File localFile = new File(NEXT_RELEASE_DIR, fileMetaData.getName());
      if (localFile.exists()) {
        long localChecksum = FileUtils.checksumCRC32(localFile);
        fileMetaData.setLocalChecksum(localChecksum);
        if (localChecksum == fileMetaData.getExpectedChecksum()) {
          fileMetaData.setStatus("Ok");
        } else {
          fileMetaData.setStatus("Invalid");
        }
      } else {
        fileMetaData.setStatus("Missing");
      }
    } catch (IOException e) {
      fileMetaData.setStatus("Error: " + e.getClass().getSimpleName() + " " + e.getMessage());
    }
  }

  public UpgradeResult localDownloadFiles() {
    try {
      if (!NEXT_RELEASE_DIR.exists()) {
        NEXT_RELEASE_DIR.mkdir();
      }

      FileMetaDataList fileMetaDataList = upgradeClient.remoteDownloadList();
      for (FileMetaData fileMetaData : fileMetaDataList.getList()) {

        checkLocalStatus(fileMetaData);

        // Wanted the ability to be able to download an empty file (zero length). This meant two things, the
        // expectedChecksum can be zero, and the removeDownloadFile() method can return null, since you can't
        // write out a null array the fileData variable need to be modified to be a zero length array.

        if (!fileMetaData.getStatus().equals("Ok") && fileMetaData.getExpectedChecksum() >= 0) {
          String file = fileMetaData.getName();
          byte[] fileData = upgradeClient.remoteDownloadFile(file);
          if (fileData == null) {
            fileData = new byte[0];
          }

          File outputFile = new File(NEXT_RELEASE_DIR, file);
          FileUtils.writeByteArrayToFile(outputFile, fileData);
        }
      }

      return new UpgradeResult("Success");
    } catch (Exception ex) {
      logger.error("UpgradeService download failed.", ex);
      return new UpgradeResult("Failed");
    }
  }


  public FileMetaDataList remoteDownloadList() {

    FileMetaDataList fileMetaDataList = new FileMetaDataList();
    fileMetaDataList.setCloudVersion(applicationService.getStatus().getVersion());

    for (String nextFile : getFileList()) {
      FileMetaData fileMetaData = new FileMetaData();
      fileMetaData.setName(nextFile);

      try {
        File remoteFile = new File(getFileLocationMap().get(nextFile), nextFile);
        if (remoteFile.exists() && remoteFile.canRead()) {
          fileMetaData.setExpectedChecksum(FileUtils.checksumCRC32(remoteFile));
        } else {
          fileMetaData.setExpectedChecksum(-1L);
        }
      } catch (IOException e) {
        fileMetaData.setExpectedChecksum(-1L);
      }

      fileMetaDataList.getList().add(fileMetaData);
    }

    return fileMetaDataList;
  }

  public byte[] remoteDownloadFile(String file) {
    try {
      byte[] fileData;
      if (getFileLocationMap().containsKey(file)) {
        fileData = FileUtils.readFileToByteArray(new File(getFileLocationMap().get(file), file));
      } else {
        throw new RuntimeException("Invalid file download request for: " + file);
      }

      return fileData;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public UpgradeResult localResetFiles() {
    if (NEXT_RELEASE_DIR.exists()) {
      File[] files = NEXT_RELEASE_DIR.listFiles();
      for (File file : files) {
        file.delete();
      }
    }
    return new UpgradeResult("Success");
  }

  private boolean areFilesValid() {
    FileMetaDataList fileMetaDataList = localDownloadList();
    boolean allOk = true;
    for (FileMetaData nextFile : fileMetaDataList.getList()) {
      allOk = allOk && nextFile.getStatus().equals("Ok");
    }

    return allOk;
  }

  private boolean isThereDiskSpace() {
    return NEXT_RELEASE_DIR.getFreeSpace() > DISK_SPACE_REQUIRED;
  }

  private boolean isNotServerMode() {
    return !serverMode.equals("Server");
  }

  public UpgradeResult upgradeRelease() {

    // Just some dummy code to avoid a special and unique class loading problem. After the jar files have been
    // copied the Logging framework doesn't like new Loggers to be loaded/created. Doing this here lets a little
    // bit more logging but still also needs this method to swallow the NoClassDefFoundError and return a success.
    CommandLineExecutor cmd = new CommandLineExecutor("nothing");

    logger.info("Upgrade release: process starting...");
    if (!areFilesValid()) {
      return createUpgradeResult("Upgrade not allowed at the moment. Files missing or invalid.");
    }

    if (!isThereDiskSpace()) {
      return createUpgradeResult("Upgrade not allowed at the moment. Insufficient disk space.");
    }

    if (!isNotServerMode()) {
      return createUpgradeResult(
          "Upgrade not allowed at the moment. Server mode not permitted for upgrades.");
    }

    logger.info("Upgrade release: all checks passed ok.");
    UpgradeResult upgradeResult;
    try {

      // If the old release directory doesn't exist then create it
      if (!OLD_RELEASE_DIR.exists()) {
        OLD_RELEASE_DIR.mkdirs();
        logger.info("Upgrade release: created: " + OLD_RELEASE_DIR + " directory");
      } else {
        // If it does exist, then delete any files it may contain
        File[] oldFiles = OLD_RELEASE_DIR.listFiles();
        for (File previousOldFile : oldFiles) {
          previousOldFile.delete();
        }
        logger.info("Upgrade release: deleted " + oldFiles.length + " old files");
      }

      // Copy all of the current version of the files into the old release directory incase we need to rollback
      logger.info("Upgrade release: copying files... ");
      for (String oldFilename : getFileList()) {
        File oldFile = new File(oldFilename);
        if (oldFile.exists()) {
          logger.info("Upgrade release: copy old file " + oldFile);
          FileUtils.copyFileToDirectory(oldFile, OLD_RELEASE_DIR);
          logger.info("Upgrade release: copy old file ok");
        }
      }

      // Copy all of the new versions into the current directory
      File[] files = NEXT_RELEASE_DIR.listFiles();
      for (File nextFile : files) {
        logger.info("Upgrade release: copy new file " + nextFile);
        FileUtils.copyFileToDirectory(nextFile, CURRENT_DIR);
        logger.info("Upgrade release: copy new file ok");
      }

      // Restart the service
      logger.info("Upgrade release: all files copied about to restart...");
      System.out.println("Upgrade release: all files copied about to restart...");
      CommandLineExecutor restartService = CommandLineExecutor.build("Dusky.exe")
          .argument("restart!")
          .timeout(TIMEOUT_MILLIS)
          .execute();

      // no logging from here onwards
      //logger.info("Upgrade release: restart complete.");

      upgradeResult = new UpgradeResult("Upgrade successful. Restart in progress - please wait.");
    } catch (NoClassDefFoundError err) {
      // Swallow logback problem and use that error as an indication of success, see comment at the top of the method
      upgradeResult = new UpgradeResult("Upgrade successful. Restart in progress - please wait.");
    } catch (IOException e) {
      logger.error("Upgrade failed. Attempting rollback...");
      try {
        // If the upgrade process fails then attempt to rollback to the old version
        for (String oldFilename : getFileList()) {
          File oldFile = new File(OLD_RELEASE_DIR, oldFilename);
          if (oldFile.exists()) {
            FileUtils.copyFileToDirectory(oldFile, CURRENT_DIR);
          }
        }

        upgradeResult = new UpgradeResult("Upgrade failed. Rollback successful.");
      } catch (IOException e2) {
        String msg2 = "Upgrade failed. Rollback failed.";
        logger.error(msg2, e2);
        upgradeResult = new UpgradeResult(msg2);
      }
    }

    return upgradeResult;
  }

  private UpgradeResult createUpgradeResult(String message) {
    UpgradeResult upgradeResult = new UpgradeResult(message);
    logger.info(upgradeResult.toString());
    return upgradeResult;
  }

  public UpgradeResult restartCheck() {
    String version = applicationService.getStatus().getVersion();
    return new UpgradeResult("Restarted: " + version);
  }
}

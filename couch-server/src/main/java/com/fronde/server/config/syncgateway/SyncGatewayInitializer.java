package com.fronde.server.config.syncgateway;

import com.fronde.server.CouchServerApplication;
import com.fronde.server.utils.ServiceManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
@ComponentScan("com.fronde.server.config.syncgateway")
@SpringBootApplication
public class SyncGatewayInitializer {

  /**
   * Logger.
   */
  private static final org.slf4j.Logger logger = LoggerFactory
      .getLogger(SyncGatewayInitializer.class);

  /**
   * The service's display name used for stop/start the service.
   */
  public static final String SERVICE_DISPLAY_NAME = "Couchbase Sync Gateway";
  /**
   * The service's underlying name for executing "sc".
   */
  public static final String SERVICE_NAME = "SyncGateway";

  @Value("${server.ssl.key-store}")
  private String keystoreFilename;

  @Value("${server.ssl.key-store-password}")
  private String storePassword;

  @Value("${server.ssl.key-password}")
  private String keyPassword;

  @Value("${couchbase.bucket.password}")
  private String bucketPassword;

  @Value("${server.ssl.key-alias}")
  private String keystoreAlias;

  @Value("${sync.gateway.binPath}")
  private String syncGatewayBinPath;

  private final ServiceManager serviceManager = new ServiceManager();

  private boolean success = false;

  /**
   * Main routine to run as an installation task.
   *
   * @param args Ignored.
   */
  public static void main(String[] args) {
    // Generate the properties file as we need that.
    CouchServerApplication.popuplatePasswords();

    SpringApplication app = new SpringApplication(SyncGatewayInitializer.class);
    app.setWebApplicationType(WebApplicationType.NONE);
    ConfigurableApplicationContext context = app.run(args);
    SyncGatewayInitializer initializer = context.getBean(SyncGatewayInitializer.class);
    initializer.initialize(true);
    if (initializer.success) {
      logger.info("Sync Gateway initialisation was successful");
      System.exit(0);
    } else {
      logger.error("Sync Gateway initialisation failed");
      System.exit(1);
    }
  }

  public void initialize() {
    initialize(false);
  }

  private boolean isInitializationRequired(boolean force) {

    boolean lockFileExists = isLockFileExist();
    boolean guestDisabled = isGuestDisabled();

    // Intialization is required if force has been requested, or there is no lockFile or guest has not been disabled
    return force || !lockFileExists || !guestDisabled;
  }

  private boolean isLockFileExist() {
    return getLockFile().exists();
  }

  /**
   * This check can be removed in the future from about release 6 onwards once we know that the
   * change has propagated to all environmets.
   */
  private boolean isGuestDisabled() {
    boolean guestDisabled = false;
    try {
      File basePath = new File(System.getProperty("user.dir"));
      File file = new File(basePath, "serviceconfig.json");
      if (file.exists()) {
        String fileContents = FileUtils.readFileToString(file);
        guestDisabled = fileContents.contains("\"GUEST\": { \"disabled\": true");
      }
    } catch (IOException ex) {
      // swallow and return false
    }
    return guestDisabled;
  }

  private File getLockFile() {
    File basePath = new File(System.getProperty("user.dir"));
    File lockFile = new File(basePath, "sync-gateway-init.lock");
    return lockFile;
  }

  private void createLockFile() {
    try {
      getLockFile().createNewFile();
    } catch (Exception ex) {
      logger.error("Couldn't create lock file. Sync Gateway will be reinitialized on next start");
    }
  }

  /**
   * Initializes the Sync Gateway by making sure it is using the right certificates and
   * configuration file.
   */
  public void initialize(boolean force) {
    try {
      File basePath = new File(System.getProperty("user.dir"));
      logger.info(
          "Sync Gateway Initialization using working directory: " + basePath.getAbsolutePath());

      if (!isInitializationRequired(force)) {
        logger.info("Couchbase Sync Gateway is already configured.");
        return;
      }
      logger.info("Couchbase Sync Gateway has not been configured; configuring it now");

      // Base filenames
      File certFile = new File(basePath, "publickey.crt");
      File keyFile = new File(basePath, "privatekey.key");

      logger
          .info("Couchbase Sync Server is missing certifcate and/or private key. Initializing...");

      // Load the keystore.
      File keystore = new File(keystoreFilename);
      KeyStore p12 = getKeyStore(keystore);

      // Load the key.
      KeyStore.Entry pkey = getKeyEntry(p12);

      // Write the PEM files.
      logger.info("Writing PEM files to Couchbase Sync Server Directory");
      writeToPEM(((KeyStore.PrivateKeyEntry) pkey).getPrivateKey(), keyFile);
      writeToPEM(((KeyStore.PrivateKeyEntry) pkey).getCertificate(), certFile);
      logger.info("PEM files written to Couchbase Sync Server Directory");

      // Now we need to reconfigure the couchbase service...
      rewriteSyncGatewayConfig(basePath, certFile, keyFile);

      // Reconfigure the service.
      if (serviceManager.stop(SERVICE_DISPLAY_NAME)) {
        if (serviceManager.setBinaryPath(SERVICE_NAME, syncGatewayBinPath)) {
          logger.info("Successfully modified the Sync Gateway binary path.");
        } else {
          logger.error("Failed to modify the Sync Gateway binary path.");
        }

        // Start the service back up.
        if (serviceManager.start(SERVICE_DISPLAY_NAME)) {
          this.success = true;
          createLockFile();
        }
      } else {
        throw new SyncServerInitializerException(
            "Could not stop service. (If not running from windows service, check you are running with Administrator rights)");
      }
    } catch (SyncServerInitializerException ex) {
      logger.error("Could not configure Sync Gateway", ex);
      logger.error("Continuing with Dusky startup anyway");
    }
  }

  private String replace(String regex, String source, String replacement) {
    Pattern findServiceJson = Pattern.compile(regex);
    Matcher m = findServiceJson.matcher(source);
    if (m.find()) {
      return source.replace(m.group(), replacement);
    } else {
      return null;
    }
  }

  private void rewriteSyncGatewayConfig(File basePath, File certFile, File keyFile)
      throws SyncServerInitializerException {
    try (
        BufferedReader br = new BufferedReader(new InputStreamReader(
            SyncGatewayInitializer.class.getResourceAsStream("/serviceconfig.json.template")));
        PrintWriter pw = new PrintWriter(
            new FileWriter(new File(basePath, "serviceconfig.json")))) {

      logger.info("Updating Couch Sync Server Config");
      String line = br.readLine();
      while (line != null) {
        line = line.replaceAll(Pattern.quote("${SSLCert}"),
            certFile.getAbsolutePath().replaceAll("\\\\", "/"));
        line = line.replaceAll(Pattern.quote("${SSLKey}"),
            keyFile.getAbsolutePath().replaceAll("\\\\", "/"));
        line = line.replaceAll(Pattern.quote("${bucketPassword}"), bucketPassword);
        pw.println(line);
        line = br.readLine();
      }
      logger.info("Couch Sync Server Config updated");

    } catch (IOException ex) {
      throw new SyncServerInitializerException("Could not generate the service config file.");
    }
  }

  private KeyStore.Entry getKeyEntry(KeyStore p12) throws SyncServerInitializerException {
    KeyStore.Entry pkey = null;
    KeyStore.ProtectionParameter param = new KeyStore.PasswordProtection(keyPassword.toCharArray());
    String[] aliases = new String[]{keystoreAlias, getLocalhost().getCanonicalHostName(),
        getLocalhost().getHostName(), "1"};
    for (int i = 0; i < aliases.length && pkey == null; i++) {
      try {
        pkey = p12.getEntry(aliases[i], param);
      } catch (Exception ex) {
        throw new SyncServerInitializerException(
            "Could not read key with alias '" + aliases[i] + "'", ex);
      }
      if (pkey != null) {
        logger.info("Found key under alias: " + aliases[i]);
      }
    }

    // Check that we found the key.
    if (pkey == null) {
      throw new SyncServerInitializerException(
          "Could not find key under any of the expected alias names");
    }

    // Check it is of the correct type.
    if (!(pkey instanceof KeyStore.PrivateKeyEntry)) {
      throw new SyncServerInitializerException("Found key but it is not of the correct type.");
    }
    return pkey;
  }

  private KeyStore getKeyStore(File keystore) throws SyncServerInitializerException {
    try {
      KeyStore p12 = KeyStore.getInstance("pkcs12");
      p12.load(new FileInputStream(keystore), storePassword.toCharArray());
      logger.info("Loaded keystore from: " + keystore.getAbsolutePath());
      return p12;
    } catch (FileNotFoundException ex) {
      throw new SyncServerInitializerException(
          "Key file not found at: " + keystore.getAbsolutePath());
    } catch (Exception ex) {
      throw new SyncServerInitializerException(
          "Could not read key file: " + keystore.getAbsolutePath());
    }
  }


  private static class SyncServerInitializerException extends Exception {

    public SyncServerInitializerException() {
    }

    public SyncServerInitializerException(String message) {
      super(message);
    }

    public SyncServerInitializerException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  private void writeToPEM(Object object, File destination) throws SyncServerInitializerException {
    try (
        JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter(destination))
    ) {
      pemWriter.writeObject(object);
      pemWriter.flush();
    } catch (IOException ex) {
      throw new SyncServerInitializerException(
          "Unable to write PEM file to " + destination.getAbsolutePath(), ex);
    }
  }

  private InetAddress getLocalhost() throws SyncServerInitializerException {
    try {
      return InetAddress.getLocalHost();
    } catch (UnknownHostException ex) {
      throw new SyncServerInitializerException("Could not find localhost", ex);
    }
  }

}


package com.fronde.server.services.noranet;

import com.fronde.server.domain.IslandEntity;
import com.fronde.server.domain.NoraNetBirdEntity;
import com.fronde.server.domain.NoraNetCmFemaleEntity;
import com.fronde.server.domain.NoraNetCmLongEntity;
import com.fronde.server.domain.NoraNetCmShortEntity;
import com.fronde.server.domain.NoraNetDetectionEntity;
import com.fronde.server.domain.NoraNetEggTimerEntity;
import com.fronde.server.domain.NoraNetEntity;
import com.fronde.server.domain.NoraNetErrorEntity;
import com.fronde.server.domain.NoraNetStandardEntity;
import com.fronde.server.domain.NoraNetWeightEntity;
import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.bird.BirdService;
import com.fronde.server.services.common.CommonRepository;
import com.fronde.server.services.id.IdSearchCriteria;
import com.fronde.server.services.id.IdSearchDTO;
import com.fronde.server.services.island.IslandCriteria;
import com.fronde.server.services.island.IslandRepository;
import com.fronde.server.services.noranet.NoraNetSearchDTO.Bird;
import com.fronde.server.services.noranet.NoraNetSearchDTO.Female;
import com.fronde.server.services.noraneterror.NoraNetErrorService;
import com.fronde.server.utils.CSVExportUtils;
import com.fronde.server.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import javax.servlet.http.HttpServletResponse;

/**
 * For processing NoraNet Json data posted to Dusky from the AWS S3 bucket
 *
 * @version 1.0
 * @date 14/07/2021
 */

@Component
public class NoraNetService extends NoraNetBaseService {

  @Autowired
  private IslandRepository islandRepository;
  @Autowired
  protected CommonRepository commonRepository;
  @Autowired
  protected NoraNetRepository noranetRepository;
  @Autowired
  protected NoraNetErrorService noranetErrorService;
  @Autowired
  private BirdService birdService;
  @Autowired
  protected JsonUtils jsonUtils;
  @Autowired
  protected CSVExportUtils exportUtils;

  private static final List<String> fileTypeList = Arrays.asList(
      "DETECT1", "STANDARD1", "EGG1", "CMSHORT1", "CMLONG1", "WEIGHT1");
  private static final List<String> snarkTypeList = Arrays.asList("H", "B", "E", "N", "F");

  private static final String NULLCHARVALUE = "u0000";

  private static final Map<String, String> CATEGORY_CODE_TO_DESCRIPTION;

  static {
    CATEGORY_CODE_TO_DESCRIPTION = new HashMap<>();
    CATEGORY_CODE_TO_DESCRIPTION.put("D", "Detection");
    CATEGORY_CODE_TO_DESCRIPTION.put("S", "Standard");
    CATEGORY_CODE_TO_DESCRIPTION.put("E", "Non-Incubating Egg Timer");
    CATEGORY_CODE_TO_DESCRIPTION.put("I", "Incubating Egg Timer");
    CATEGORY_CODE_TO_DESCRIPTION.put("C", "Non-Mating Checkmate");
    CATEGORY_CODE_TO_DESCRIPTION.put("L", "Mating Checkmate");
  }

  private static final Map<String, String> STATION_TO_FULL_NAME;

  static {
    STATION_TO_FULL_NAME = new HashMap<>();
    STATION_TO_FULL_NAME.put("E", "Errol");
    STATION_TO_FULL_NAME.put("B", "Berrol");
    STATION_TO_FULL_NAME.put("H", "Hub");
    STATION_TO_FULL_NAME.put("R", "Relay");
    STATION_TO_FULL_NAME.put("F", "Feed-out Snark");
    STATION_TO_FULL_NAME.put("N", "Nest Snark");
  }

  public ResponseEntity<String> processNoraNetRequest(NoraNetRequest noraNetRequest) {

    String fileName = noraNetRequest.getFileName().toUpperCase();
    String[] splitList = fileName.split("_", 5);
    if (splitList.length != 4) {
      return processErrorResponse("Not a valid NoraNet file, cannot be processed",
          fileName, noraNetRequest.getFileData().toString());
    }


    String fileData = noraNetRequest.getFileData().toString();
    String dataType = splitList[0];
    if (!fileTypeList.contains(dataType)) {
      return processErrorResponse("Files of type " + dataType
          + " are not recognised NoraNet files", fileName, fileData);
    }
    Integer islandNo = Integer.valueOf(splitList[1]);
    String station = splitList[2];
    String fileDateString = splitList[3];
    Date fileDate;
    try {
      TimeZone timezone = TimeZone.getTimeZone("UTC");
      SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy", Locale.ENGLISH);
      formatter.setTimeZone(timezone);
      fileDate = formatter.parse(fileDateString);
    } catch (ParseException e) {
      e.printStackTrace();
      return processErrorResponse(
          "Not a NoraNet file, date component cannot be processed", fileName, fileData);
    }

    String jsonString = noraNetRequest.getFileData().toString();
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    String resultString = new String();

    NoraNetS3DataFile dataFileObj = new NoraNetS3DataFile();
    try {
      dataFileObj = mapper.readValue(jsonString, NoraNetS3DataFile.class);
    } catch (IOException e) {
      e.printStackTrace();
      return processErrorResponse(
          "file format is corrupt, unable to convert json data to java object", fileName, fileData);
    }
    dataFileObj.setDataType(dataType);
    dataFileObj.setIslandNo(islandNo);
    dataFileObj.setStation(station);
    String typeOfSnark = String.valueOf(Character.toChars(dataFileObj.getTypeOfSnark()));
    if (!checkStationAgainstSnarkInfo(station, typeOfSnark,
        dataFileObj.getIdOfSnark())) {
      return processErrorResponse("The station in filename does not match the Snark type and ID",
          fileName, fileData);
    }
    if (!snarkTypeList.contains(typeOfSnark)) {
      return processErrorResponse("Data from stations/snarks of type " + typeOfSnark
          + " cannot be processed by Dusky", fileName, fileData);
    }
    String stationId = convertToStationId(typeOfSnark, dataFileObj.getIdOfSnark());

    dataFileObj.setFileDate(fileDate);
    if (dataFileObj.getHeaderDateTime().compareTo(dataFileObj.getFileDate()) != 0) {
      return processErrorResponse("File date and activity date do not match", fileName, fileData);
    }
    if (islandNo.compareTo(dataFileObj.getIdOfIsland()) != 0) {
      return processErrorResponse(
          "IslandNo in the filename does not match the islandNo in the file", fileName, fileData);
    }
    if ((dataFileObj.getRecordList() == null) || dataFileObj.getRecordList().isEmpty() ||
        (dataFileObj.getRecordList().size() < 1)) {
      return processErrorResponse("No bird or weight record data provided in file", fileName, fileData);
    }
    String island = convertIslandIdToIslandString(dataFileObj.getIslandNo());
    if ("Error".equalsIgnoreCase(island)) {
      return processErrorResponse(
          "Could not find the islandId in Dusky for islandNo: " + dataFileObj.getIslandNo(),
          fileName, fileData);
    }

    // if a document already exists, need to fetch the NoraNetEntity so we can check for update
    boolean updateMode = true;
    NoraNetCriteria noraNetCriteria = new NoraNetCriteria();
    noraNetCriteria.setIsland(island);
    noraNetCriteria.setStationId(stationId);
    noraNetCriteria.setFileDate(fileDate);
    NoraNetEntity noraNetEntity = new NoraNetEntity();
    PagedResponse<NoraNetEntity> pagedResponse = noranetRepository.findByCriteria(noraNetCriteria);
    if (pagedResponse.getTotal() > 1) {
      boolean duplicated = false;
      for (int i = 0; i < pagedResponse.getResults().size(); i++) {
        NoraNetEntity entity = pagedResponse.getResults().get(i);
        if ("DETECT1".equalsIgnoreCase(dataType)) {
          if (!(entity.getDetectionList() == null && entity.getDetectionList().isEmpty())) {
            duplicated = true;
            break;
          }
        } else if ("STANDARD1".equalsIgnoreCase(dataType)) {
          if (!(entity.getStandardList() == null && entity.getStandardList().isEmpty())) {
            duplicated = true;
            break;
          }
        } else if ("EGG1".equalsIgnoreCase(dataType)) {
          if (!(entity.getEggTimerList() == null && entity.getEggTimerList().isEmpty())) {
            duplicated = true;
            break;
          }
        } else if ("CMSHORT1".equalsIgnoreCase(dataType)) {
          if (!(entity.getCmShortList() == null && entity.getCmShortList().isEmpty())) {
            duplicated = true;
            break;
          }
        } else if ("CMLONG1".equalsIgnoreCase(dataType)) {
          if (!(entity.getCmLongList() == null && entity.getCmLongList().isEmpty())) {
            duplicated = true;
            break;
          }
        } else if ("WEIGHT1".equalsIgnoreCase(dataType)) {
          if (!(entity.getCmLongList() == null && entity.getCmLongList().isEmpty())) {
            duplicated = true;
            break;
          }
        }
      }
      if (duplicated) {
        return processErrorResponse("Duplicate data found in Dusky", fileName, fileData);
      } else {
        noraNetEntity = pagedResponse.getResults().get(0);
      }
    } else if (pagedResponse.getTotal() == 1) {
      noraNetEntity = pagedResponse.getResults().get(0);
    } else {
      updateMode = false;
      // write the document now, even without detail info, this is to try and prevent duplicates
      // being written to Couchbase if files are being processed very quickly one after the other.
      // It appears to take a couple of seconds to process each file.
      noraNetEntity.setIsland(island);
      noraNetEntity.setStationId(stationId);
      noraNetEntity.setFileDate(dataFileObj.getFileDate());
      noraNetEntity.setDataVersion(dataFileObj.getDataVersion());
      noraNetEntity.setActivityDate(dataFileObj.getHeaderDateTime());
      noraNetEntity.setLocationCode(dataFileObj.getLocationCode());
      Response<NoraNetEntity> response = super.saveNoraNet(noraNetEntity);
      if (response.getModel().getId() != null) {
        Optional<NoraNetEntity> currentEntity = noranetRepository.findById(
            response.getModel().getId());
        if (currentEntity.isPresent()) {
          noraNetEntity = currentEntity.get();
        }
      }
      if (noraNetEntity.getId() == null) {
        return processErrorResponse("Error trying to write document to Dusky", fileName, fileData);
      }
    }

    resultString = convertDataToNoraNetEntity(dataFileObj, noraNetEntity, updateMode);
    if ("Success".equalsIgnoreCase(resultString)) {
      return processSuccessResponse("Successfully processed file", fileName);
    } else {
      return processErrorResponse(resultString, fileName, fileData);
    }
  }

  private String convertDataToNoraNetEntity(NoraNetS3DataFile dataFileObj,
      NoraNetEntity noraNetEntity, boolean updateMode) {

    String island = noraNetEntity.getIsland();

    switch (dataFileObj.getDataType()) {
      case "DETECT1":
        if (updateMode) {
          if (noraNetEntity.getDetectionList() == null
              || noraNetEntity.getDetectionList().isEmpty()) {
            // do nothing
          } else {
            // TODO what about same header info but with different birds
            return "Cannot update existing detection data";
          }
        }
        noraNetEntity.setBatteryVolts(dataFileObj.getBatteryVolts());
        noraNetEntity.setRecordCounts(dataFileObj.getRecordCounts());
        Integer kakapoId;
        boolean isKakapoIdValid;
        Integer pulseCount;
        boolean isPulseCountValid;

        List<NoraNetDetectionEntity> detectionList = new ArrayList<>();
        for (NoraNetS3DataRecord dataFileRecord : dataFileObj.getRecordList()) {
          kakapoId = dataFileRecord.getKakapoId();
          pulseCount = dataFileRecord.getPulseCount();
          isKakapoIdValid = !(kakapoId == null || kakapoId == 0);
          isPulseCountValid = !(pulseCount == null || pulseCount == 0);
          // Only process record data where both KakapoId and the pulseCount are > 0
          if (isKakapoIdValid || isPulseCountValid) {
            NoraNetDetectionEntity detectionEntity = new NoraNetDetectionEntity();
            detectionEntity.setUhfId(kakapoId);
            detectionEntity.setBirdList(convertKakapoIdToBirdId(kakapoId, island));
            if (!NULLCHARVALUE.equalsIgnoreCase(dataFileRecord.getCategory())) {
              detectionEntity.setCategory(toCategoryDescription(dataFileRecord.getCategory()));
            }
            detectionEntity.setPulseCount(pulseCount);
            detectionEntity.setPeakTwitch(dataFileRecord.getPeakTwitch());
            if ((!"D".equalsIgnoreCase(dataFileRecord.getCategory()))
                && dataFileRecord.getActivity() != null) {
              detectionEntity.setActivity(convertActivityHours(dataFileRecord.getActivity()));
            }
            detectionList.add(detectionEntity);
          }
        }
        if (detectionList.isEmpty()) {
          return "Could not create a detectionList";
        } else {
          detectionList.sort(
              Comparator.comparing(detect -> findBirdName(detect.getBirdList())));
          noraNetEntity.setDetectionList(detectionList);
        }
        break;

      case "STANDARD1":
        if (updateMode) {
          if (noraNetEntity.getStandardList() == null
              || noraNetEntity.getStandardList().isEmpty()) {
            // do nothing
          } else {
            return "Cannot update existing standard data";
          }
        }
        List<NoraNetStandardEntity> standardList = new ArrayList<>();
        for (NoraNetS3DataRecord dataFileRecord : dataFileObj.getRecordList()) {
          // Only process record data where KakapoId, activity and batteryLife are > 0
          if (isActivityDataViable(dataFileRecord.getKakapoId(), dataFileRecord.getActivity(),
              dataFileRecord.getBatteryLife())) {
            NoraNetStandardEntity standardEntity = new NoraNetStandardEntity();
            standardEntity.setUhfId(dataFileRecord.getKakapoId());
            standardEntity.setBirdList(convertKakapoIdToBirdId(dataFileRecord.getKakapoId(),
                island));
            if (dataFileRecord.getActivity() != null) {
              standardEntity.setActivity(convertActivityHours(dataFileRecord.getActivity()));
            }
            standardEntity.setBatteryLife(dataFileRecord.getBatteryLife());
            standardList.add(standardEntity);
          }
        }
        if (standardList.isEmpty()) {
          return "Could not create a standardList";
        } else {
          standardList.sort(
              Comparator.comparing(standard -> findBirdName(standard.getBirdList())));
          noraNetEntity.setStandardList(standardList);
        }
        break;

      case "EGG1":
        if (updateMode) {
          if (noraNetEntity.getEggTimerList() == null
              || noraNetEntity.getEggTimerList().isEmpty()) {
            // do nothing
          } else {
            return "Cannot update existing egg timer data";
          }
        }
        List<NoraNetEggTimerEntity> eggTimerList = new ArrayList<>();
        for (NoraNetS3DataRecord dataFileRecord : dataFileObj.getRecordList()) {
          // Only process record data where KakapoId, activity and batteryLife are > 0
          if (isActivityDataViable(dataFileRecord.getKakapoId(), dataFileRecord.getActivity(),
              dataFileRecord.getBatteryLife())) {
            NoraNetEggTimerEntity eggTimerEntity = new NoraNetEggTimerEntity();
            eggTimerEntity.setUhfId(dataFileRecord.getKakapoId());
            eggTimerEntity.setBirdList(
                convertKakapoIdToBirdId(dataFileRecord.getKakapoId(), island));
            if (dataFileRecord.getActivity() != null) {
              eggTimerEntity.setActivity(convertActivityHours(dataFileRecord.getActivity()));
            }
            eggTimerEntity.setBatteryLife(dataFileRecord.getBatteryLife());
            eggTimerEntity.setIncubating(dataFileRecord.getIncubatingFlag());
            eggTimerEntity.setDaysSinceChange(dataFileRecord.getDaysSinceChange());
            eggTimerList.add(eggTimerEntity);
          }
        }
        if (eggTimerList.isEmpty()) {
          return "Could not create an eggTimerList";
        } else {
          eggTimerList.sort(
              Comparator.comparing(eggTimer -> findBirdName(eggTimer.getBirdList())));
          noraNetEntity.setEggTimerList(eggTimerList);
        }
        break;

      case "CMSHORT1":
        if (updateMode) {
          if (noraNetEntity.getCmShortList() == null || noraNetEntity.getCmShortList().isEmpty()) {
            // do nothing
          } else {
            return "Cannot update existing checkmate short data";
          }
        }
        List<NoraNetCmShortEntity> cmShortList = new ArrayList<>();
        for (NoraNetS3DataRecord dataFileRecord : dataFileObj.getRecordList()) {
          if (!((dataFileRecord.getKakapoId() == null || dataFileRecord.getKakapoId() == 0)
              && (dataFileRecord.getActivity() == null || dataFileRecord.getActivity() == 0)
              && (dataFileRecord.getBatteryLife() == null
              || dataFileRecord.getBatteryLife() == 0))) {
            NoraNetCmShortEntity cmShortEntity = new NoraNetCmShortEntity();
            cmShortEntity.setUhfId(dataFileRecord.getKakapoId());
            cmShortEntity.setBirdList(
                convertKakapoIdToBirdId(dataFileRecord.getKakapoId(), island));
            if (dataFileRecord.getActivity() != null) {
              cmShortEntity.setActivity(convertActivityHours(dataFileRecord.getActivity()));
            }
            cmShortEntity.setBatteryLife(dataFileRecord.getBatteryLife());
            cmShortEntity.setMatingAge(dataFileRecord.getMatingAge());
            cmShortEntity.setCmHour(dataFileRecord.getCmHour());
            cmShortEntity.setCmMinute(dataFileRecord.getCmMinute());
            cmShortList.add(cmShortEntity);
          }
        }
        if (cmShortList.isEmpty()) {
          return "Could not create a cmShortList";
        } else {
          cmShortList.sort(
              Comparator.comparing(cm -> findBirdName(cm.getBirdList())));
          noraNetEntity.setCmShortList(cmShortList);
        }
        break;

      case "CMLONG1":
        if (updateMode) {
          if (noraNetEntity.getCmLongList() == null || noraNetEntity.getCmLongList().isEmpty()) {
            // do nothing
          } else {
            return "Cannot update existing checkmate Long data";
          }
        }
        List<NoraNetCmLongEntity> cmLongList = new ArrayList<>();
        for (NoraNetS3DataRecord dataFileRecord : dataFileObj.getRecordList()) {
          NoraNetCmLongEntity cmLongEntity = new NoraNetCmLongEntity();
          cmLongEntity.setUhfId(dataFileRecord.getKakapoId());
          cmLongEntity.setBirdList(convertKakapoIdToBirdId(dataFileRecord.getKakapoId(), island));
          cmLongEntity.setMatingAge(dataFileRecord.getMatingAge());
          cmLongEntity.setCmHour(dataFileRecord.getCmHour());
          cmLongEntity.setCmMinute(dataFileRecord.getCmMinute());
          cmLongEntity.setLastCmHour(dataFileRecord.getLastCmHour());
          cmLongEntity.setLastCmMinute(dataFileRecord.getLastCmMinute());
          List<NoraNetCmFemaleEntity> cmFemaleList = new ArrayList<>();
          NoraNetCmFemaleEntity cmFemaleEntity = createCmFemaleEntity(dataFileRecord.getFemaleId1(),
              dataFileRecord.getStartTimeHoursAgo1(),
              dataFileRecord.getDuration1(),
              dataFileRecord.getQuality1(),
              island);
          if (!Objects.isNull(cmFemaleEntity)) {
            cmFemaleList.add(cmFemaleEntity);
          }
          cmFemaleEntity = createCmFemaleEntity(dataFileRecord.getFemaleId2(),
              dataFileRecord.getStartTimeHoursAgo2(),
              dataFileRecord.getDuration2(),
              dataFileRecord.getQuality2(),
              island);
          if (!Objects.isNull(cmFemaleEntity)) {
            cmFemaleList.add(cmFemaleEntity);
          }
          cmFemaleEntity = createCmFemaleEntity(dataFileRecord.getFemaleId3(),
              dataFileRecord.getStartTimeHoursAgo3(),
              dataFileRecord.getDuration3(),
              dataFileRecord.getQuality3(),
              island);
          if (!Objects.isNull(cmFemaleEntity)) {
            cmFemaleList.add(cmFemaleEntity);
          }
          cmFemaleEntity = createCmFemaleEntity(dataFileRecord.getFemaleId4(),
              dataFileRecord.getStartTimeHoursAgo4(),
              dataFileRecord.getDuration4(),
              dataFileRecord.getQuality4(),
              island);
          if (!Objects.isNull(cmFemaleEntity)) {
            cmFemaleList.add(cmFemaleEntity);
          }
          if (cmFemaleList.size() > 0) {
            cmFemaleList.sort(
                Comparator.comparing(cmf -> findBirdName(cmf.getBirdList())));
            cmLongEntity.setCmFemaleList(cmFemaleList);
          }
          cmLongList.add(cmLongEntity);
        }
        if (cmLongList.isEmpty()) {
          return "Could not create a cmLongList";
        } else {
          cmLongList.sort(
              Comparator.comparing(cm -> findBirdName(cm.getBirdList())));
          noraNetEntity.setCmLongList(cmLongList);
        }
        break;

      case "WEIGHT1":
        if (updateMode) {
          if (noraNetEntity.getWeightList() == null
              || noraNetEntity.getWeightList().isEmpty()) {
            // do nothing
          } else {
            return "Cannot update existing weight data";
          }
        }
        List<NoraNetWeightEntity> weightList = new ArrayList<>();
        for (NoraNetS3DataRecord dataFileRecord : dataFileObj.getRecordList()) {
          // Only process record data where all 3 values are present and weight and count are not 0
          if (isWeightDataViable(dataFileRecord.getWeightBin(), dataFileRecord.getBinCount(),
              dataFileRecord.getMaxQuality())) {
            NoraNetWeightEntity weightEntity = new NoraNetWeightEntity();
            weightEntity.setWeightBin(dataFileRecord.getWeightBin());
            weightEntity.setBinCount(dataFileRecord.getBinCount());
            weightEntity.setMaxQuality(dataFileRecord.getMaxQuality());
            weightList.add(weightEntity);
          }
        }
        if (weightList.isEmpty()) {
          return "Could not create a weightList";
        } else {
          noraNetEntity.setWeightList(weightList);
        }
        break;

      default:
        // Not possible
    }
    Response<NoraNetEntity> response = super.saveNoraNet(noraNetEntity);
    return "Success";
  }

  private String convertIslandIdToIslandString(Integer islandNo) {
    IslandCriteria islandCriteria = new IslandCriteria();
    islandCriteria.setIslandId(islandNo);
    IslandEntity islandEntity = new IslandEntity();
    PagedResponse<IslandEntity> pagedResponse = islandRepository.findByCriteria(islandCriteria);
    if (pagedResponse.getTotal() > 0) {
      islandEntity = pagedResponse.getResults().get(0);
    }
    if (islandEntity == null || islandEntity.getName() == null) {
      return "Error";
    }
    return islandEntity.getName();
  }

  private List<NoraNetBirdEntity> convertKakapoIdToBirdId(Integer KakapoId, String island) {
    if (KakapoId == null) {
      return null;
    }
    IdSearchCriteria idSearchCriteria = new IdSearchCriteria();
    idSearchCriteria.setUhfId(KakapoId);
    idSearchCriteria.setIsland(island);
    idSearchCriteria.setLatestOnly(true);
    List<IdSearchDTO> isSearchResults = commonRepository.findIdSearchByCriteria(idSearchCriteria)
        .getResults();
    if (isSearchResults == null || isSearchResults.size() < 1) {
      // no match found
      return null;
    } else {
      List<NoraNetBirdEntity> birdList = new ArrayList<>();
      for (IdSearchDTO bird : isSearchResults) {
        if (bird != null && bird.getBirdId() != null) {
          NoraNetBirdEntity nnBird = new NoraNetBirdEntity();
          nnBird.setBirdID(bird.getBirdId());
          birdList.add(nnBird);
        }
      }
      return birdList;
    }
  }

  private boolean checkStationAgainstSnarkInfo(String station, String typeOfSnark,
      Integer iDofSnark) {
    return station.equals(typeOfSnark + iDofSnark.toString());
  }

  private Float convertActivityHours(Float activity) {
    if (activity == 0) {
      return activity;
    }
    double activityDouble = activity.doubleValue();
    BigDecimal activityDec = new BigDecimal(activityDouble / 6).setScale(1, RoundingMode.HALF_UP);
    return activityDec.floatValue();
  }

  private String toCategoryDescription(String category) {
    return CATEGORY_CODE_TO_DESCRIPTION.get(category);
  }

  private String convertToStationId(String typeOfSnark, Integer idOfSnark) {
    return STATION_TO_FULL_NAME.get(typeOfSnark) + " " + idOfSnark.toString();
  }

  private String findBirdName(List<NoraNetBirdEntity> birdList) {
    if (birdList == null || birdList.get(0) == null || birdList.get(0).getBirdID() == null) {
      return "";
    } else {
      return birdService.findById(birdList.get(0).getBirdID()).getBirdName();
    }
  }

  public boolean isActivityDataViable(Integer kakapoId, Float activity, Integer batteryLife) {
    boolean isKakapoIdValid = !(kakapoId == null || kakapoId == 0);
    boolean isActivityValid = !(activity == null || activity == 0);
    boolean isBatteryLifeValid = !(batteryLife == null || batteryLife == 0);

    return isKakapoIdValid || isActivityValid || isBatteryLifeValid;
  }

  public boolean isWeightDataViable(Integer weightBin, Integer binCount, Integer maxQuality) {
    boolean isWeightBinValid = !(weightBin == null || weightBin == 0);
    boolean isBinCountValid = !(binCount == null || binCount == 0);
    boolean isMaxQualityValid = !(maxQuality == null);

    return isWeightBinValid && isBinCountValid && isMaxQualityValid;
  }

  private NoraNetCmFemaleEntity createCmFemaleEntity(Integer femaleId, Integer startTimeHoursAgo,
      Integer duration, Integer quality, String island) {
    if (femaleId != null) {
      if (startTimeHoursAgo == 0 && duration == 0 && quality == 0) {
        return null;
      } else {
        // only create an entity if all 3 data values are not 0
        return new NoraNetCmFemaleEntity(
            convertKakapoIdToBirdId(femaleId, island), femaleId, startTimeHoursAgo, duration,
            quality);
      }
    }
    return null;
  }

  public ResponseEntity<String> processErrorResponse(String errorMessage, String fileName,
      String fileData) {
    writeErrorResponse(errorMessage, fileName, fileData);
    HttpHeaders responseHeaders = new HttpHeaders();
    NoraNetResponse error = new NoraNetResponse(HttpStatus.NOT_ACCEPTABLE.value(),
        "error", fileName, errorMessage);
    String responseStr = jsonUtils.toJsonPretty(error);
    return new ResponseEntity<>(responseStr, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
  }

  private void writeErrorResponse(String errorMessage, String fileName, String fileData) {
    NoraNetErrorEntity errorEntity = new NoraNetErrorEntity();
    errorEntity.setFileName(fileName);
    errorEntity.setFileData(fileData);
    errorEntity.setDateProcessed(new Date());
    errorEntity.setMessage(errorMessage);
    Response<NoraNetErrorEntity> e = noranetErrorService.save(errorEntity);
  }

  public ResponseEntity<String> processSuccessResponse(String successMessage, String fileName) {
    HttpHeaders responseHeaders = new HttpHeaders();
    NoraNetResponse success = new NoraNetResponse(HttpStatus.ACCEPTED.value(),
        "success", fileName, successMessage);
    String responseStr = jsonUtils.toJsonPretty(success);
    return new ResponseEntity<>(responseStr, responseHeaders, HttpStatus.ACCEPTED);
  }

  public PagedResponse<NoraNetSearchDTO> findSearchDTOByCriteria(NoraNetCriteria criteria) {
    return repository.findSearchDTOByCriteria(criteria);
  }

  public List<NoraNetSearchUndetectedDTO> findUndetectedDTOByCriteria(NoraNetCriteria criteria) {
    return repository.findUndetectedDTOByCriteria(criteria);
  }

  public PagedResponse<NoraNetSearchStationDTO> findStationDTOByCriteria(NoraNetCriteria criteria) {
    return repository.findStationDTOByCriteria(criteria);
  }

  public PagedResponse<NoraNetSearchSnarkDTO> findSnarkDTOByCriteria(NoraNetCriteria criteria) {
    return repository.findSnarkDTOByCriteria(criteria);
  }

  public NoraNetDetectionEntity findDetectedById(String docId, Integer uhfId) {
    return repository.findDetectedById(docId, uhfId);
  }

  @Override
  public DeleteByIdCheckDTO deleteByIdCheck(String docId) {
    DeleteByIdCheckDTO result = new DeleteByIdCheckDTO();
    result.setId(docId);
    // we can't delete something that does not exist
    result.setDeleteOk(docId != null && repository.existsById(docId));
    return result;
  }

  /**
   * Overriding method because super class can't call the DTO search method
   * "findSearchDTOByCriteria()" yet. This could evolve to something better, but this was the
   * quickest simplest solution at the time.
   *
   * @param criteria
   * @param response
   */
  @Override
  public void export(NoraNetCriteria criteria, HttpServletResponse response) {
    criteria.setPageSize(exportUtils.getMaxRows());
    criteria.setPageNumber(1);
    PagedResponse<NoraNetSearchDTO> pr = this.findSearchDTOByCriteria(criteria);

    List<String> header = new ArrayList(Arrays.asList(
        "Island", "Activity Date", "Station Id", "Battery Volts", "Detection Type", "UHFID",
        "bird(s)", "Category", "Pulse Count", "Peak Twitch", "Activity",
        "Battery Life", "Incubating", "Days Since Change", "Mating Age", "Checkmate Hour",
        "Checkmate Minute", "Last Checkmate Hour", "Last Checkmate Minute",
        "Checkmate Female UHFID", "Checkmate Female Bird(s)",
        "Checkmate Female Start time Hours Ago", "Checkmate Female Duration",
        "Checkmate Female Quality"));
    List<String> props = new ArrayList(Arrays.asList(
        "island", "activityDate", "stationId", "batteryVolts", "dataType", "uhfId", "birdList",
        "category", "pulseCount", "peakTwitch", "activity", "batteryLife", "incubating",
        "daysSinceChange", "matingAge", "cmHour", "cmMinute", "lastCmHour", "lastCmMinute",
        "femaleUhfId", "femaleBirdList", "femaleStartTimeHoursAgo",
        "femaleDuration", "femaleQuality"));

    List<Map<String, Object>> records = new LinkedList<>();

    for (NoraNetSearchDTO nn : pr.getResults()) {
      Map<String, Object> row = new HashMap<>();
      row.put("island", nn.getIsland());
      row.put("activityDate", nn.getActivityDate());
      row.put("stationId", nn.getStationId());
      row.put("batteryVolts", nn.getBatteryVolts());
      row.put("dataType", nn.getDataType());
      row.put("uhfId", nn.getUhfId());
      if (nn.getBirdList() == null || nn.getBirdList().size() == 0) {
        row.put("birdList", null);
      } else {
        String birdList = null;
        for (Bird bird : nn.getBirdList()) {
          if (bird.getBirdName() != null) {
            if (birdList == null) {
              birdList = bird.getBirdName();
            } else {
              birdList = birdList + ", " + bird.getBirdName();
            }
          }
        }
        row.put("birdList", birdList);
      }
      row.put("category", nn.getCategory());
      row.put("pulseCount", nn.getPulseCount());
      row.put("peakTwitch", nn.getPeakTwitch());
      row.put("activity", nn.getActivity());
      row.put("batteryLife", nn.getBatteryLife());
      row.put("incubating", nn.getIncubating());
      row.put("daysSinceChange", nn.getDaysSinceChange());
      row.put("matingAge", nn.getMatingAge());
      row.put("cmHour", nn.getCmHour());
      row.put("cmMinute", nn.getCmMinute());
      row.put("lastCmHour", nn.getLastCmHour());
      row.put("lastCmMinute", nn.getLastCmMinute());
      if (nn.getCmFemaleList().size() == 0) {
        records.add(row);
      } else {
        for (Female fem : nn.getCmFemaleList()) {
          // create fresh duplicate row of unnested data to add female data to end of
          Map<String, Object> rowFemale = new HashMap<>(row);
          rowFemale.put("femaleUhfId", fem.getUhfId());
          if (fem.getBirdList() == null || fem.getBirdList().size() == 0) {
            rowFemale.put("femaleBirdList", "");
          } else {
            String birdList = null;
            for (Bird bird : fem.getBirdList()) {
              if (bird.getBirdID() != null) {
                // Need to find the name as not provided in searchDTO
                String birdName = birdService.findById(bird.getBirdID()).getBirdName();
                if (birdList == null) {
                  birdList = birdName;
                } else {
                  birdList = birdList + ", " + birdName;
                }
              }
            }
            rowFemale.put("femaleBirdList", birdList);
          }
          rowFemale.put("femaleStartTimeHoursAgo", fem.getStartTimeHoursAgo());
          rowFemale.put("femaleDuration", fem.getDuration());
          rowFemale.put("femaleQuality", fem.getQuality());
          records.add(rowFemale);
        }
      }
    }
    exportUtils.export(response, records, header, props, "NoraNet");
  }

}

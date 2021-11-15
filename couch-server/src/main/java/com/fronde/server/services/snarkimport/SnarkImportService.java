package com.fronde.server.services.snarkimport;

import com.fronde.server.domain.LocationEntity;
import com.fronde.server.domain.ObserverEntity;
import com.fronde.server.domain.PersonEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.domain.SnarkActivityEntity;
import com.fronde.server.domain.SnarkDataEntity;
import com.fronde.server.domain.SnarkImportEntity;
import com.fronde.server.domain.SnarkRecordEntity;
import com.fronde.server.domain.WeightEntity;
import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.bird.BirdService;
import com.fronde.server.services.location.LocationService;
import com.fronde.server.services.person.PersonService;
import com.fronde.server.services.record.RecordService;
import com.fronde.server.services.snarkactivity.SnarkActivityService;
import com.fronde.server.services.snarkimport.processor.SnarkFileProcessResult;
import com.fronde.server.services.snarkimport.processor.SnarkFileProcessor;
import com.fronde.server.services.snarkimport.processor.SnarkFileProcessorFactory;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SnarkImportService extends SnarkImportBaseService {

  private static final Map<String, String> ACTIVITY_TYPE_TO_REASON;
  private static final String RECORD_TYPE = "Snark";
  private static final String BIRD_CERT = "Definitely";
  private static final String OBSERVATION_ROLE = "Recorder";
  private static final String WEIGHT_METHOD = "Auto";

  static {
    ACTIVITY_TYPE_TO_REASON = new HashMap<>();
    ACTIVITY_TYPE_TO_REASON.put("Hopper", "Snark at hopper");
    ACTIVITY_TYPE_TO_REASON.put("Nest", "Snark at nest");
    ACTIVITY_TYPE_TO_REASON.put("Roost", "Snark at roost");
    ACTIVITY_TYPE_TO_REASON.put("Track and Bowl", "Snark at T&B");
  }

  @Value("${snarkimport.saveSnarkFileContent:true}")
  private final boolean saveSnarkFileContent = true;
  @Autowired
  private SnarkActivityService snarkActivityService;
  @Autowired
  private RecordService recordService;
  @Autowired
  private LocationService locationService;
  @Autowired
  private PersonService personService;
  @Autowired
  private BirdService birdService;
  @Autowired
  private SnarkFileProcessorFactory processorFactory;

  private SnarkFileProcessor createProcessor(Integer qualityOverride, boolean showLockRecords) {
    return processorFactory.createProcessor(new BirdIdConverterImpl(birdService), qualityOverride);
  }

  private SnarkFileProcessor createProcessor() {
    return processorFactory.createProcessor(new BirdIdConverterImpl(birdService));
  }

  /**
   * Process the chosen snark records prior to Snark Import
   *
   * @param snarkProcessRequest snark records list chosen by user to be processed
   * @return a list of processed snark records which are ready to be imported
   */
  public List<EveningDTO> includeSelectedRecords(SnarkProcessRequest snarkProcessRequest) {
    SnarkFileProcessor processor = createProcessor();
    List<EveningDTO> snarkRecordsByEvening = snarkProcessRequest.getEveningList();
    List<EveningDTO> eveningDTOList = new ArrayList<>();

    for (EveningDTO eveningDTO : snarkRecordsByEvening) {
      List<SnarkRecordEntity> selectedRecordList = new ArrayList<>();
      Date evening = eveningDTO.getDate();

      for (SnarkRecordEntity record : eveningDTO.getSnarkRecordList()) {
        if (record.isInclude()) {
          selectedRecordList.add(record);
        }
      }

      if (!selectedRecordList.isEmpty()) {
        eveningDTOList.add(processor.processSelectedRecords(evening, selectedRecordList));
      }
    }

    return eveningDTOList;
  }

  public Response<SnarkImportEntity> importSnark(SnarkImportRequest snarkImportRequest) {
    SnarkImportEntity entity = snarkImportRequest.getEntity();
    Date importDate = new Date();
    byte[] data = Base64.getDecoder().decode(entity.getSnarkFileContent());
    entity.setSnarkFileHash(hash(data));
    // load the location
    LocationEntity l = locationService.findById(entity.getLocationID());
    // load the observer
    PersonEntity p = personService.findById(entity.getObserverPersonID());

    List<EveningDTO> snarkRecordsByEvening = snarkImportRequest.getEveningList();
    for (EveningDTO eveningDTO : snarkRecordsByEvening) {
      List<SnarkRecordEntity> snarkRecords = eveningDTO.getSnarkRecordList();
      Date date = eveningDTO.getDate();
      SnarkActivityEntity sa = new SnarkActivityEntity();
      sa.setSnarkRecordList(snarkRecords);
      sa.setObserverPersonID(entity.getObserverPersonID());
      sa.setLocationID(entity.getLocationID());
      sa.setActivityType(entity.getActivityType());
      sa.setDate(date);
      snarkActivityService.saveWithThrow(sa);

      for (SnarkRecordEntity sr : snarkRecords) {
        if (sr.getBirdID() != null) {
          RecordEntity r = new RecordEntity();
          r.setBirdID(sr.getBirdID());
          r.setComments(sr.getComments());
          r.setRecordType(RECORD_TYPE);
          r.setIsland(l.getIsland());
          r.setDateTime(sr.getArriveDateTime());
          r.setActivity(sr.getActivity());
          r.setReason(toReason(entity.getActivityType()));
          r.setSubReason(null);
          r.setEntryDateTime(importDate);
          r.setBirdCert(BIRD_CERT);
          r.setSignificantEvent(false);
          r.setLocationID(entity.getLocationID());
          r.setNorthing(l.getNorthing());
          r.setEasting(l.getEasting());
          r.setMappingMethod(l.getMappingMethod());
          ObserverEntity o = new ObserverEntity();
          o.setObservationRoles(Collections.singletonList(OBSERVATION_ROLE));
          o.setObserverCapacity(p.getCurrentCapacity());
          o.setPersonID(entity.getObserverPersonID());
          r.setObserverList(Collections.singletonList(o));

          if (sr.getWeight() != null) {
            WeightEntity w = new WeightEntity();
            w.setMethod(WEIGHT_METHOD);
            w.setWeight(sr.getWeight());
            r.setWeight(w);
          }

          SnarkDataEntity sd = new SnarkDataEntity();
          sd.setArriveDateTime(sr.getArriveDateTime());
          sd.setDepartDateTime(sr.getDepartDateTime());
          // this will be false to start with
          sd.setMating(sr.getMating());
          sd.setSnarkActivityID(sa.getId());
          r.setSnarkData(sd);
          recordService.saveWithThrow(r);
          sr.setRecordID(r.getId());
        }
      }
      // save again to set ids for the snark records
      snarkActivityService.saveWithThrow(sa);
    }
    if (entity.getIsland() == null) {
      entity.setIsland(l.getIsland());
    }
    return save(entity);
  }

  private String toReason(String activityType) {
    return ACTIVITY_TYPE_TO_REASON.get(activityType);
  }

  public SnarkCheckResultDTO checkSnark(SnarkCheckRequestDTO request) {
    SnarkCheckResultDTO resultDTO = new SnarkCheckResultDTO();
    byte[] data = Base64.getDecoder().decode(request.getSnarkFileContent());
    String hash = hash(data);

    // When doing the snark check we don't need (or want) to supply the birdChannelMap so the user gets a fresh
    // look at all of the various multiple birds for one channel scenarios.
    SnarkFileProcessor processor = createProcessor(request.getQualityOverride(),
        request.isShowLockRecords());
    LocationEntity location = locationService.findById(request.getLocationID());
    String island = Optional.ofNullable(location.getIsland()).orElseThrow();
    SnarkFileProcessResult result = processor.processFile(island, data,
        request.isShowLockRecords());
    result.setShowLockRecords(request.isShowLockRecords());

    List<Date> eveningDates = new ArrayList<>(result.getSnarkRecordsByEvening().keySet());
    Collections.sort(eveningDates);
    for (Date date : eveningDates) {
      EveningDTO evening = new EveningDTO();
      evening.setDate(date);
      evening.setSnarkRecordList(result.getSnarkRecordsByEvening().get(date));
      resultDTO.getEveningList().add(evening);

      if (!resultDTO.isSomeBirdsNotFound()) {
        for (SnarkRecordEntity sr : result.getSnarkRecordsByEvening().get(date)) {
          // if the UHF ID does not reconcile with a bird on the Island
          if (!processor.isUniqueBirdFound(sr.getBirdID())) {
            resultDTO.setSomeBirdsNotFound(true);
            break;
          }
        }
      }
    }

    resultDTO.setMysteryWeightList(result.getMysteryWeightList());
    resultDTO.setExistingImport(!repository.findBySnarkFileHash(hash).isEmpty());
    resultDTO.setIsland(island);
    resultDTO.setShowLockRecords(request.isShowLockRecords());
    return resultDTO;
  }

  private String hash(byte[] data) {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException();
    }
    // not sure it matters what character set we use here because we are only outputting chars in ascii range
    return new String(Base64.getEncoder().encode(md.digest(data)), StandardCharsets.ISO_8859_1);
  }

  @Override
  public DeleteByIdCheckDTO deleteByIdCheck(String docId) {
    DeleteByIdCheckDTO result = new DeleteByIdCheckDTO();
    result.setId(docId);
    // snark imports can always be deleted, just that some deletes won't revert creation of other records
    // we can't delete something that doesn't exist
    result.setDeleteOk(docId != null);
    return result;
  }

  @Override
  public Response<SnarkImportEntity> save(SnarkImportEntity entity) {
    if (!saveSnarkFileContent) {
      // don't save the snarkFileContent field as it can get quite large
      entity.setSnarkFileContent(null);
    }
    return super.save(entity);
  }
}

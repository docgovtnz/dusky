package com.fronde.server.services.record;

import com.fronde.server.domain.BatteryLifeEntity;
import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.BloodDetailEntity;
import com.fronde.server.domain.BloodSampleDetailEntity;
import com.fronde.server.domain.BloodSampleEntity;
import com.fronde.server.domain.CheckmateDataEntity;
import com.fronde.server.domain.CheckmateEntity;
import com.fronde.server.domain.EggTimerEntity;
import com.fronde.server.domain.OtherDetailEntity;
import com.fronde.server.domain.OtherSampleEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.domain.SampleEntity;
import com.fronde.server.domain.SpermDetailEntity;
import com.fronde.server.domain.SpermSampleEntity;
import com.fronde.server.domain.StandardEntity;
import com.fronde.server.domain.SwabDetailEntity;
import com.fronde.server.domain.SwabSampleEntity;
import com.fronde.server.domain.TransmitterChangeEntity;
import com.fronde.server.domain.TransmitterEntity;
import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.bird.BirdService;
import com.fronde.server.services.nestobservation.NestObservationService;
import com.fronde.server.services.options.BandsAndChipsDTO;
import com.fronde.server.services.options.CurrentTransmitterInfoDTO;
import com.fronde.server.services.options.TransmitterBirdHistoryDTO2;
import com.fronde.server.services.sample.SampleService;
import com.fronde.server.services.transmitter.TransmitterService;
import com.fronde.server.services.validation.ValidationMessage;
import com.fronde.server.utils.ServiceUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class RecordService extends RecordBaseService {

  @Autowired
  private BirdService birdService;

  @Autowired
  private NestObservationService nestObservationService;

  @Autowired
  private TransmitterService transmitterService;

  @Autowired
  private SampleService sampleService;

  @Override
  public DeleteByIdCheckDTO deleteByIdCheck(String docId) {
    DeleteByIdCheckDTO result = new DeleteByIdCheckDTO();
    result.setId(docId);
    // records can almost always be deleted, just that some deletes won't revert changes to other entities
    boolean deleteOk = true;

    // we can't delete something that doesn't exist
    deleteOk = deleteOk && docId != null;

    // the rare case of the transmitter change, which can't always be deleted because doing so will cause irreversible data integrity issues for record and transmitter
    RecordEntity previous = this.findById(docId);
    if (deleteOk && previous.getTransmitterChange() != null) {
      TransmitterEntity previousFromT = getFromTransmitter(previous);
      TransmitterEntity previousToT = getToTransmitter(previous);
      Response<RecordEntity> response = preTransmitterChangeSave(null, null, null, previous,
          previousFromT, previousToT, null);
      deleteOk =
          deleteOk && (response == null || (response != null && response.getMessages().isEmpty()));
    }

    // check there are no associated samples
    BloodSampleDetailEntity bloodSampleDetail = previous.getBloodSampleDetail();
    if (deleteOk && bloodSampleDetail != null && bloodSampleDetail.getBloodSampleList() != null
        && !bloodSampleDetail.getBloodSampleList().isEmpty()) {
      deleteOk = false;
    }
    if (deleteOk && previous.getSwabSampleList() != null && !previous.getSwabSampleList()
        .isEmpty()) {
      deleteOk = false;
    }
    if (deleteOk && previous.getOtherSampleList() != null && !previous.getOtherSampleList()
        .isEmpty()) {
      deleteOk = false;
    }
    if (deleteOk && previous.getSpermSampleList() != null && !previous.getSpermSampleList()
        .isEmpty()) {
      deleteOk = false;
    }

    // Check for related Nest Observations.
    if (nestObservationService.hasRelatedNestObservation(docId)) {
      deleteOk = false;
    }

    result.setDeleteOk(deleteOk);
    return result;
  }

  public PagedResponse<RecordSearchDTO> findSearchDTOByCriteria(RecordCriteria criteria) {
    return repository.findSearchDTOByCriteria(criteria);
  }

  public BandsAndChipsDTO findIdInfoByBirdId(String birdId) {
    return repository.findIdInfoByBirdId(birdId);
  }

  public List<BandsAndChipsDTO> findBandHistoryByBirdId(String birdId) {
    return repository.findBandHistoryByBirdId(birdId);
  }

  public List<BandsAndChipsDTO> findChipHistoryByBirdId(String birdId) {
    return repository.findChipHistoryByBirdId(birdId);
  }

  public CurrentTransmitterInfoDTO findCurrentTransmitterInfoByBirdId(String birdId) {
    return repository.findCurrentTransmitterInfoByBirdId(birdId);
  }

  public DatedMeasureDetailDTO getCurrentMeasureDetailByBirdID(String birdID) {
    return repository.getCurrentMeasureDetailByBirdID(birdID);
  }

  public List<DatedMeasureDetailDTO> getMeasureDetailHistoryByBirdID(String birdID) {
    return repository.getMeasureDetailHistoryByBirdID(birdID);
  }

  public List<TransmitterBirdHistoryDTO2> getTransmitterBirdHistory(String txID) {
    return repository.findTransmitterBirdHistory(txID);
  }

  /**
   * Returns the bird entity of the birdID if birdID has been set, otherwise null
   */
  private BirdEntity getBird(RecordEntity entity) {
    if (entity.getBirdID() != null) {
      return birdService.findById(entity.getBirdID());
    } else {
      return null;
    }
  }

  private TransmitterEntity getAndSetFromTransmitter(RecordEntity entity, RecordEntity previous) {
    TransmitterEntity t = getFromTransmitter(entity);
    if (entity.getTransmitterChange() != null) {
      if ((previous != null && !ObjectUtils
          .nullSafeEquals(entity.getBirdID(), previous.getBirdID())) || previous == null
          || previous.getTransmitterChange() == null) {
        // if the bird has been changed
        // OR this is a new record (previous is null)
        // OR the previous record had no ID change information
        // THEN set to current birds transmitter and set this on the record entity
        t = birdService.getCurrentTransmitter(entity.getBirdID());
        // ct.getDocId() can be null but ct shouldn't be
        if (t != null) {
          entity.getTransmitterChange().setTxFrom(t.getId());
        } else {
          entity.getTransmitterChange().setTxFrom(null);
        }
      }
    }
    return t;
  }

  private TransmitterEntity getFromTransmitter(RecordEntity entity) {
    if (entity.getTransmitterChange() != null
        && entity.getTransmitterChange().getTxFrom() != null) {
      return transmitterService.findById(entity.getTransmitterChange().getTxFrom());
    } else {
      return null;
    }
  }

  private TransmitterEntity getToTransmitter(RecordEntity entity) {
    if (entity.getTransmitterChange() != null && entity.getTransmitterChange().getTxTo() != null) {
      return transmitterService.findById(entity.getTransmitterChange().getTxTo());
    } else {
      return null;
    }
  }

  /**
   * Gets the previous record (assuming the supplied one has not yet been saved)
   * <p>
   * If this is a new record (id is null) then this returns null.
   */
  private RecordEntity getPrevious(RecordEntity entity) {
    if (entity.getId() != null) {
      return findById(entity.getId());
    } else {
      return null;
    }
  }

  @Override
  public Response<RecordEntity> save(RecordEntity entity) {
    return save(entity, true);
  }

  public void saveWithThrow(RecordEntity entity) throws RuntimeException {
    saveWithThrow(entity, true);
  }

  public void saveWithThrow(RecordEntity entity, boolean withSampleUpdate) throws RuntimeException {
    ServiceUtils.throwIfRequired(save(entity, withSampleUpdate), "Record", entity.getId());
  }

  public Response<RecordEntity> save(RecordEntity entity, boolean withSampleUpdate) {
    // load associated entities for the pre save and post save logic
    RecordEntity previous = getPrevious(entity);
    BirdEntity b = getBird(entity);
    TransmitterEntity bt = null;
    // get the birds current transmitter if needed
    if (b != null && entity.getTransferDetail() != null) {
      bt = birdService.getCurrentTransmitter(b.getId());
    }
    TransmitterEntity toT = getToTransmitter(entity);
    // the from transmitter may be derived from the to transmitter
    TransmitterEntity fromT = getAndSetFromTransmitter(entity, previous);
    BirdEntity previousB = null;
    TransmitterEntity previousBt = null;
    TransmitterEntity previousFromT = null;
    TransmitterEntity previousToT = null;
    if (previous != null) {
      previousB = getBird(previous);
      if (previousB != null && previous.getTransferDetail() != null) {
        previousBt = birdService.getCurrentTransmitter(previousB.getId());
      }
      previousFromT = getFromTransmitter(previous);
      previousToT = getToTransmitter(previous);
    }

    // perform pre save logic for a transfer
    if (entity.getTransferDetail() != null) {
      Response<RecordEntity> response = preTransferSave(entity, b, previous, previousB, bt);
      if (response != null) {
        return response;
      }
    }

    // perform pre save logic for a transmitter change
    if (entity.getTransmitterChange() != null || (previous != null
        && previous.getTransmitterChange() != null)) {
      Response<RecordEntity> response = preTransmitterChangeSave(entity, fromT, toT, previous,
          previousFromT, previousToT, b);
      if (response != null) {
        return response;
      }
    }

    // Validate before we process the record.
    List<ValidationMessage> validationMessages = validationService.validateEntity(entity);
    if (validationMessages != null && validationMessages.size() > 0) {
      Response<RecordEntity> response = new Response<>(entity);
      response.setMessages(validationMessages);
      return response;
    }

    // Derive values for checkmate if required.
    if (entity.getCheckmate() != null) {
      Response<RecordEntity> entityResponse = processCheckmate(entity);
      if (entityResponse != null) {
        return entityResponse;
      }
    }

    // Derive values for Egg Timer if required.
    if (entity.getEggTimer() != null) {
      Response<RecordEntity> entityResponse = processEggTimer(entity);
      if (entityResponse != null) {
        return entityResponse;
      }
    }

    // Derive values for Standard Transmitter if required.
    if (entity.getStandard() != null) {
      Response<RecordEntity> entityResponse = processStandard(entity);
      if (entityResponse != null) {
        return entityResponse;
      }
    }

    // perform pre save logic for samples (includes creating samples if they don't already exist and setting sample ids on record)
    if (withSampleUpdate) {
      BloodSampleDetailEntity currentBloodSample = entity.getBloodSampleDetail();
      if (currentBloodSample != null && currentBloodSample.getBloodSampleList().isEmpty()) {
        entity.setBloodSampleDetail(null);
        currentBloodSample = null;
      }

      if (currentBloodSample != null || (previous != null
          && previous.getBloodSampleDetail() != null)) {
        preBloodSampleSave(entity, previous);
      }
      if (entity.getSwabSampleList() != null || (previous != null
          && previous.getSwabSampleList() != null)) {
        preSwabSampleSave(entity, previous);
      }
      if (entity.getOtherSampleList() != null || (previous != null
          && previous.getOtherSampleList() != null)) {
        preOtherSampleSave(entity, previous);
      }
      if (entity.getSpermSampleList() != null || (previous != null
          && previous.getSpermSampleList() != null)) {
        preSpermSampleSave(entity, previous);
      }
    }

    Response<RecordEntity> response = super.save(entity);

    // perform post save logic for a transfer
    if (entity.getTransferDetail() != null) {
      postTransferSave(entity, b, bt, previous, previousB, previousBt);
    }

    // perform post save logic for a transmitter change
    if (entity.getTransmitterChange() != null || (previous != null
        && previous.getTransmitterChange() != null)) {
      postTransmitterChangeSave(entity, fromT, toT, previous, previousFromT, previousToT);
    }

    return response;
  }

  private Response<RecordEntity> processEggTimer(RecordEntity entity) {
    // No Egg Timers, ignore.
    if (entity.getEggTimer() == null) {
      return null;
    }

    // Reset manual flags.
    EggTimerEntity eggTimer = entity.getEggTimer();
    if (!"Manual".equals(entity.getEggTimer().getDataCaptureType())) {
      eggTimer.setActivity2DaysAgo(null);
      eggTimer.setActivity2DaysAgo1(null);
      eggTimer.setActivity2DaysAgo2(null);
      eggTimer.setActivity3DaysAgo(null);
      eggTimer.setActivity3DaysAgo1(null);
      eggTimer.setActivity3DaysAgo2(null);
      eggTimer.setActivity4DaysAgo(null);
      eggTimer.setActivity4DaysAgo1(null);
      eggTimer.setActivity4DaysAgo2(null);
      eggTimer.setActivity5DaysAgo(null);
      eggTimer.setActivity5DaysAgo1(null);
      eggTimer.setActivity5DaysAgo2(null);
      eggTimer.setActivity6DaysAgo(null);
      eggTimer.setActivity6DaysAgo1(null);
      eggTimer.setActivity6DaysAgo2(null);
      eggTimer.setActivity7DaysAgo(null);
      eggTimer.setActivity7DaysAgo1(null);
      eggTimer.setActivity7DaysAgo2(null);
      eggTimer.setActivityYesterday(null);
      eggTimer.setActivityYesterday1(null);
      eggTimer.setActivityYesterday2(null);
      eggTimer.setBatteryLife1(null);
      eggTimer.setBatteryLife2(null);
      eggTimer.setDaysSinceChange(null);
      eggTimer.setDaysSinceChange1(null);
      eggTimer.setDaysSinceChange2(null);
      eggTimer.setDurationOfPrevious(null);
      eggTimer.setDurationOfPrevious1(null);
      eggTimer.setDurationOfPrevious2(null);
      eggTimer.setMeanActivity(null);
      eggTimer.setMeanActivity1(null);
      eggTimer.setMeanActivity2(null);

      entity.setBatteryLife(null);
    }

    // Reset Errol flags.
    if (!"From Errol".equals(entity.getEggTimer().getDataCaptureType())) {
      eggTimer.setErrolActivity1Day(null);
      eggTimer.setErrolActivity7Days(null);
      eggTimer.setErrolDaysSinceChange(null);
      eggTimer.setErrolID(null);
    }

    // Process manually entered data.
    if ("Manual".equals(entity.getEggTimer().getDataCaptureType())) {
      eggTimer.setActivityYesterday(TransmitterHelper
          .deriveValue(eggTimer.getActivityYesterday1(), eggTimer.getActivityYesterday2()));
      eggTimer.setActivity2DaysAgo(TransmitterHelper
          .deriveValue(eggTimer.getActivity2DaysAgo1(), eggTimer.getActivity2DaysAgo2()));
      eggTimer.setActivity3DaysAgo(TransmitterHelper
          .deriveValue(eggTimer.getActivity3DaysAgo1(), eggTimer.getActivity3DaysAgo2()));
      eggTimer.setActivity4DaysAgo(TransmitterHelper
          .deriveValue(eggTimer.getActivity4DaysAgo1(), eggTimer.getActivity4DaysAgo2()));
      eggTimer.setActivity5DaysAgo(TransmitterHelper
          .deriveValue(eggTimer.getActivity5DaysAgo1(), eggTimer.getActivity5DaysAgo2()));
      eggTimer.setActivity6DaysAgo(TransmitterHelper
          .deriveValue(eggTimer.getActivity6DaysAgo1(), eggTimer.getActivity6DaysAgo2()));
      eggTimer.setActivity7DaysAgo(TransmitterHelper
          .deriveValue(eggTimer.getActivity7DaysAgo1(), eggTimer.getActivity7DaysAgo2()));
      eggTimer.setDaysSinceChange(TransmitterHelper
          .deriveValue(eggTimer.getDaysSinceChange1(), eggTimer.getDaysSinceChange2()));
      eggTimer.setDurationOfPrevious(TransmitterHelper
          .deriveValue(eggTimer.getDurationOfPrevious1(), eggTimer.getDurationOfPrevious2()));
      eggTimer.setMeanActivity(
          TransmitterHelper.deriveValue(eggTimer.getMeanActivity1(), eggTimer.getMeanActivity2()));

      Integer batteryWeeks = TransmitterHelper.deriveValue(entity.getEggTimer().getBatteryLife1(),
          entity.getEggTimer().getBatteryLife2());
      BatteryLifeEntity ble = new BatteryLifeEntity();
      ble.setBattLifeWeeks(batteryWeeks);
      entity.setBatteryLife(ble);
    }

    // There should never be validation errors, so return null to let the processing continue.
    return null;
  }

  private Response<RecordEntity> preTransferSave(RecordEntity entity, BirdEntity b,
      RecordEntity previous, BirdEntity previousB, TransmitterEntity bt) {
    // check if we allow this
    // determine if this record is now the latest
    boolean notLatest = false;
    // check both previous and current
    if (previous != null && previous.getTransferDetail() != null) {
      // previous record expected to be valid and have a bird id and date time
      RecordEntity latestExcluding = repository
          .findLatestRecordWithPartByBirdIDExcluding(previous.getBirdID(), "transferDetail",
              previous.getId());
      notLatest = notLatest || (latestExcluding != null && latestExcluding.getDateTime()
          .after(previous.getDateTime()));
    }
    if (entity != null && entity.getTransferDetail() != null) {
      String birdID = entity.getBirdID();
      Date dateTime = entity.getDateTime();
      if (birdID != null && dateTime != null) {
        RecordEntity latestExcluding = repository
            .findLatestRecordWithPartByBirdIDExcluding(birdID, "transferDetail", entity.getId());
        notLatest =
            notLatest || (latestExcluding != null && latestExcluding.getDateTime().after(dateTime));
      }
    }
    // determine if the transmitter change part has changed
    boolean newOrChanged = !(previous != null &&
        entity != null &&
        entity.getTransferDetail() != null &&
        previous.getTransferDetail() != null &&
        ObjectUtils.nullSafeEquals(entity.getTransferDetail().getTransferFromIsland(),
            previous.getTransferDetail().getTransferFromIsland()) &&
        ObjectUtils.nullSafeEquals(entity.getTransferDetail().getTransferToIsland(),
            previous.getTransferDetail().getTransferToIsland()) &&
        ObjectUtils.nullSafeEquals(entity.getTransferDetail().getTransferFromLocationID(),
            previous.getTransferDetail().getTransferFromLocationID()) &&
        ObjectUtils.nullSafeEquals(entity.getTransferDetail().getTransferToLocationID(),
            previous.getTransferDetail().getTransferToLocationID()));
    // return validation message if not latest and this is new or changed
    if (notLatest && newOrChanged) {
      Response<RecordEntity> response = new Response<RecordEntity>();
      ValidationMessage message = new ValidationMessage();
      message.setPropertyName("dateTime");
      message.setMessageText("Cannot create or change past transfer");
      response.setModel(entity);
      response.setMessages(Collections.singletonList(message));
      return response;
    }
    // if the bird has not been changed determine if the from island or from location has been changed (which we don't allow so we can simplify other logic)
    if (previous != null && entity.getBirdID().equals(previous.getBirdID())) {
      List<ValidationMessage> messages = new ArrayList<>();
      if (!ObjectUtils.nullSafeEquals(entity.getTransferDetail().getTransferFromIsland(),
          previous.getTransferDetail().getTransferFromIsland())) {
        ValidationMessage message = new ValidationMessage();
        message.setPropertyName("transferFromIsland");
        message.setMessageText("Cannot change Transfer from Island on an existing transfer");
        messages.add(message);
      }
      if (!ObjectUtils.nullSafeEquals(entity.getTransferDetail().getTransferFromLocationID(),
          previous.getTransferDetail().getTransferFromLocationID())) {
        ValidationMessage message = new ValidationMessage();
        message.setPropertyName("transferFromLocationID");
        message.setMessageText("Cannot change Transfer from Location on an existing transfer");
        messages.add(message);
      }
      if (!messages.isEmpty()) {
        Response<RecordEntity> response = new Response<RecordEntity>();
        response.setMessages(messages);
        response.setModel(entity);
        return response;
      }
    }
    // for a new transfer or a transfer to a different bird, check that the transfer from island and location match the birds current island and location
    // also check the island of the bird matches the island of the birds current transmitter
    if (previous == null || !entity.getBirdID().equals(previous.getBirdID())) {
      List<ValidationMessage> messages = new ArrayList<>();
      if (!ObjectUtils.nullSafeEquals(entity.getTransferDetail().getTransferFromIsland(),
          b.getCurrentIsland())) {
        ValidationMessage message = new ValidationMessage();
        message.setPropertyName("transferFromIsland");
        message.setMessageText("Transfer from Island must match bird's Current Island");
        messages.add(message);
      }
      if (!ObjectUtils.nullSafeEquals(entity.getTransferDetail().getTransferFromLocationID(),
          b.getCurrentLocationID())) {
        ValidationMessage message = new ValidationMessage();
        message.setPropertyName("transferFromLocationID");
        message.setMessageText("Transfer from Location must match bird's Current Location");
        messages.add(message);
      }
      // if the bird has a transmitter, then check the islands match
      if (bt != null && !ObjectUtils.nullSafeEquals(bt.getIsland(), b.getCurrentIsland())) {
        ValidationMessage message = new ValidationMessage();
        message.setPropertyName("birdAndTransmitterIsland");
        message.setMessageText("The island of the bird (" + b.getCurrentIsland()
            + ") and the island of the bird's transmitter (" + bt.getIsland()
            + ") must match before creating a transfer. Update the island of the transmitter (" + bt
            .getTxId() + ") first");
        messages.add(message);
      }
      if (!messages.isEmpty()) {
        Response<RecordEntity> response = new Response<RecordEntity>();
        response.setMessages(messages);
        response.setModel(entity);
        return response;
      }
    }

    return null;
  }

  private void postTransferSave(RecordEntity entity, BirdEntity b, TransmitterEntity bt,
      RecordEntity previous, BirdEntity previousB, TransmitterEntity previousBt) {
    // scenarios to consider
    // 1. new record and from matches
    // 2. existing record, bird was not updated by transfer or updated since, same bird
    // 3. existing record, bird was not updated by transfer or updated since, different bird
    // 4. existing record, bird was updated by transfer, bird not updated since, same bird, same to
    // 5. existing record, bird was updated by transfer, bird not updated since, same bird, different to
    // 6. existing record, bird was updated by transfer, bird not updated since, different bird

    if (previous == null || previous.getTransferDetail() == null) {
      // new record or new transfer detail
      // from island and locationID should match bird due to validation rules in preTransferSave
      // change the birds island and locationID and save
      b.setCurrentIsland(entity.getTransferDetail().getTransferToIsland());
      b.setCurrentLocationID(entity.getTransferDetail().getTransferToLocationID());
      birdService.saveWithThrow(b, entity.getId());
      // scenario 1
      System.out.println("Bird record updated");
      // change the transmitter's island and save
      if (bt != null) {
        bt.setIsland(entity.getTransferDetail().getTransferToIsland());
        transmitterService.saveWithThrow(bt);
        System.out.println("Bird transmitter record updated");
      }
    } else {
      // existing record
      if (previousB.getModifiedByRecordId() == null || !previousB.getModifiedByRecordId()
          .equals(entity.getId())) {
        // existing record, bird was not updated by transfer
        // OR
        // existing record, bird was updated by transfer, bird updated since
        if (previousB.getId().equals(b.getId())) {
          // same bird
          // scenario 2
          System.out.println("Bird record not updated");
          if (bt != null) {
            System.out.println("Bird transmitter record not updated");
          }
        } else {
          // different bird
          System.out.println("Previous bird record not reverted");
          if (previousBt != null) {
            System.out.println("Previous bird transmitter record not reverted");
          }
          // from island and locationID should match bird due to validation rules in preTransferSave
          // change the birds island and locationID and save
          b.setCurrentIsland(entity.getTransferDetail().getTransferToIsland());
          b.setCurrentLocationID(entity.getTransferDetail().getTransferToLocationID());
          birdService.saveWithThrow(b, entity.getId());
          // scenario 3
          System.out.println("Bird record updated");
          // change the transmitter's island and save
          if (bt != null) {
            bt.setIsland(entity.getTransferDetail().getTransferToIsland());
            transmitterService.saveWithThrow(bt);
            System.out.println("Bird transmitter record updated");
          }
        }
      } else if (previousB.getId().equals(b.getId())) {
        // same bird
        if (ObjectUtils.nullSafeEquals(entity.getTransferDetail().getTransferToIsland(),
            previous.getTransferDetail().getTransferToIsland()) &&
            ObjectUtils.nullSafeEquals(entity.getTransferDetail().getTransferToLocationID(),
                previous.getTransferDetail().getTransferToLocationID())) {
          // same island and locationID
          // scenario 4
          System.out.println("Bird record not updated");
          if (bt != null) {
            System.out.println("Bird transmitter record not updated");
          }
        } else {
          // change the birds island and locationID and save
          b.setCurrentIsland(entity.getTransferDetail().getTransferToIsland());
          b.setCurrentLocationID(entity.getTransferDetail().getTransferToLocationID());
          birdService.saveWithThrow(b, entity.getId());
          // scenario 5
          System.out.println("Bird record updated");
          // change the transmitter's island and save
          if (bt != null) {
            bt.setIsland(entity.getTransferDetail().getTransferToIsland());
            transmitterService.saveWithThrow(bt);
            System.out.println("Bird transmitter record updated");
          }
        }
      } else {
        // different bird
        // set the current island and location back to what it was
        previousB.setCurrentIsland(previous.getTransferDetail().getTransferFromIsland());
        previousB.setCurrentLocationID(previous.getTransferDetail().getTransferFromLocationID());
        // save but don't link to record anymore
        birdService.saveWithThrow(previousB, null);
        System.out.println("Previous bird record reverted");
        if (previousBt != null) {
          previousBt.setIsland(previous.getTransferDetail().getTransferFromIsland());
          transmitterService.saveWithThrow(previousBt);
          System.out.println("Previous bird transmitter record reverted");
        }
        // from island and locationID should match bird due to validation rules in preTransferSave
        // change the birds island and locationID and save
        b.setCurrentIsland(entity.getTransferDetail().getTransferToIsland());
        b.setCurrentLocationID(entity.getTransferDetail().getTransferToLocationID());
        birdService.saveWithThrow(b, entity.getId());
        // scenario 6
        System.out.println("Bird record updated");
        if (bt != null) {
          bt.setIsland(previous.getTransferDetail().getTransferToIsland());
          transmitterService.saveWithThrow(bt);
          System.out.println("Bird transmitter record updated");
        }
      }
    }
  }

  private Response<RecordEntity> preTransmitterChangeSave(RecordEntity entity,
      TransmitterEntity fromT, TransmitterEntity toT, RecordEntity previous,
      TransmitterEntity previousFromT, TransmitterEntity previousToT, BirdEntity bird) {
    // check if we allow this
    // determine if this record is now the latest
    boolean notLatest = false;
    // check both previous and current
    if (previous != null && previous.getTransmitterChange() != null) {
      // previous record expected to be valid and have a bird id and date time
      RecordEntity latestExcluding = repository
          .findLatestRecordWithPartByBirdIDExcluding(previous.getBirdID(), "transmitterChange",
              previous.getId());
      notLatest = notLatest || (latestExcluding != null && latestExcluding.getDateTime()
          .after(previous.getDateTime()));
    }
    if (entity != null && entity.getTransmitterChange() != null) {
      String birdID = entity.getBirdID();
      Date dateTime = entity.getDateTime();
      if (birdID != null && dateTime != null) {
        RecordEntity latestExcluding = repository
            .findLatestRecordWithPartByBirdIDExcluding(birdID, "transmitterChange", entity.getId());
        notLatest =
            notLatest || (latestExcluding != null && latestExcluding.getDateTime().after(dateTime));
      }
    }
    // determine if the transmitter change part has changed
    boolean newOrChanged = !(previous != null &&
        entity != null &&
        entity.getTransmitterChange() != null &&
        previous.getTransmitterChange() != null &&
        ObjectUtils.nullSafeEquals(entity.getTransmitterChange().getTxFrom(),
            previous.getTransmitterChange().getTxFrom()) &&
        ObjectUtils.nullSafeEquals(entity.getTransmitterChange().getTxTo(),
            previous.getTransmitterChange().getTxTo()) &&
        ObjectUtils.nullSafeEquals(entity.getTransmitterChange().getNewTxFineTune(),
            previous.getTransmitterChange().getNewTxFineTune()));
    // return validation message if not latest and this is new or changed
    if (notLatest && newOrChanged) {
      Response<RecordEntity> response = new Response<RecordEntity>();
      ValidationMessage message = new ValidationMessage();
      message.setPropertyName("dateTime");
      message.setMessageText("Cannot create or change past transmitter change");
      response.setModel(entity);
      response.setMessages(Collections.singletonList(message));
      return response;
    }

    // perform additional validation if this is the latest transmitter change record
    // this is regardless as to whether there has been a change or not
    if (!notLatest) {
      // check if the from and to transmitters are the same, which isn't valid
      if (fromT != null && toT != null) {
        if (fromT.getId().equals(toT.getId())) {
          Response<RecordEntity> response = new Response<RecordEntity>();
          ValidationMessage message = new ValidationMessage();
          message.setPropertyName("");
          message.setMessageText("Cannot select the current transmitter as the new transmitter");
          response.setModel(entity);
          response.setMessages(Collections.singletonList(message));
          return response;
        }
      }
      // check that the to transmitter has the same island as the bird
      if (toT != null) {
        if (bird.getCurrentIsland() == null || toT.getIsland() == null || !bird.getCurrentIsland()
            .equals(toT.getIsland())) {
          Response<RecordEntity> response = new Response<RecordEntity>();
          ValidationMessage message = new ValidationMessage();
          message.setPropertyName("");
          message.setMessageText(
              "Cannot use transmitter " + toT.getTxId() + " as the island of this transmitter ("
                  + toT.getIsland() + ") does not match the current island of the bird (" + bird
                  .getCurrentIsland() + "). Change the island of the transmitter first");
          response.setModel(entity);
          response.setMessages(Collections.singletonList(message));
          return response;
        }
      }
    }

    // save the to transmitter details so it can be restored if the record is changed
    if (previous == null) {
      if (fromT != null) {
        entity.getTransmitterChange().setTxFrom(fromT.getId());
        entity.getTransmitterChange().setRemovedTxFromStatus(fromT.getStatus());
        entity.getTransmitterChange().setRemovedTxFromLastRecordID(fromT.getLastRecordId());
      }
      if (toT != null) {
        entity.getTransmitterChange().setAddedTxFromLastRecordID(toT.getLastRecordId());
        entity.getTransmitterChange().setAddedTxFromStatus(toT.getStatus());
        entity.getTransmitterChange().setAddedTxFromTxFineTune(toT.getTxFineTune());

        if ("New".equals(toT.getStatus())) {
          entity.getTransmitterChange().setNewStatus("Deployed new");
        } else {
          entity.getTransmitterChange().setNewStatus("Deployed old");
        }
      }
    } else if (entity != null && entity.getTransmitterChange() != null) {
      if (fromT != null || previousFromT != null) {
        if (previous.getTransmitterChange() != null && ObjectUtils
            .nullSafeEquals(entity.getTransmitterChange().getTxFrom(),
                previous.getTransmitterChange().getTxFrom())) {
          // nothing to do
        } else if (fromT != null) {
          entity.getTransmitterChange().setRemovedTxFromLastRecordID(fromT.getLastRecordId());
          entity.getTransmitterChange().setRemovedTxFromStatus(fromT.getStatus());
        } else {
          entity.getTransmitterChange().setRemovedTxFromLastRecordID(null);
          entity.getTransmitterChange().setRemovedTxFromStatus(null);
        }
      }
      if (toT != null || previousToT != null) {
        if (previous.getTransmitterChange() != null && ObjectUtils
            .nullSafeEquals(entity.getTransmitterChange().getTxTo(),
                previous.getTransmitterChange().getTxTo())) {
          // nothing to do
          // update the from tx fine tune if it has been added
          if (entity.getTransmitterChange().getNewTxFineTune() != null
              && previous.getTransmitterChange().getNewTxFineTune() == null) {
            entity.getTransmitterChange().setAddedTxFromTxFineTune(toT.getTxFineTune());
          }
        } else if (toT != null) {
          entity.getTransmitterChange().setAddedTxFromLastRecordID(toT.getLastRecordId());
          entity.getTransmitterChange().setAddedTxFromStatus(toT.getStatus());
          entity.getTransmitterChange().setAddedTxFromTxFineTune(toT.getTxFineTune());
          if ("New".equals(toT.getStatus())) {
            entity.getTransmitterChange().setNewStatus("Deployed new");
          } else {
            entity.getTransmitterChange().setNewStatus("Deployed old");
          }
        } else {
          // there shouldn't even be a transmitter change ...
          entity.getTransmitterChange().setAddedTxFromLastRecordID(null);
          entity.getTransmitterChange().setAddedTxFromStatus(null);
          entity.getTransmitterChange().setAddedTxFromTxFineTune(null);
        }
      }
    }
    return null;
  }

  private void postTransmitterChangeSave(RecordEntity entity, TransmitterEntity fromT,
      TransmitterEntity toT, RecordEntity previous, TransmitterEntity previousFromT,
      TransmitterEntity previousToT) {
    TransmitterChangeEntity tc = null;
    if (entity != null) {
      tc = entity.getTransmitterChange();
    }
    TransmitterChangeEntity previousTc = null;
    if (previous != null) {
      previousTc = previous.getTransmitterChange();
    }
    // rule are if existing record and reassigned don't update the transmitter

    // scenarios to consider
    // 1. new record
    // 3. existing record, from transmitter not reassigned, to transmitter not reassigned, same from transmitter, same to transmitter
    // 3. existing record, from transmitter not reassigned, to transmitter not reassigned, same from transmitter, different to transmitter
    // 3. existing record, from transmitter reassigned, to transmitter not reassigned, same from transmitter, same to transmitter
    // 3. existing record, from transmitter not reassigned, to transmitter reassigned, same from transmitter, same to transmitter
    // 3. existing record, from transmitter reassigned, to transmitter reassigned, same from transmitter, same to transmitter
    // 3. existing record, from transmitter reassigned, to transmitter not reassigned, same from transmitter, different to transmitter
    // 3. existing record, from transmitter not reassigned, to transmitter reassigned, same from transmitter, different to transmitter
    // 3. existing record, from transmitter reassigned, to transmitter reassigned, same from transmitter, different to transmitter

    // scenarios that won't be considered
    // 3. existing record, from transmitter not reassigned, to transmitter not reassigned, different from transmitter
    if (entity != null && previous == null) {
      // new record
      if (fromT != null) {
        fromT.setLastRecordId(entity.getId());
        fromT.setStatus("Removed");
        transmitterService.saveWithThrow(fromT);
      }
      if (toT != null) {
        toT.setLastRecordId(entity.getId());
        if ("New".equals(toT.getStatus())) {
          toT.setStatus("Deployed new");
        } else {
          toT.setStatus("Deployed old");
        }
        if (tc.getNewTxFineTune() != null) {
          toT.setTxFineTune(tc.getNewTxFineTune());
        }
        transmitterService.saveWithThrow(toT);
      }
    } else {
      // existing record
      if (fromT != null || previousFromT != null) {
        if (tc != null && previousTc != null && ObjectUtils
            .nullSafeEquals(tc.getTxFrom(), previousTc.getTxFrom())) {
          // from transmitter hasn't changed
          // if possible we still make an update in case there was an error last save
          if (tc.getRemovedTxFromLastRecordID() != null && ObjectUtils
              .nullSafeEquals(fromT.getLastRecordId(), tc.getRemovedTxFromLastRecordID())) {
            // the record was saved but perhaps the transmitter wasn't?
            fromT.setLastRecordId(entity.getId());
            fromT.setStatus("Removed");
            transmitterService.saveWithThrow(fromT);
          } else {
            // leave the transmitter alone
          }
        } else {
          // from transmitter has changed
          if (previousFromT != null) {
            previousFromT.setLastRecordId(previousTc.getRemovedTxFromLastRecordID());
            if (previousTc.getRemovedTxFromStatus() != null) {
              previousFromT.setStatus(previousTc.getRemovedTxFromStatus());
            }
            transmitterService.saveWithThrow(previousFromT);
            System.out.println("Previous from transmitter record reverted");
          }
          if (fromT != null) {
            fromT.setLastRecordId(entity.getId());
            fromT.setStatus("Removed");
            transmitterService.saveWithThrow(fromT);
          }
        }
      }
      if (toT != null || previousToT != null) {
        if (tc != null && previousTc != null && ObjectUtils
            .nullSafeEquals(tc.getTxTo(), previousTc.getTxTo())) {
          // to transmitter hasn't changed
          // if possible we still make an update in case there was an error last save
          // addedTxFromLastRecordID will be null if toT is a brand new transmitter
          if (ObjectUtils.nullSafeEquals(toT.getLastRecordId(), tc.getAddedTxFromLastRecordID())) {
            // the record was saved but perhaps the transmitter wasn't?
            if (!Arrays.asList(new String[]{"Deployed new", "Deployed old"})
                .contains(toT.getStatus())) {
              // attempt to set the status again
              toT.setStatus(entity.getTransmitterChange().getNewStatus());
            }
            toT.setLastRecordId(entity.getId());
            if (tc.getNewTxFineTune() != null) {
              toT.setTxFineTune(tc.getNewTxFineTune());
            }
            transmitterService.saveWithThrow(toT);
          } else if (ObjectUtils.nullSafeEquals(toT.getLastRecordId(), entity.getId())) {
            // both record and transmitter were saved and transmitter hasn't been reassigned
            // update or revert the tx fine tune
            if (tc.getNewTxFineTune() != null) {
              // update
              toT.setTxFineTune(tc.getNewTxFineTune());
            } else {
              // revert if required
              if (previousTc.getNewTxFineTune() != null) {
                if (previousTc.getNewTxFineTune().equals(toT.getTxFineTune())) {
                  // tx fine tune still set to previously set value
                  // revert it
                  toT.setTxFineTune(previousTc.getAddedTxFromTxFineTune());
                } else {
                  // something or someone has changed the fine tune so leave it alone
                  System.out.println("Previous tx fine tune not reverted");
                }
              } else {
                // no previous update so nothing to revert
              }
            }
            transmitterService.saveWithThrow(toT);
          } else {
            // leave the transmitter alone
          }
        } else {
          // to transmitter has changed
          if (previousToT != null) {
            // previousTc will not be null
            previousToT.setLastRecordId(previousTc.getAddedTxFromLastRecordID());
            if (previousTc.getAddedTxFromStatus() != null) {
              previousToT.setStatus(previousTc.getAddedTxFromStatus());
            }
            // revert if required
            if (previousTc.getNewTxFineTune() != null) {
              if (previousTc.getNewTxFineTune().equals(previousToT.getTxFineTune())) {
                // tx fine tune still set to previously set value
                // revert it
                previousToT.setTxFineTune(previousTc.getAddedTxFromTxFineTune());
              } else {
                // something or someone has changed the fine tune so leave it alone
                System.out.println("Previous tx fine tune not reverted");
              }
            } else {
              // no previous update so nothing to revert
            }
            transmitterService.saveWithThrow(previousToT);
            System.out.println("Previous to transmitter record reverted");
          }
          if (toT != null) {
            toT.setLastRecordId(entity.getId());
            toT.setStatus(entity.getTransmitterChange().getNewStatus());
            if (tc.getNewTxFineTune() != null) {
              toT.setTxFineTune(tc.getNewTxFineTune());
            }
            transmitterService.saveWithThrow(toT);
          }
        }
      }
    }
  }

  public void preBloodSampleSave(RecordEntity entity, RecordEntity previous) {
    // TODO this code is very similar to other pre sample saves but we'd need inheritance to refactor
    List<BloodSampleEntity> bloodList = null;
    if (entity != null) {
      bloodList = entity.getBloodSampleDetail().getBloodSampleList();
    }
    if (bloodList == null) {
      bloodList = Collections.emptyList();
    }

    // calculated changed, added, removed
    List<BloodSampleEntity> added = new ArrayList<>();
    List<BloodSampleEntity> changed = new ArrayList<>();
    for (BloodSampleEntity s : bloodList) {
      if (s.getSampleID() == null) {
        added.add(s);
      } else {
        changed.add(s);
      }
    }

    for (BloodSampleEntity blood : added) {
      SampleEntity s = new SampleEntity();
      // set record level fields
      s.setSampleCategory("Blood");
      s.setBirdID(entity.getBirdID());
      s.setCollectionDate(entity.getDateTime());
      s.setCollectionIsland(entity.getIsland());
      s.setCollectionLocationID(entity.getLocationID());
      // set record sample level fields
      s.setSampleName(blood.getSampleName());
      s.setSampleTakenBy(blood.getSampleTakenBy());
      s.setReasonForSample(blood.getReasonForSample());
      s.setStorageMedium(blood.getStorageMedium());
      s.setContainer(blood.getContainer());
      s.setStorageConditions(blood.getStorageConditions());
      BloodDetailEntity d = new BloodDetailEntity();
      s.setBloodDetail(d);
      d.setTotalBloodVolumeInMl(entity.getBloodSampleDetail().getTotalBloodVolumeInMl());
      d.setType(blood.getType());
      d.setVeinSite(entity.getBloodSampleDetail().getVeinSite());
      d.setVolumeInMl(blood.getVolumeInMl());
      // initialise sample level fields
      s.setCurrentIsland(entity.getIsland());
      s.setArchived(false);
      sampleService.saveWithThrow(s, false);
      // note sample type set by SampleService.saveWithThrow();
      blood.setSampleID(s.getId());
    }

    for (BloodSampleEntity blood : changed) {
      SampleEntity s = sampleService.findById(blood.getSampleID());
      // update record level fields
      s.setBirdID(entity.getBirdID());
      s.setCollectionDate(entity.getDateTime());
      s.setCollectionIsland(entity.getIsland());
      s.setCollectionLocationID(entity.getLocationID());
      // update record sample level fields
      s.setStorageMedium(blood.getStorageMedium());
      s.setContainer(blood.getContainer());
      s.setStorageConditions(blood.getStorageConditions());
      BloodDetailEntity d = s.getBloodDetail();
      if (d == null) {
        d = new BloodDetailEntity();
        s.setBloodDetail(d);
      }
      d.setTotalBloodVolumeInMl(entity.getBloodSampleDetail().getTotalBloodVolumeInMl());
      d.setType(blood.getType());
      d.setVeinSite(entity.getBloodSampleDetail().getVeinSite());
      d.setVolumeInMl(blood.getVolumeInMl());
      s.setSampleTakenBy(blood.getSampleTakenBy());
      s.setReasonForSample(blood.getReasonForSample());
      s.setSampleName(blood.getSampleName());
      // note sample type set by SampleService.saveWithThrow();
      sampleService.saveWithThrow(s, false);
    }
  }

  public void preSwabSampleSave(RecordEntity entity, RecordEntity previous) {
    // TODO this code is very similar to other pre sample saves but we'd need inheritance to refactor
    List<SwabSampleEntity> swabList = null;
    if (entity != null) {
      swabList = entity.getSwabSampleList();
    }
    if (swabList == null) {
      swabList = Collections.emptyList();
    }
    List<SwabSampleEntity> previousSwabList = null;
    if (previous != null) {
      previousSwabList = previous.getSwabSampleList();
    }
    if (previousSwabList == null) {
      previousSwabList = Collections.emptyList();
    }

    // calculated changed, added, removed
    List<SwabSampleEntity> added = new ArrayList<>();
    List<SwabSampleEntity> changed = new ArrayList<>();
    for (SwabSampleEntity s : swabList) {
      if (s.getSampleID() == null) {
        added.add(s);
      } else {
        changed.add(s);
      }
    }

    for (SwabSampleEntity swab : added) {
      SampleEntity s = new SampleEntity();
      // set record level fields
      s.setSampleCategory("Swab");
      s.setBirdID(entity.getBirdID());
      s.setCollectionDate(entity.getDateTime());
      s.setCollectionIsland(entity.getIsland());
      s.setCollectionLocationID(entity.getLocationID());
      // set record sample level fields
      s.setSampleName(swab.getSampleName());
      s.setSampleTakenBy(swab.getSampleTakenBy());
      s.setReasonForSample(swab.getReasonForSample());
      s.setStorageMedium(swab.getStorageMedium());
      s.setContainer(swab.getContainer());
      s.setStorageConditions(swab.getStorageConditions());
      SwabDetailEntity d = new SwabDetailEntity();
      s.setSwabDetail(d);
      d.setQuantity(swab.getQuantity());
      d.setSwabSite(swab.getSwabSite());
      // initialise sample level fields
      s.setCurrentIsland(entity.getIsland());
      s.setArchived(false);
      // note sample type set by SampleService.saveWithThrow();
      sampleService.saveWithThrow(s, false);
      swab.setSampleID(s.getId());
    }

    for (SwabSampleEntity swab : changed) {
      SampleEntity s = sampleService.findById(swab.getSampleID());
      // update record level fields
      s.setBirdID(entity.getBirdID());
      s.setCollectionDate(entity.getDateTime());
      s.setCollectionIsland(entity.getIsland());
      s.setCollectionLocationID(entity.getLocationID());
      // update record sample level fields
      s.setStorageMedium(swab.getStorageMedium());
      s.setContainer(swab.getContainer());
      s.setStorageConditions(swab.getStorageConditions());
      SwabDetailEntity d = s.getSwabDetail();
      if (d == null) {
        d = new SwabDetailEntity();
        s.setSwabDetail(d);
      }
      d.setQuantity(swab.getQuantity());
      d.setSwabSite(swab.getSwabSite());
      s.setSampleTakenBy(swab.getSampleTakenBy());
      s.setReasonForSample(swab.getReasonForSample());
      s.setSampleName(swab.getSampleName());
      // note sample type set by SampleService.saveWithThrow();
      sampleService.saveWithThrow(s, false);
    }
  }

  public void preOtherSampleSave(RecordEntity entity, RecordEntity previous) {
    // TODO this code is very similar to other pre sample saves but we'd need inheritance to refactor
    List<OtherSampleEntity> otherList = null;
    if (entity != null) {
      otherList = entity.getOtherSampleList();
    }
    if (otherList == null) {
      otherList = Collections.emptyList();
    }
    List<OtherSampleEntity> previousOtherList = null;
    if (previous != null) {
      previousOtherList = previous.getOtherSampleList();
    }
    if (previousOtherList == null) {
      previousOtherList = Collections.emptyList();
    }

    // calculated changed, added, removed
    List<OtherSampleEntity> added = new ArrayList<>();
    List<OtherSampleEntity> changed = new ArrayList<>();
    for (OtherSampleEntity s : otherList) {
      if (s.getSampleID() == null) {
        added.add(s);
      } else {
        changed.add(s);
      }
    }

    for (OtherSampleEntity other : added) {
      SampleEntity s = new SampleEntity();
      // set record level fields
      s.setSampleCategory("Other");
      s.setBirdID(entity.getBirdID());
      s.setCollectionDate(entity.getDateTime());
      s.setCollectionIsland(entity.getIsland());
      s.setCollectionLocationID(entity.getLocationID());
      // set record sample level fields
      s.setSampleName(other.getSampleName());
      s.setSampleTakenBy(other.getSampleTakenBy());
      s.setReasonForSample(other.getReasonForSample());
      s.setStorageMedium(other.getStorageMedium());
      s.setContainer(other.getContainer());
      s.setStorageConditions(other.getStorageConditions());
      OtherDetailEntity d = new OtherDetailEntity();
      s.setOtherDetail(d);
      d.setType(other.getType());
      d.setAmount(other.getAmount());
      d.setUnits(other.getUnits());
      // initialise sample level fields
      s.setCurrentIsland(entity.getIsland());
      s.setArchived(false);
      // note sample type set by SampleService.saveWithThrow();
      sampleService.saveWithThrow(s, false);
      other.setSampleID(s.getId());
    }

    for (OtherSampleEntity other : changed) {
      SampleEntity s = sampleService.findById(other.getSampleID());
      // update record level fields
      s.setBirdID(entity.getBirdID());
      s.setCollectionDate(entity.getDateTime());
      s.setCollectionIsland(entity.getIsland());
      s.setCollectionLocationID(entity.getLocationID());
      // update record sample level fields
      s.setStorageMedium(other.getStorageMedium());
      s.setContainer(other.getContainer());
      s.setStorageConditions(other.getStorageConditions());
      OtherDetailEntity d = s.getOtherDetail();
      if (d == null) {
        d = new OtherDetailEntity();
        s.setOtherDetail(d);
      }
      d.setType(other.getType());
      d.setAmount(other.getAmount());
      d.setUnits(other.getUnits());
      s.setSampleTakenBy(other.getSampleTakenBy());
      s.setReasonForSample(other.getReasonForSample());
      s.setSampleName(other.getSampleName());
      // note sample type set by SampleService.saveWithThrow();
      sampleService.saveWithThrow(s, false);
    }
  }

  public void preSpermSampleSave(RecordEntity entity, RecordEntity previous) {
    // TODO this code is very similar to other pre sample saves but we'd need inheritance to refactor
    List<SpermSampleEntity> spermList = null;
    if (entity != null) {
      spermList = entity.getSpermSampleList();
    }
    if (spermList == null) {
      spermList = Collections.emptyList();
    }
    List<SpermSampleEntity> previousSpermList = null;
    if (previous != null) {
      previousSpermList = previous.getSpermSampleList();
    }
    if (previousSpermList == null) {
      previousSpermList = Collections.emptyList();
    }

    // calculated changed, added, removed
    List<SpermSampleEntity> added = new ArrayList<>();
    List<SpermSampleEntity> changed = new ArrayList<>();
    for (SpermSampleEntity s : spermList) {
      if (s.getSampleID() == null) {
        added.add(s);
      } else {
        changed.add(s);
      }
    }

    for (SpermSampleEntity sperm : added) {
      SampleEntity s = new SampleEntity();
      // set record level fields
      s.setSampleCategory("Sperm");
      s.setBirdID(entity.getBirdID());
      s.setCollectionDate(entity.getDateTime());
      s.setCollectionIsland(entity.getIsland());
      s.setCollectionLocationID(entity.getLocationID());
      // set record sample level fields
      s.setSampleName(sperm.getSampleName());
      s.setSampleTakenBy(sperm.getSampleTakenBy());
      s.setReasonForSample(sperm.getReasonForSample());
      s.setContainer(sperm.getContainer());
      s.setStorageConditions(sperm.getStorageConditions());
      SpermDetailEntity d = new SpermDetailEntity();
      s.setSpermDetail(d);
      d.setDiluent(sperm.getDiluent());
      d.setVolumeInMicroL(sperm.getVolumeInMicroL());
      d.setCollectionMethod(sperm.getCollectionMethod());
      d.setPapillaSwelling(sperm.getPapillaSwelling());
      d.setStimulation(sperm.getStimulation());
      d.setStress(sperm.getStress());
      // initialise sample level fields
      s.setCurrentIsland(entity.getIsland());
      s.setArchived(false);
      // note sample type set by SampleService.saveWithThrow();
      sampleService.saveWithThrow(s, false);
      sperm.setSampleID(s.getId());
    }

    for (SpermSampleEntity sperm : changed) {
      SampleEntity s = sampleService.findById(sperm.getSampleID());
      // update record level fields
      s.setBirdID(entity.getBirdID());
      s.setCollectionDate(entity.getDateTime());
      s.setCollectionIsland(entity.getIsland());
      s.setCollectionLocationID(entity.getLocationID());
      // update record sample level fields
      s.setContainer(sperm.getContainer());
      s.setStorageConditions(sperm.getStorageConditions());
      SpermDetailEntity d = s.getSpermDetail();
      if (d == null) {
        d = new SpermDetailEntity();
        s.setSpermDetail(d);
      }
      d.setDiluent(sperm.getDiluent());
      d.setVolumeInMicroL(sperm.getVolumeInMicroL());
      d.setCollectionMethod(sperm.getCollectionMethod());
      d.setPapillaSwelling(sperm.getPapillaSwelling());
      d.setStimulation(sperm.getStimulation());
      d.setStress(sperm.getStress());
      s.setSampleTakenBy(sperm.getSampleTakenBy());
      s.setReasonForSample(sperm.getReasonForSample());
      s.setSampleName(sperm.getSampleName());
      // note sample type set by SampleService.saveWithThrow();
      sampleService.saveWithThrow(s, false);
    }
  }

  @Override
  public void deleteById(String id) {
    RecordEntity entity = repository.findById(id).get();
    BirdEntity b = getBird(entity);
    TransmitterEntity txFrom = getFromTransmitter(entity);
    TransmitterEntity txTo = getToTransmitter(entity);

    DeleteByIdCheckDTO deleteByIdCheckDTO = deleteByIdCheck(id);
    if (deleteByIdCheckDTO != null && deleteByIdCheckDTO.getDeleteOk()) {
      if (entity.getTransferDetail() != null) {
        TransmitterEntity bt = birdService.getCurrentTransmitter(b.getId());
        preTransferDelete(entity, b, bt);
      }
      if (entity.getTransmitterChange() != null) {
        preTransmitterChangeDelete(entity, txFrom, txTo);
      }
      repository.deleteById(id);
    } else {
      throw new RuntimeException("Delete check failed for: " + id);
    }
  }

  private void preTransferDelete(RecordEntity entity, BirdEntity b, TransmitterEntity bt) {
    // scenarios to consider
    // 1. bird was not updated
    // 2. bird was updated, bird updated since
    // 3. bird was updated, bird not updated since
    if (b.getModifiedByRecordId() == null || !b.getModifiedByRecordId().equals(entity.getId())) {
      // bird was not updated
      // OR
      // bird was updated, bird updated since
      System.out.println("Previous bird record not reverted");
    } else {
      // bird was updated, bird not updated since
      b.setCurrentIsland(entity.getTransferDetail().getTransferFromIsland());
      b.setCurrentLocationID(entity.getTransferDetail().getTransferFromLocationID());
      birdService.saveWithThrow(b, null);
      System.out.println("Previous bird record reverted");
      if (bt != null && ObjectUtils
          .nullSafeEquals(bt.getIsland(), entity.getTransferDetail().getTransferToIsland())) {
        bt.setIsland(entity.getTransferDetail().getTransferFromIsland());
        transmitterService.saveWithThrow(bt);
        System.out.println("Previous bird transmitter record reverted");
      }
    }
  }

  private void preTransmitterChangeDelete(RecordEntity entity, TransmitterEntity txFrom,
      TransmitterEntity txTo) {
    postTransmitterChangeSave(null, null, null, entity, txFrom, txTo);
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
  public void export(RecordCriteria criteria, HttpServletResponse response) {
    List<String> header = Arrays.asList(
        "Date", "Bird", "Recorder", "Island", "Location", "Easting", "Northing",
        "Record Type", "Reason", "Sub Reason", "Activity", "Weight (kg)",
        "Samples", "Comments");
    List<String> props = Arrays.asList(
        "dateTime", "birdName", "recorder", "island", "locationName", "easting", "northing",
        "recordType", "reason", "subReason", "activity", "weight",
        "hasSample", "hasComment");
    criteria.setPageSize(exportUtils.getMaxRows());
    criteria.setPageNumber(1);
    PagedResponse<RecordSearchDTO> pr = this.findSearchDTOByCriteria(criteria);
    exportUtils.export(response, pr, header, props, "record");
  }


  /**
   * Process the checkmate data and derive new values if necessary.
   *
   * @param entity The record.
   */
  private Response<RecordEntity> processCheckmate(RecordEntity entity) {
    CheckmateEntity checkmateEntity = entity.getCheckmate();
    // If there is no checkmate, then we don't need to do anything.
    if (checkmateEntity == null) {
      return null;
    }

    // Always re-calcuate the Battery Life if the digits are present.
    if ("Manual".equals(checkmateEntity.getDataCaptureType())) {
      Integer batteryWeeks = TransmitterHelper
          .deriveValue(checkmateEntity.getBattery1(), checkmateEntity.getBattery2());
      BatteryLifeEntity ble = new BatteryLifeEntity();
      ble.setBattLifeWeeks(batteryWeeks);
      entity.setBatteryLife(ble);

      Integer last24hourActivity = TransmitterHelper
          .deriveValue(checkmateEntity.getLast24hourActivity1(),
              checkmateEntity.getLast24hourActivity2());
      checkmateEntity.setLast24hourActivity(last24hourActivity);
    } else {
      // Reset the battery.
      entity.setBatteryLife(null);
      checkmateEntity.setBattery1(null);
      checkmateEntity.setBattery2(null);
      checkmateEntity.setLast24hourActivity(null);
      checkmateEntity.setLast24hourActivity1(null);
      checkmateEntity.setLast24hourActivity2(null);
    }

    // Short-circuit - if we don't have entries to derive values for then we don't care.
    if (checkmateEntity == null || checkmateEntity.getCheckmateDataList() == null
        || checkmateEntity.getCheckmateDataList().size() == 0) {
      return null;
    }

    boolean hasCheckmateChanged = false;

    // There are entries so we need to compare.
    if (entity.getId() == null) {
      // There is no previous entry so we need to process the entry.
      hasCheckmateChanged = true;
    } else {
      RecordEntity oldEntity = repository.findById(entity.getId()).get();
      CheckmateEntity oldCM = oldEntity.getCheckmate();

      if (oldEntity == null || oldCM == null || oldCM.getCheckmateDataList() == null
          || oldCM.getCheckmateDataList().size() != checkmateEntity.getCheckmateDataList()
          .size()) {
        // The number of checkmate records is different so we need to derive values.
        hasCheckmateChanged = true;
      } else {
        // Identity number of reocrds so we need to check if they're the same.
        // Record count is the same - check whether any of the data has been edited.
        List<CheckmateDataEntity> newList = checkmateEntity.getCheckmateDataList();
        List<CheckmateDataEntity> oldList = oldEntity.getCheckmate().getCheckmateDataList();
        for (int i = 0; i < newList.size(); i++) {
          if (!CheckmateHelper.areIdentical(newList.get(i), oldList.get(i))) {
            hasCheckmateChanged = true;
            break;
          }
        }
      }
    }

    if (hasCheckmateChanged) {
      // Checkmate data has changed, re-calculate the values.

      for (CheckmateDataEntity row : checkmateEntity.getCheckmateDataList()) {
        if ("Manual".equals(checkmateEntity.getDataCaptureType())) {
          row.setDuration(TransmitterHelper.deriveValue(row.getDuration1(), row.getDuration2()));
          row.setQuality(TransmitterHelper.deriveValue(row.getQuality1(), row.getQuality2()));
          row.setTime(CheckmateHelper.deriveTime(entity.getDateTime(),
              TransmitterHelper.deriveValue(row.getTime1(), row.getTime2())));
          row.setFemaleTx(TransmitterHelper.deriveValue(row.getFemaleTx1(), row.getFemaleTx2()));

          String derivedBirdId = CheckmateHelper.deriveBird(this.birdService, entity.getIsland(),
              TransmitterHelper.deriveValue(row.getFemaleTx1(), row.getFemaleTx2()));
          if (!Objects.equals(derivedBirdId, row.getBirdId())) {
            return buildErrorResponse(entity,
                "Determined bird differs from that shown on the user interface");
          } else {
            row.setBirdId(derivedBirdId);
          }
        } else {
          // Reset the partial fields.
          row.setFemaleTx1(null);
          row.setFemaleTx2(null);
          row.setTime1(null);
          row.setTime2(null);
          row.setDuration1(null);
          row.setDuration2(null);
          row.setQuality1(null);
          row.setQuality2(null);

          // Re-calculate the bird.
          String derivedBirdId = CheckmateHelper
              .deriveBird(this.birdService, entity.getIsland(), row.getFemaleTx());
          if (!Objects.equals(derivedBirdId, row.getBirdId())) {
            return buildErrorResponse(entity,
                "Determined bird differs from that shown on the user interface");
          } else {
            row.setBirdId(derivedBirdId);
          }
        }
      }
    }
    return null;
  }

  private Response<RecordEntity> processStandard(RecordEntity entity) {
    // No Standard, ignore.
    if (entity.getStandard() == null) {
      return null;
    }

    // Reset manual flags.
    StandardEntity standard = entity.getStandard();
    // Always re-calculate the Battery Life if the digits are present.
    Integer batteryWeeks = TransmitterHelper.deriveValue(standard.getBattery1(),
        standard.getBattery2());
    BatteryLifeEntity ble = new BatteryLifeEntity();
    ble.setBattLifeWeeks(batteryWeeks);
    entity.setBatteryLife(ble);

    // There should never be validation errors, so return null to let the processing continue.
    return null;
  }

  protected Response<RecordEntity> buildErrorResponse(RecordEntity entity, String... messages) {
    List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>(messages.length);
    for (String message : messages) {
      ValidationMessage msg = new ValidationMessage();
      msg.setMessageText(message);
      validationMessages.add(msg);
    }
    Response<RecordEntity> response = new Response<>(entity);
    response.setMessages(validationMessages);
    return response;
  }

  public RecordEntity findLatestBandsRecordByBirdID(String birdID) {
    return repository.findLatestRecordWithPartByBirdID(birdID, "bands");
  }

  public RecordEntity findLatestChipsRecordByBirdID(String birdID) {
    return repository.findLatestRecordWithPartByBirdID(birdID, "chips");
  }

  public RecordEntity findLatestTransmitterChangeRecordByBirdID(String birdID) {
    return repository.findLatestRecordWithPartByBirdID(birdID, "transmitterChange");
  }

  public List<BirdTransmitterHistoryDTO> findBirdTransmitterHistoryDTOByBirdID(String birdID) {
    return repository.findTransmitterHistoryDTOByBirdID(birdID);
  }

  public List<TransmitterBirdHistoryDTO> findTransmitterBirdHistoryDTOByTxDocId(String txDocId) {
    return repository.findTransmitterBirdHistoryDTOByTxDocId(txDocId);
  }

  public RecordEntity findRecordBySampleID(String sampleID) {
    return repository.findRecordBySampleID(sampleID);
  }

}

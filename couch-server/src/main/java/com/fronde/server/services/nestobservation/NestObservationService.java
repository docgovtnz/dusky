package com.fronde.server.services.nestobservation;

import com.fronde.server.domain.ChickHealthEntity;
import com.fronde.server.domain.ChickRecordReferenceEntity;
import com.fronde.server.domain.EggHealthEntity;
import com.fronde.server.domain.EggRecordReferenceEntity;
import com.fronde.server.domain.LocationEntity;
import com.fronde.server.domain.MotherTripEntity;
import com.fronde.server.domain.MotherTripSummaryEntity;
import com.fronde.server.domain.NestChickEntity;
import com.fronde.server.domain.NestEggEntity;
import com.fronde.server.domain.NestObservationEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.domain.WeightEntity;
import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.bird.BirdService;
import com.fronde.server.services.location.LocationService;
import com.fronde.server.services.record.RecordService;
import com.fronde.server.services.validation.ValidationMessage;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class NestObservationService extends NestObservationBaseService {

  public static long MILLIS_PER_MIN = 60 * 1000;

  @Autowired
  private LocationService locationService;

  @Autowired
  private RecordService recordService;

  @Autowired
  private BirdService birdService;

  @Override
  public DeleteByIdCheckDTO deleteByIdCheck(String docId) {
    DeleteByIdCheckDTO dto = new DeleteByIdCheckDTO();
    dto.setId(docId);
    // we can't delete something that doesn't exist
    dto.setDeleteOk(docId != null);
    return dto;
  }

  public Response<NestObservationEntity> save(NestObservationEntity entity) {
    List<ValidationMessage> messages = validationService.validateEntity(entity);
    if (!messages.isEmpty()) {
      Response resp = new Response(entity);
      resp.setMessages(messages);
      return resp;
    }

    List<ValidationMessage> allMessages = new LinkedList<>();

    entity.setEggRecordReferenceList(new LinkedList<>());
    entity.setChickRecordReferenceList(new LinkedList<>());

    // Process the eggs and accumulate any error messages.
    List<ValidationMessage> validationMessages = processEggs(entity);
    if (!validationMessages.isEmpty()) {
      Response resp = new Response(entity);
      resp.setMessages(validationMessages);
      return resp;
    }

    // Process the chicks and accumulate any error messages.
    List<ValidationMessage> validationMessagesChicks = processChicks(entity);
    if (!validationMessagesChicks.isEmpty()) {
      Response resp = new Response(entity);
      resp.setMessages(validationMessagesChicks);
      return resp;
    }

    // Remove the nest eggs and chicks from the NestObservation record. They're already saved as records.
    entity.setNestChickList(null);
    entity.setNestEggList(null);

    // Process the Mother Trip Information.
    processMotherTrips(entity);

    // Process the eggs and chicks to retrieve their record IDs and save appropriately.
    Response<NestObservationEntity> response = super.save(entity);

    // If the nest observation was saved successfully.
    if (response.getMessages().isEmpty()) {

    }

    return response;
  }

  public void processMotherTrips(NestObservationEntity entity) {

    if (entity.getMotherTripList() != null && entity.getMotherTripList().size() > 0) {

      MotherTripSummaryEntity summary = new MotherTripSummaryEntity();
      summary.setNumberOfTimesOff(entity.getMotherTripList().size());

      int maxTime = -1;
      int totalTime = 0;
      Date firstTime = null;

      for (MotherTripEntity trip : entity.getMotherTripList()) {

        if (trip.getMotherLeft() != null) {
          if (firstTime == null || trip.getMotherLeft().before(firstTime)) {
            firstTime = trip.getMotherLeft();
          }

          if (trip.getMotherBack() != null) {
            int duration = getDurationInMinutes(trip.getMotherLeft(), trip.getMotherBack());
            if (duration > maxTime) {
              maxTime = duration;
            }
            totalTime += duration;
          }
        }
      }

      summary.setMaxTimeOff(maxTime >= 0 ? maxTime : null);
      summary.setTotalTimeOff(totalTime > 0 ? totalTime : null);
      summary.setFirstTimeOff(firstTime);

      entity.setMotherTripSummary(summary);
    }
  }

  public int getDurationInMinutes(Date date1, Date date2) {
    return (int) Math.abs((date2.getTime() - date1.getTime()) / (float) MILLIS_PER_MIN);
  }


  public List<ValidationMessage> processChicks(NestObservationEntity entity) {
    List<ValidationMessage> messages = new LinkedList<>();

    // For each egg, create a record.
    if (entity.getNestChickList() != null) {
      for (NestChickEntity chick : entity.getNestChickList()) {

        // Create a record only if there is relevant data.
        if (anyNotNull(
            chick.getRecordID(),
            // If RecordID is set, we need to update even if everything is empty.
            chick.getWeightInGrams(),
            chick.getCropStatus(),
            chick.getRespiratoryRate(),
            chick.getComments()
        )) {
          RecordEntity r = new RecordEntity();

          if (chick.getRecordID() != null) {
            r = recordService.findById(chick.getRecordID());
          }

          r.setBirdID(chick.getBirdID());

          // Location from the Nest Observation.
          LocationEntity l = locationService.findById(entity.getLocationID());
          r.setLocationID(l.getId());
          r.setIsland(l.getIsland());
          r.setMappingMethod(l.getMappingMethod());
          r.setEasting(l.getEasting());
          r.setNorthing(l.getNorthing());

          // Choose the Inspection time if present; otherwise use the Nest Observation date/time.
          if (entity.getObservationTimes().getInspectionTime() != null) {
            r.setDateTime(entity.getObservationTimes().getInspectionTime());
          } else {
            r.setDateTime(entity.getDateTime());
          }

          // Event type information.
          r.setRecordType("Capture");
          r.setActivity(chick.getActivity());
          r.setReason("Chick health");
          r.setSubReason(null);
          r.setSignificantEvent(false);
          r.setErrol(false);

          // Copy the Observer list from the Nest Observation
          r.setObserverList(entity.getObserverList());

          // Add the Weight Entity
          if (chick.getWeightInGrams() != null) {
            WeightEntity we = new WeightEntity();
            we.setWeight(gramsToKg(chick.getWeightInGrams()));
            we.setMethod(entity.getWeighMethod());
            r.setWeight(we);
          } else {
            r.setWeight(null);
          }

          // Create the chick information tab.
          if (anyNotNull(chick.getRespiratoryRate(), chick.getCropStatus())) {
            ChickHealthEntity chickHealth = new ChickHealthEntity();
            chickHealth.setCropStatus(chick.getCropStatus());
            chickHealth.setRespiratoryRate(chick.getRespiratoryRate());
            r.setChickHealth(chickHealth);
          } else {
            r.setChickHealth(null);
          }

          // Save the comments.
          r.setComments(chick.getComments());

          // Save the record.
          // TODO this may silently fail but we didn't want to change this close to a prod release
          recordService.save(r);
          messages.addAll(recordService.save(r).getMessages());

          // Add chick references
          ChickRecordReferenceEntity chickRef = new ChickRecordReferenceEntity();
          chickRef.setRecordID(r.getId());
          chickRef.setBirdID(r.getBirdID());
          entity.getChickRecordReferenceList().add(chickRef);

          // If there are errors creating the record, abort now.
          if (!messages.isEmpty()) {
            break;
          }
        } else {
          ChickRecordReferenceEntity chickRef = new ChickRecordReferenceEntity();
          chickRef.setBirdID(chick.getBirdID());
          entity.getChickRecordReferenceList().add(chickRef);
        }
      }
    }
    return messages;
  }

  public List<ValidationMessage> processEggs(NestObservationEntity entity) {
    List<ValidationMessage> messages = new LinkedList<>();

    // For each egg, create a record.
    if (entity.getNestEggList() != null) {
      for (NestEggEntity egg : entity.getNestEggList()) {

        if (anyNotNull(
            egg.getRecordID(),
            // If RecordID is set, we need to update the record even if everything is empty.
            egg.getLengthInMms(),
            egg.getWidthInMms(),
            egg.getWeightInGrams(),
            egg.getCandlingAgeEstimateInDays(),
            egg.getTemperature(),
            egg.getHeartRate(),
            egg.getEmbryoMoving(),
            egg.getComments()
        )) {
          RecordEntity r = new RecordEntity();
          if (egg.getRecordID() != null) {
            r = recordService.findById(egg.getRecordID());
          }

          r.setBirdID(egg.getBirdID());

          // Location from the Nest Observation.
          String locationID = entity.getLocationID();
          r.setLocationID(locationID);
          LocationEntity l = locationService.findById(locationID);
          r.setIsland(l.getIsland());
          r.setMappingMethod(l.getMappingMethod());
          r.setEasting(l.getEasting());
          r.setNorthing(l.getNorthing());

          // Choose the Inspection time if present; otherwise use the Nest Observation date/time.
          if (entity.getObservationTimes().getInspectionTime() != null) {
            r.setDateTime(entity.getObservationTimes().getInspectionTime());
          } else {
            r.setDateTime(entity.getDateTime());
          }

          // Event type information.
          r.setRecordType("Capture");
          r.setActivity(egg.getActivity());
          r.setReason("Egg health");
          r.setSubReason(null);
          r.setSignificantEvent(false);
          r.setErrol(false);

          // Copy the Observer list from the Nest Observation
          r.setObserverList(entity.getObserverList());

          // Add the Weight Entity
          if (egg.getWeightInGrams() != null) {
            WeightEntity we = new WeightEntity();
            we.setWeight(gramsToKg(egg.getWeightInGrams()));
            we.setMethod(entity.getWeighMethod());
            r.setWeight(we);
          } else {
            // Clear out the settings.
            r.setWeight(null);
          }

          if (anyNotNull(egg.getEmbryoMoving(), egg.getComments(), egg.getLengthInMms(),
              egg.getWidthInMms(), egg.getCandlingAgeEstimateInDays(),
              egg.getHeartRate(), egg.getTemperature())) {
            EggHealthEntity eggHealth = new EggHealthEntity();
            eggHealth.setEmbryoMoving(egg.getEmbryoMoving());
            eggHealth.setCandlingAgeEstimateInDays(egg.getCandlingAgeEstimateInDays());
            eggHealth.setHeartRate(egg.getHeartRate());
            eggHealth.setTemperature(egg.getTemperature());
            eggHealth.setLengthInMms(egg.getLengthInMms());
            eggHealth.setWidthInMms(egg.getWidthInMms());
            r.setEggHealth(eggHealth);
          } else {
            r.setEggHealth(null);
          }

          // Save the comments.
          r.setComments(egg.getComments());

          // Save the record.
          // TODO this may silently fail but we didn't want to change this close to a prod release
          recordService.save(r);

          EggRecordReferenceEntity eggRef = new EggRecordReferenceEntity();
          eggRef.setBirdID(r.getBirdID());
          eggRef.setRecordID(r.getId());
          entity.getEggRecordReferenceList().add(eggRef);

          messages.addAll(recordService.save(r).getMessages());

          // If there are errors creating the record, abort now.
          if (!messages.isEmpty()) {
            break;
          }
        } else {
          EggRecordReferenceEntity eggRef = new EggRecordReferenceEntity();
          eggRef.setBirdID(egg.getBirdID());
          entity.getEggRecordReferenceList().add(eggRef);
        }
      }
    }
    return messages;
  }

  public boolean anyNotNull(Object... objects) {
    for (Object object : objects) {
      if (object != null) {
        return true;
      }
    }
    return false;
  }


  public PagedResponse<NestObservationSearchDTO> findSearchDTOByCriteria(
      @RequestBody NestObservationCriteria criteria) {
    return repository.findSearchDTOByCriteria(criteria);
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
  public void export(NestObservationCriteria criteria, HttpServletResponse response) {

    List<String> header = new ArrayList(
        Arrays.asList("Date", "Mother", "Island", "Location", "Location Type"));
    List<String> props = new ArrayList(Arrays
        .asList("dateTime", "birdName", "island", "locationName", "locationType"));
    criteria.setPageSize(exportUtils.getMaxRows());
    criteria.setPageNumber(1);
    PagedResponse<NestObservationSearchDTO> pr = this.findSearchDTOByCriteria(criteria);

    Set<String> childHeaders = new TreeSet<>();
    Set<String> observerHeaders = new TreeSet<>();

    List<Map<String, Object>> records = new LinkedList<>();

    // Need to append the child bird names into a comma separated list for export.
    pr.getResults().forEach(no -> {
      Map<String, Object> row = new HashMap<>();

      row.put("dateTime", no.getDateTime());
      row.put("birdName", no.getBirdName());
      row.put("island", no.getIsland());
      row.put("locationName", no.getLocationName());
      row.put("locationType", no.getLocationType());

      buildArray(childHeaders, row, "Child", no.getChildren(), (child) -> child.getBirdName());
      buildArray(observerHeaders, row, "Observer", no.getObservers(),
          (observer) -> observer.getName());

      records.add(row);
    });

    // Update the headers.
    header.addAll(childHeaders);
    header.addAll(observerHeaders);
    props.addAll(childHeaders);
    props.addAll(observerHeaders);

    exportUtils.export(response, records, header, props, "nestobservation");
  }

  public <T> void buildArray(Set<String> headers, Map<String, Object> map, String key,
      List<T> items, Function<T, String> extractor) {
    if (items != null) {
      for (int i = 0; i < items.size(); i++) {
        String itemHeader = key + " " + (i + 1);
        headers.add(itemHeader);
        map.put(itemHeader, extractor.apply(items.get(i)));
      }
    }
  }


  @Override
  public NestObservationEntity findById(String docId) {
    // Retrieve entity.
    NestObservationEntity entity = super.findById(docId);
    if (entity == null) {
      return null;
    }

    entity.setNestEggList(new LinkedList<>());
    entity.setNestChickList(new LinkedList<>());

    // Rehydrate the Egg entities.
    for (EggRecordReferenceEntity eggRef : entity.getEggRecordReferenceList()) {
      NestEggEntity nestEgg = new NestEggEntity();
      entity.getNestEggList().add(nestEgg);

      nestEgg.setBirdID(eggRef.getBirdID());
      nestEgg.setRecordID(eggRef.getRecordID());

      if (eggRef.getRecordID() != null) {
        // Load and populate from the record.
        RecordEntity rec = recordService.findById(eggRef.getRecordID());
        nestEgg.setActivity(rec.getActivity());

        if (rec.getEggHealth() != null) {
          nestEgg.setCandlingAgeEstimateInDays(rec.getEggHealth().getCandlingAgeEstimateInDays());
          nestEgg.setEmbryoMoving(rec.getEggHealth().getEmbryoMoving());
          nestEgg.setLengthInMms(rec.getEggHealth().getLengthInMms());
          nestEgg.setWidthInMms(rec.getEggHealth().getWidthInMms());
          nestEgg.setHeartRate(rec.getEggHealth().getHeartRate());
          nestEgg.setTemperature(rec.getEggHealth().getTemperature());
        }

        if (rec.getWeight() != null && rec.getWeight().getWeight() != null) {
          nestEgg.setWeightInGrams(kgToGrams(rec.getWeight().getWeight()));
        }

        nestEgg.setComments(rec.getComments());
      }
    }

    // Rehydrate the Chick entities.
    for (ChickRecordReferenceEntity chickRef : entity.getChickRecordReferenceList()) {

      NestChickEntity chick = new NestChickEntity();
      entity.getNestChickList().add(chick);

      chick.setBirdID(chickRef.getBirdID());
      chick.setRecordID(chickRef.getRecordID());

      // Rehydrate from the record.
      if (chickRef.getRecordID() != null) {
        // Load and populate from the record.
        RecordEntity rec = recordService.findById(chickRef.getRecordID());

        chick.setActivity(rec.getActivity());
        chick.setComments(rec.getComments());

        if (rec.getChickHealth() != null) {
          chick.setCropStatus(rec.getChickHealth().getCropStatus());
          chick.setRespiratoryRate(rec.getChickHealth().getRespiratoryRate());
        }

        if (rec.getWeight() != null && rec.getWeight().getWeight() != null) {
          chick.setWeightInGrams(kgToGrams(rec.getWeight().getWeight()));
        }
      }

    }

    // Re-sort the eggs ...
    entity.getNestEggList().sort(Comparator.comparing(egg -> {
      String birdName = birdService.findById(egg.getBirdID()).getBirdName();
      return (birdName != null) ? birdName.toLowerCase() : "";
    }));
    //... and chicks
    entity.getNestChickList().sort(Comparator
        .comparing(chick -> birdService.findById(chick.getBirdID()).getBirdName().toLowerCase()));

    return entity;
  }

  /**
   * Convert kilogram to grams using BigDecimal to avoid floating point arithmetic issues.
   *
   * @param kg The weight in kilograms.
   * @return The weight in grams.
   */
  private float kgToGrams(float kg) {
    BigDecimal bd = new BigDecimal(kg * 1000);
    bd = bd.round(new MathContext(4));
    return bd.floatValue();
  }

  /**
   * Convert kilogram to grams using BigDecimal to avoid floating point arithmetic issues.
   *
   * @param g The weight in grams.
   * @return The weight in kilograms.
   */
  private float gramsToKg(float g) {
    BigDecimal bd = new BigDecimal(g / 1000);
    bd = bd.round(new MathContext(4));
    return bd.floatValue();
  }


  public boolean hasRelatedNestObservation(String recordID) {
    return repository.hasRelatedNestObservation(recordID);
  }

  public NestObservationEntity findByRecordID(String recordID) {
    return repository.findByRecordID(recordID);
  }

}


package com.fronde.server.migration;

import static com.fronde.server.migration.MigrationUtils.getIntOrNull;

import com.fronde.server.domain.BandsEntity;
import com.fronde.server.domain.CaptureDetailEntity;
import com.fronde.server.domain.CheckmateDataEntity;
import com.fronde.server.domain.CheckmateEntity;
import com.fronde.server.domain.ChipsEntity;
import com.fronde.server.domain.EggTimerEntity;
import com.fronde.server.domain.HealthStatusEntity;
import com.fronde.server.domain.MedicationEntity;
import com.fronde.server.domain.ObserverEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.domain.SnarkDataEntity;
import com.fronde.server.domain.SupplementaryFeedingEntity;
import com.fronde.server.domain.TransferDetailEntity;
import com.fronde.server.domain.TransmitterChangeEntity;
import com.fronde.server.services.record.CheckmateHelper;
import com.fronde.server.services.record.TransmitterHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
@Order(200)
public class RecordMigrator extends RecordBaseMigrator implements Migrator {

  @Autowired
  protected PersonMigrator personMigrator;

  private final String RECORDER = "Recorder";

  private static final Map<Integer, String> reactionMapping;

  static {
    reactionMapping = new HashMap<>();
    reactionMapping.put(0, "Calm");
    reactionMapping.put(1, "Struggling a bit");
    reactionMapping.put(2, "Struggled a lot");
    reactionMapping.put(3, "'Panting'");
  }

  private static final Map<String, String> foodTypeMapping;

  static {
    foodTypeMapping = new HashMap<>();
    foodTypeMapping.put("ka", "freeze-dried kahikatea");
    foodTypeMapping.put("ri", "freeze-dried rimu");
    foodTypeMapping.put("fv", "fruit and veges");
    foodTypeMapping.put("gp", "green pinecones");
    foodTypeMapping.put("gw", "green walnuts");
    foodTypeMapping.put("hr", "handraise food");
    // ignored
    //foodTypeMapping.put("NULL", "Harrison's HPC");
    foodTypeMapping.put("hw", "honeywater");
    foodTypeMapping.put("mu", "muesli");
    foodTypeMapping.put("nu", "nuts");
    foodTypeMapping.put("p", "pellets");
    // ignored
    //foodTypeMapping.put("NULL", "Pumpkin 2");
    foodTypeMapping.put("se", "seeds");
    foodTypeMapping.put("sp", "small pellets");
    foodTypeMapping.put("y", "Yvettes");
  }

  private static final Map<Integer, String> pulseRateMapping;

  static {
    pulseRateMapping = new HashMap<>();
    pulseRateMapping.put(1, "Normal (30 ppm)");
    pulseRateMapping.put(2, "Mated (48 ppm)");
    pulseRateMapping.put(3, "Mortality (60 ppm)");
  }

  private static final Map<Integer, String> eggTimerPulseRateMapping;

  static {
    eggTimerPulseRateMapping = new HashMap<>();
    eggTimerPulseRateMapping.put(1, "Not Incubating (30 ppm)");
    eggTimerPulseRateMapping.put(2, "Incubating (48 ppm)");
    eggTimerPulseRateMapping.put(3, "Mortality (60 ppm)");
  }


  @Override
  public void migrate() {
    super.migrate();
  }

  @Override
  protected RecordEntity readEntity(SqlRowSet rs) {
    RecordEntity entity = super.readEntity(rs);
    entity.setId(migrationUtils.convertRecordIDForRecords(entity.getRecordID()));
    readObserverList(entity);
    readBandsAndChips(entity);
    readTransmitter(entity);
    populateTransferDetailsIfRequired(entity);
    readRecordId(entity);
    populateHeathStatusIfRequired(entity);
    return entity;
  }

  private void readObserverList(RecordEntity entity) {
    // select * from [Capture Details]
    String recordID = entity.getRecordID();
    String sql = "Select * from [Capture Details] as cd where cd.[RecoveryID] = ? ";
    SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, recordID);

    // create and populate Observers
    Map<String, ObserverEntity> observerMap = new HashMap<>();
    while (sqlRowSet.next()) {
      List<ObserverEntity> observerList = createObserver(sqlRowSet);

      for (ObserverEntity observer : observerList) {
        ObserverEntity existingObserver = observerMap.get(observer.getPersonID());
        if (existingObserver == null) {
          // this person doesn't exist as an observer yet, so just add them
          observerMap.put(observer.getPersonID(), observer);
        } else {
          // we need to add the next observer's roles to the already existing observer
          mergeObserver(existingObserver, observer);
        }
      }
    }
    //Check if an observer has been set with the Recorder role.
    Boolean isRecorderSet = observerMap.values().stream()
        .anyMatch(roles -> roles.getObservationRoles().contains(RECORDER));
    //If not set then set observer on record entity as recorder.
    if (!isRecorderSet && !observerMap.isEmpty() && entity.getOldObserver() != null) {
      String personId = migrationUtils.convertPersonName(entity.getOldObserver());
      //If person already exists in observerMap give them Recorder role
      if (observerMap.containsKey(personId)) {
        observerMap.get(personId).getObservationRoles().add(RECORDER);
      }
      //Otherwise add person to the observerMap
      else {
        ObserverEntity observer = createObserver(
            migrationUtils.convertPersonName(entity.getOldObserver()), RECORDER);
        observerMap.put(observer.getPersonID(), observer);
      }
    }

    //If observerMap is empty then it is not a capture record. Add observer on record to observerList.
    if (observerMap.isEmpty() && entity.getOldObserver() != null) {
      ObserverEntity observer = createObserver(
          migrationUtils.convertPersonName(entity.getOldObserver()), RECORDER);
      observerMap.put(observer.getPersonID(), observer);
    }

    entity.setObserverList(new ArrayList<>(observerMap.values()));
  }

  private void readBandsAndChips(RecordEntity entity) {
    // select * from [BandChange]
    String recordID = entity.getRecordID();
    String sql = "Select * from [BandChange] as bc where bc.[RecoveryID] = ? ";
    SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, recordID);

    // if band or chip is present set it in record
    BandsEntity band = new BandsEntity();
    ChipsEntity chip = new ChipsEntity();
    while (sqlRowSet.next()) {
      if (sqlRowSet.getString("NewBandNumber") != null) {
        band.setNewBandNumber(sqlRowSet.getString("NewBandNumber"));
        Float legCode = sqlRowSet.getFloat("Leg");
        String leg = "";

        switch (legCode.intValue()) {
          case 0:
            leg = "Unknown";
            break;
          case 1:
            leg = "Left";
            break;
          case 2:
            leg = "Right";
            break;
          default:
            break;
        }

        band.setLeg(leg);
      } else {
        chip.setMicrochip(sqlRowSet.getString("Microchip"));
      }
    }
    if (band.getNewBandNumber() != null) {
      entity.setBands(band);
    }
    if (chip.getMicrochip() != null) {
      entity.setChips(chip);
    }
  }

  //TODO: Refactor, txDocId and transmitterId in record might not be needed anymore.
  private void readTransmitter(RecordEntity entity) {
    if (entity.getTransmitterChange() != null) {
      String txid = entity.getTransmitterChange().getTxId();
      String transmitterDocId = migrationUtils.convertTxId(txid);

      entity.setTransmitterId(transmitterDocId);

      TransmitterChangeEntity tc = entity.getTransmitterChange();

      tc.setTxDocId(transmitterDocId);

      tc.setTxTo(migrationUtils.convertTxId(tc.getTxTo()));
      tc.setTxFrom(migrationUtils.convertTxId(tc.getTxFrom()));
      entity.setTransmitterChange(tc);
    }
  }

  private void mergeObserver(ObserverEntity existingObserver, ObserverEntity observer) {
    for (String role : observer.getObservationRoles()) {
      if (!existingObserver.getObservationRoles().contains(role)) {
        existingObserver.getObservationRoles().add(role);
      }
    }
  }

  private List<ObserverEntity> createObserver(SqlRowSet sqlRowSet) {
    List<ObserverEntity> observerList = new ArrayList<>();
    createObserver(observerList, sqlRowSet.getString("Recorder1"), "Inspector");
    createObserver(observerList, sqlRowSet.getString("Recorder2"), RECORDER);
    createObserver(observerList, sqlRowSet.getString("Holder"), "Holder");
    return observerList;
  }

  private void createObserver(List<ObserverEntity> observerList, String personName,
      String roleName) {
    if (personName != null) {
//            String personId = personMigrator.getPersonNameMap().get(personName);

      String personId = migrationUtils.convertPersonName(personName);

      if (personId != null) {
        observerList.add(createObserver(personId, roleName));
      } else {
        System.out.println("WARNING: No person found for person name of: " + personName + ".");
      }
    }
    // else no name so no need to add an observer for this column
  }

  private ObserverEntity createObserver(String personId, String role) {
    ObserverEntity observer = new ObserverEntity();
    observer.setPersonID(personId);
    observer.setObservationRoles(new ArrayList<>());
    observer.getObservationRoles().add(role);

    return observer;
  }

  @Override
  protected CaptureDetailEntity readCaptureDetail(String parentID) {
    CaptureDetailEntity cd = super.readCaptureDetail(parentID);
    if (cd != null && cd.getOldReaction() != null) {
      cd.setReaction(reactionMapping.get(cd.getOldReaction()));
    }
    return cd;
  }

  @Override
  protected SupplementaryFeedingEntity readSupplementaryFeeding(String parentID) {
    SupplementaryFeedingEntity entity = super.readSupplementaryFeeding(parentID);
    if (entity != null) {
      // turn oldFoodType string into list of strings
      String oldFoodTypeString = entity.getOldFoodType();
      if (oldFoodTypeString != null) {
        // strip quotes, brackets, and spaces to leave <food type code>, <food type code> ...
        oldFoodTypeString = oldFoodTypeString.replaceAll("[\\(\\)' ]", "");
        // split string using ',' as delimiter
        String[] foodTypeCodesArray = oldFoodTypeString.split(",");
        String[] foodTypeCodes = foodTypeCodesArray;
        List<String> foodTypes = new ArrayList<>();
        for (String code : foodTypeCodes) {
          String foodType = foodTypeMapping.get(code);
          if (foodType != null) {
            foodTypes.add(foodType);
          } else {
            foodTypes.add("Unknown code '" + code + "'");
            System.out.println("Unknown food type code '" + code + "'");
          }
        }
        entity.setFoodTypes(foodTypes);
      }
    }
    return entity;
  }

  @Override
  protected CheckmateEntity readCheckmate(String parentID) {
    List<CheckmateEntity> list = jdbcTemplate.query(
        "SELECT * FROM [checkmate] c INNER JOIN [recovery] r ON c.RecoveryId = r.rocoveryid WHERE c.[Recoveryid] = ? ",
        new Object[]{parentID},
        (rs, rowNum) -> {
          CheckmateEntity entity = new CheckmateEntity();

          entity.setBattery1(getIntOrNull(rs, "battery1"));
          entity.setBattery2(getIntOrNull(rs, "battery2"));
          entity.setPulseRate(pulseRateMapping.get(getIntOrNull(rs, "Pulse rate")));
          entity.setRecoveryid(rs.getString("Recoveryid"));

          // Determine if this is Errol.
          boolean isErrol = rs.getInt("Errol") == 1;
          entity.setDataCaptureType(isErrol ? "From Errol" : "Manual");

          entity.setCheckmateDataList(new LinkedList<CheckmateDataEntity>());

          // There can be up to four records for the checkmate entry.
          if (!isErrol) {
            for (int i = 1; i < 5; i++) {
              CheckmateDataEntity child = new CheckmateDataEntity();
              child.setRecoveryid(parentID);

              // Iterate through and set the other fields.
              child.setDuration1(getIntOrNull(rs, i + "duration1"));
              child.setDuration2(getIntOrNull(rs, i + "duration2"));
              child.setFemaleTx1(getIntOrNull(rs, i + "femaletx1"));
              child.setFemaleTx2(getIntOrNull(rs, i + "femaletx2"));
              child.setQuality1(getIntOrNull(rs, i + "qual1"));
              child.setQuality2(getIntOrNull(rs, i + "qual2"));
              child.setTime1(getIntOrNull(rs, i + "time1"));
              child.setTime2(getIntOrNull(rs, i + "time2"));
              child.setBirdId(migrationUtils.convertBirdID(rs.getString("bird" + i + "id")));

              // Derive the resultant fields
              child.setQuality(
                  TransmitterHelper.deriveValue(child.getQuality1(), child.getQuality2()));
              child.setDuration(
                  TransmitterHelper.deriveValue(child.getDuration1(), child.getDuration2()));
              child.setFemaleTx(
                  TransmitterHelper.deriveValue(child.getFemaleTx1(), child.getFemaleTx2()));
              child.setTime(CheckmateHelper.deriveTime(rs.getTimestamp("DateTime"),
                  TransmitterHelper.deriveValue(child.getTime1(), child.getTime2())));

              // Add the child if not empty.
              if (!isCheckmateDataEmpty(child)) {
                entity.getCheckmateDataList().add(child);
              }
            }
          } else {
            // Errol processing.

            for (int i = 1; i < 5; i++) {
              CheckmateDataEntity child = new CheckmateDataEntity();
              child.setRecoveryid(parentID);

              // Iterate through and set the other fields.
              child.setDuration(getIntOrNull(rs, "E" + i + "Duration"));
              child.setQuality(getIntOrNull(rs, "E" + i + "Quality"));
              child.setFemaleTx(getIntOrNull(rs, "E" + i + "femaletxno"));
              child.setTime(rs.getTimestamp("E" + i + "DateTime"));
              child.setBirdId(migrationUtils.convertBirdID(rs.getString("bird" + i + "id")));

              if (child.getDuration() != null) {
                entity.getCheckmateDataList().add(child);
              }
            }
          }

          return entity;
        }
    );

    return MigrationUtils.optionalResult(list);
  }

  private boolean isCheckmateDataEmpty(CheckmateDataEntity checkmate) {
    return checkmate.getDuration1() == null
        && checkmate.getDuration2() == null
        && checkmate.getFemaleTx1() == null
        && checkmate.getFemaleTx2() == null
        && checkmate.getQuality1() == null
        && checkmate.getQuality2() == null
        && checkmate.getTime1() == null
        && checkmate.getTime2() == null;
  }

  private void populateTransferDetailsIfRequired(RecordEntity entity) {
    if ("Transfer".equals(entity.getRecordType())) {
      TransferDetailEntity td = new TransferDetailEntity();
      td.setTransferToIsland(entity.getIsland());
      entity.setTransferDetail(td);
    }
  }

  private void readRecordId(RecordEntity entity) {
    String id = migrationUtils.convertRecordID(entity.getRecordID());
    if (id != null) {
      entity.setId(id);
    }
  }

  protected SnarkDataEntity readSnarkData(String parentID) {
    SnarkDataEntity entity = super.readSnarkData(parentID);
    if (entity != null) {
      // use the method that creates ids due to the order of loading records before snark activities
      entity.setSnarkActivityID(
          migrationUtils.convertTAndBRecIdForSnarkActivities(entity.getOldTAndBRecId()));
    }
    return entity;
  }

  protected EggTimerEntity readEggTimer(String parentID) {
    EggTimerEntity entity = super.readEggTimer(parentID);
    if (entity != null) {
      entity.setPulseRate(eggTimerPulseRateMapping.get(entity.getOldPulseRate()));
      boolean isErrol = jdbcTemplate
          .queryForObject("SELECT Errol FROM [Recovery] WHERE rocoveryid = ?",
              new Object[]{parentID}, Boolean.class);
      entity.setDataCaptureType(isErrol ? "From Errol" : "Manual");

      // Process the values.
      entity.setDaysSinceChange(TransmitterHelper
          .deriveValue(entity.getDaysSinceChange1(), entity.getDaysSinceChange2()));
      entity.setDurationOfPrevious(TransmitterHelper
          .deriveValue(entity.getDurationOfPrevious1(), entity.getDurationOfPrevious2()));
      entity.setMeanActivity(
          TransmitterHelper.deriveValue(entity.getMeanActivity1(), entity.getMeanActivity2()));

      // Activity over days.
      entity.setActivityYesterday(TransmitterHelper
          .deriveValue(entity.getActivityYesterday1(), entity.getActivityYesterday2()));
      entity.setActivityYesterday(TransmitterHelper
          .deriveValue(entity.getActivityYesterday1(), entity.getActivityYesterday2()));
      entity.setActivityYesterday(TransmitterHelper
          .deriveValue(entity.getActivityYesterday1(), entity.getActivityYesterday2()));
      entity.setActivityYesterday(TransmitterHelper
          .deriveValue(entity.getActivityYesterday1(), entity.getActivityYesterday2()));
      entity.setActivityYesterday(TransmitterHelper
          .deriveValue(entity.getActivityYesterday1(), entity.getActivityYesterday2()));
      entity.setActivity2DaysAgo(TransmitterHelper
          .deriveValue(entity.getActivity2DaysAgo1(), entity.getActivity2DaysAgo2()));
      entity.setActivity3DaysAgo(TransmitterHelper
          .deriveValue(entity.getActivity3DaysAgo1(), entity.getActivity3DaysAgo2()));
      entity.setActivity4DaysAgo(TransmitterHelper
          .deriveValue(entity.getActivity4DaysAgo1(), entity.getActivity4DaysAgo2()));
      entity.setActivity5DaysAgo(TransmitterHelper
          .deriveValue(entity.getActivity5DaysAgo1(), entity.getActivity5DaysAgo2()));
      entity.setActivity6DaysAgo(TransmitterHelper
          .deriveValue(entity.getActivity6DaysAgo1(), entity.getActivity6DaysAgo2()));
      entity.setActivity7DaysAgo(TransmitterHelper
          .deriveValue(entity.getActivity7DaysAgo1(), entity.getActivity7DaysAgo2()));

    }
    return entity;
  }

  protected void populateHeathStatusIfRequired(RecordEntity entity) {
    if (entity.getOldBoomingIntensity() != null || entity.getOldHealthStatus() != null) {
      HealthStatusEntity hs = new HealthStatusEntity();
      hs.setBoomingIntensity(entity.getOldBoomingIntensity());
      hs.setHealthStatus(entity.getOldHealthStatus());
      entity.setHealthStatus(hs);
    }
  }

  @Override
  protected List<MedicationEntity> readMedicationList(String parentID) {
    // only return a list if there is actual data (special case for records as not every record should hold this data)
    List<MedicationEntity> result = super.readMedicationList(parentID);
    if (result == null || result.isEmpty()) {
      return null;
    } else {
      return result;
    }
  }

}

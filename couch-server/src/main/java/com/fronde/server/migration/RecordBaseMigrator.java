package com.fronde.server.migration;

import static com.fronde.server.migration.MigrationUtils.getBooleanOrNull;
import static com.fronde.server.migration.MigrationUtils.getFloatOrNull;
import static com.fronde.server.migration.MigrationUtils.getIntOrNull;

import com.fronde.server.domain.BatteryLifeEntity;
import com.fronde.server.domain.CaptureDetailEntity;
import com.fronde.server.domain.CheckmateEntity;
import com.fronde.server.domain.EggTimerEntity;
import com.fronde.server.domain.HandRaiseEntity;
import com.fronde.server.domain.HarnessChangeEntity;
import com.fronde.server.domain.HealthCheckEntity;
import com.fronde.server.domain.MeasureDetailEntity;
import com.fronde.server.domain.MedicationEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.domain.SnarkDataEntity;
import com.fronde.server.domain.SupplementaryFeedingEntity;
import com.fronde.server.domain.TransmitterChangeEntity;
import com.fronde.server.domain.TxActivityEntity;
import com.fronde.server.domain.WeightEntity;
import com.fronde.server.services.common.ObjectIdFactory;
import com.fronde.server.services.record.RecordRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public abstract class RecordBaseMigrator implements Migrator {


  @Autowired
  protected MigrationUtils migrationUtils;

  @Autowired
  protected JdbcTemplate jdbcTemplate;

  @Autowired
  protected RecordRepository repository;

  @Autowired
  protected ProgressMonitor progressMonitor;

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  public void migrate() {
    progressMonitor.reset(RecordEntity.class);
    progressMonitor.println("started");

    SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT r.* FROM [Recovery] r where " +
        // exclude tx add records as these are not true bird records
        "not (r.[RecoveryType] = 'tx add') and " +
        // exclude records associated with nest observations for now
        "not (exists (select null from [nest observations] no where no.[Nest obs id] = r.[RocoveryID]))");
    while (sqlRowSet.next()) {
      RecordEntity entity = readEntity(sqlRowSet);
      migrationUtils.createRevision(entity);
      repository.save(entity);
      progressMonitor.tick();
    }

    progressMonitor.end();
    progressMonitor.println("save ok");
  }

  protected RecordEntity readEntity(SqlRowSet rs) {
    RecordEntity entity = new RecordEntity();

    entity.setActivity(rs.getString("Activity"));
    entity.setBirdCert(rs.getString("BirdCert"));
    entity.setBirdID(migrationUtils.convertBirdIDForBirds(rs.getString("BirdID")));
    entity.setBooming(getIntOrNull(rs, "Booming"));
    entity.setComments(rs.getString("Comments"));
    entity.setDateTime(rs.getTimestamp("DateTime"));
    entity.setDocType("Record");
    entity.setDuration(getIntOrNull(rs, "Duration"));
    entity.setEasting(getFloatOrNull(rs, "Easting"));
    entity.setEntryDateTime(rs.getTimestamp("EntryDateTime"));
    entity.setErrol(getBooleanOrNull(rs, "Errol"));
    entity.setFirstArriveTime(rs.getTimestamp("First arrive time"));
    entity.setId(objectIdFactory.create());
    entity.setIsland(rs.getString("Island"));
    entity.setLastDepartTime(rs.getTimestamp("Last depart time"));
    entity.setLocationID(migrationUtils.convertLocationName(rs.getString("Location Name")));
    entity.setMappingMethod(rs.getString("MapMethod"));
    entity.setModifiedByPersonId(migrationUtils.getModifiedByPersonId());
    entity.setModifiedTime(migrationUtils.getModifiedTime());
    entity.setNesting(getIntOrNull(rs, "Nesting"));
    entity.setNorthing(getFloatOrNull(rs, "Northing"));
    entity.setNumberOfVisits(getIntOrNull(rs, "Number of visits"));
    entity.setObsQuality(rs.getString("ObsQuality"));
    entity.setOldBirdID(rs.getString("BirdID"));
    entity.setOldBoomingIntensity(getIntOrNull(rs, "Booming Intensity"));
    entity.setOldHealthStatus(rs.getString("Health status"));
    entity.setOldLocationDescription(rs.getString("Location Description"));
    entity.setOldLocationName(rs.getString("Location Name"));
    entity.setOldObserver(rs.getString("Observer"));
    entity.setReason(rs.getString("Reason"));
    entity.setRecordID(rs.getString("rocoveryID"));
    entity.setRecordType(rs.getString("recoveryType"));
    // Skip migration for: revision
    entity.setSignificantEvent(getBooleanOrNull(rs, "SignificantEvent"));
    entity.setSubReason(rs.getString("SubReason"));
    entity.setSupFed(getIntOrNull(rs, "Sup fed"));
    // Skip migration for: transmitterId
    // Skip migration for: bands
    entity.setBatteryLife(readBatteryLife(rs.getString("RocoveryID")));
    // Skip migration for: bloodSampleList
    entity.setCaptureDetail(readCaptureDetail(rs.getString("RocoveryID")));
    entity.setCheckmate(readCheckmate(rs.getString("RocoveryID")));
    // Skip migration for: chickHealth
    // Skip migration for: chips
    // Skip migration for: eggHealth
    entity.setEggTimer(readEggTimer(rs.getString("RocoveryID")));
    entity.setHandRaise(readHandRaise(rs.getString("RocoveryID")));
    entity.setHarnessChange(readHarnessChange(rs.getString("RocoveryID")));
    entity.setHealthCheck(readHealthCheck(rs.getString("RocoveryID")));
    // Skip migration for: healthStatus
    // Skip migration for: locationBearingList
    entity.setMeasureDetail(readMeasureDetail(rs.getString("RocoveryID")));
    entity.setMedicationList(readMedicationList(rs.getString("RocoveryID")));
    // Skip migration for: observerList
    // Skip migration for: otherSampleList
    entity.setSnarkData(readSnarkData(rs.getString("RocoveryID")));
    // Skip migration for: spermSampleList
    entity.setSupplementaryFeeding(readSupplementaryFeeding(rs.getString("RocoveryID")));
    // Skip migration for: swabSampleList
    // Skip migration for: transferDetail
    entity.setTransmitterChange(readTransmitterChange(rs.getString("RocoveryID")));
    entity.setTxActivity(readTxActivity(rs.getString("RocoveryID")));
    entity.setWeight(readWeight(rs.getString("RocoveryID")));

    return entity;
  }

  protected BatteryLifeEntity readBatteryLife(String parentID) {
    List<BatteryLifeEntity> list = jdbcTemplate.query(
        "SELECT * FROM [Sky Ranger] as t WHERE t.[Recoveryid] = ? ", new Object[]{parentID},
        (rs, rowNum) -> {
          BatteryLifeEntity entity = new BatteryLifeEntity();

          entity.setBattLifeWeeks(getIntOrNull(rs, "BattLifeWeeks"));
          entity.setRecoveryid(rs.getString("Recoveryid"));
          return entity;
        }
    );

    return MigrationUtils.optionalResult(list);
  }

  protected CaptureDetailEntity readCaptureDetail(String parentID) {
    List<CaptureDetailEntity> list = jdbcTemplate.query(
        "SELECT * FROM [Capture Details] as t WHERE t.[RecoveryID] = ? ", new Object[]{parentID},
        (rs, rowNum) -> {
          CaptureDetailEntity entity = new CaptureDetailEntity();

          entity.setCaptureMethod(rs.getString("Capture Method"));
          entity.setDuration(getFloatOrNull(rs, "Duration"));
          entity.setEyesClosed(getBooleanOrNull(rs, "Eyes Closed"));
          entity.setHeadDown(getBooleanOrNull(rs, "Head Down"));
          entity.setOldHolder(rs.getString("Holder"));
          entity.setOldReaction(getIntOrNull(rs, "reaction"));
          entity.setOldRecorder1(rs.getString("Recorder1"));
          entity.setOldRecorder2(rs.getString("Recorder2"));
          // Skip migration for: reaction
          entity.setRecoveryID(rs.getString("RecoveryID"));
          entity.setResponse(rs.getString("Response"));
          entity.setShivered(getBooleanOrNull(rs, "Shivered"));
          return entity;
        }
    );

    return MigrationUtils.optionalResult(list);
  }

  protected CheckmateEntity readCheckmate(String parentID) {
    List<CheckmateEntity> list = jdbcTemplate.query(
        "SELECT * FROM [checkmate] as t WHERE t.[Recoveryid] = ? ", new Object[]{parentID},
        (rs, rowNum) -> {
          CheckmateEntity entity = new CheckmateEntity();

          entity.setBattery1(getIntOrNull(rs, "battery1"));
          entity.setBattery2(getIntOrNull(rs, "battery2"));
          // Skip migration for: dataCaptureType
          // Skip migration for: pulseRate
          entity.setRecoveryid(rs.getString("Recoveryid"));
          // Skip migration for: checkmateDataList
          return entity;
        }
    );

    return MigrationUtils.optionalResult(list);
  }

  protected EggTimerEntity readEggTimer(String parentID) {
    List<EggTimerEntity> list = jdbcTemplate.query(
        "SELECT * FROM [Egg timer] as t WHERE t.[recoveryid] = ? ", new Object[]{parentID},
        (rs, rowNum) -> {
          EggTimerEntity entity = new EggTimerEntity();

          // Skip migration for: activity2DaysAgo
          entity.setActivity2DaysAgo1(getIntOrNull(rs, "Activity 2 days ago 1"));
          entity.setActivity2DaysAgo2(getIntOrNull(rs, "Activity 2 days ago 2"));
          // Skip migration for: activity3DaysAgo
          entity.setActivity3DaysAgo1(getIntOrNull(rs, "Activity 3 days ago 1"));
          entity.setActivity3DaysAgo2(getIntOrNull(rs, "Activity 3 days ago 2"));
          // Skip migration for: activity4DaysAgo
          entity.setActivity4DaysAgo1(getIntOrNull(rs, "Activity 4 days ago 1"));
          entity.setActivity4DaysAgo2(getIntOrNull(rs, "Activity 4 days ago 2"));
          // Skip migration for: activity5DaysAgo
          entity.setActivity5DaysAgo1(getIntOrNull(rs, "Activity 5 days ago 1"));
          entity.setActivity5DaysAgo2(getIntOrNull(rs, "Activity 5 days ago 2"));
          // Skip migration for: activity6DaysAgo
          entity.setActivity6DaysAgo1(getIntOrNull(rs, "Activity 6 days ago 1"));
          entity.setActivity6DaysAgo2(getIntOrNull(rs, "Activity 6 days ago 2"));
          // Skip migration for: activity7DaysAgo
          entity.setActivity7DaysAgo1(getIntOrNull(rs, "Activity 7 days ago 1"));
          entity.setActivity7DaysAgo2(getIntOrNull(rs, "Activity 7 days ago 2"));
          // Skip migration for: activityYesterday
          entity.setActivityYesterday1(getIntOrNull(rs, "Activity yesterday 1"));
          entity.setActivityYesterday2(getIntOrNull(rs, "Activity yesterday 2"));
          entity.setBatteryLife1(getIntOrNull(rs, "Battery life 1"));
          entity.setBatteryLife2(getIntOrNull(rs, "Battery life 2"));
          // Skip migration for: dataCaptureType
          // Skip migration for: daysSinceChange
          entity.setDaysSinceChange1(getIntOrNull(rs, "Days since change 1"));
          entity.setDaysSinceChange2(getIntOrNull(rs, "Days since change 2"));
          // Skip migration for: durationOfPrevious
          entity.setDurationOfPrevious1(getIntOrNull(rs, "Duration of previous 1"));
          entity.setDurationOfPrevious2(getIntOrNull(rs, "Duration of previuos 2"));
          entity.setErrolActivity1Day(getIntOrNull(rs, "EActivity 1 Day"));
          entity.setErrolActivity7Days(getIntOrNull(rs, "EActivity 7 Days"));
          entity.setErrolDaysSinceChange(getIntOrNull(rs, "EDaysSinceChange"));
          entity.setErrolID(getIntOrNull(rs, "ErrolID"));
          // Skip migration for: meanActivity
          entity.setMeanActivity1(getIntOrNull(rs, "Mean activity 1"));
          entity.setMeanActivity2(getIntOrNull(rs, "Mean activity 2"));
          entity.setOldPulseRate(getIntOrNull(rs, "Pulse rate"));
          // Skip migration for: pulseRate
          entity.setRecoveryid(rs.getString("Recoveryid"));
          return entity;
        }
    );

    return MigrationUtils.optionalResult(list);
  }

  protected HandRaiseEntity readHandRaise(String parentID) {
    List<HandRaiseEntity> list = jdbcTemplate.query(
        "SELECT * FROM [Handraise data] as t WHERE t.[recoveryid] = ? ", new Object[]{parentID},
        (rs, rowNum) -> {
          HandRaiseEntity entity = new HandRaiseEntity();

          entity.setAmountFed(getIntOrNull(rs, "Amount fed"));
          entity.setBrooderType(rs.getString("Brooder type"));
          entity.setDietaryFibre(getIntOrNull(rs, "% Dietary Fibre"));
          entity.setHandRearingFormula(rs.getString("Hand Rearing Formula"));
          entity.setLargestMeal(getIntOrNull(rs, "Largest meal"));
          entity.setMedication(getBooleanOrNull(rs, "Medication"));
          entity.setNewRecord(rs.getString("new record"));
          entity.setNumberOfFeeds(getIntOrNull(rs, "Number of feeds"));
          entity.setPercentSolids(getIntOrNull(rs, "percent solids"));
          entity.setRecoveryid(rs.getString("recoveryid"));
          entity.setRelativeHumidity(getIntOrNull(rs, "Relative Humidity"));
          entity.setTemp(getFloatOrNull(rs, "Temp"));
          return entity;
        }
    );

    return MigrationUtils.optionalResult(list);
  }

  protected HarnessChangeEntity readHarnessChange(String parentID) {
    List<HarnessChangeEntity> list = jdbcTemplate.query(
        "SELECT * FROM [TxChange] as t WHERE t.[RecoveryID] = ? ", new Object[]{parentID},
        (rs, rowNum) -> {
          HarnessChangeEntity entity = new HarnessChangeEntity();

          entity.setNewHarnessLength(getIntOrNull(rs, "new harness length"));
          entity.setOldHarnessLengthRight(getIntOrNull(rs, "old harness length right"));
          entity.setOldHarnessLengthLeft(getIntOrNull(rs, "old harness length left"));
          entity.setTxtranstype(rs.getString("txtranstype"));
          return entity;
        }
    );

    if (!list.isEmpty()) {
      return MigrationUtils.getHarnessChangeForRecord(list);
    }
    return null;
  }

  protected HealthCheckEntity readHealthCheck(String parentID) {
    List<HealthCheckEntity> list = jdbcTemplate.query(
        "SELECT * FROM [Health checks] as t WHERE t.[Recoveryid] = ? ", new Object[]{parentID},
        (rs, rowNum) -> {
          HealthCheckEntity entity = new HealthCheckEntity();

          entity.setBand(getIntOrNull(rs, "Band"));
          entity.setBill(getIntOrNull(rs, "Bill"));
          entity.setBillNotes(rs.getString("Bill notes"));
          entity.setBoomSac(getBooleanOrNull(rs, "Boom Sac"));
          entity.setBroodPatch(getBooleanOrNull(rs, "Brood Patch"));
          entity.setCereAndNostrils(getIntOrNull(rs, "Cere and nostrils"));
          entity.setCereNotes(rs.getString("Cere notes"));
          entity.setCloaca(getIntOrNull(rs, "Cloaca"));
          entity.setCloacaNotes(rs.getString("Cloaca notes"));
          entity.setCloacaScore(getFloatOrNull(rs, "Cloaca score"));
          entity.setEars(getIntOrNull(rs, "Ears"));
          entity.setEarsNotes(rs.getString("Ears notes"));
          entity.setEyes(getIntOrNull(rs, "Eyes"));
          entity.setEyesNotes(rs.getString("Eyes notes"));
          entity.setFeetAndToes(getIntOrNull(rs, "Feet and toes"));
          entity.setFeetNotes(rs.getString("Feet notes"));
          entity.setInMoult(getBooleanOrNull(rs, "In Moult"));
          entity.setLegs(getIntOrNull(rs, "Legs"));
          entity.setLegsNotes(rs.getString("Legs notes"));
          entity.setMicrochip(getIntOrNull(rs, "Microchip"));
          entity.setMicrochipNotes(rs.getString("Microchip notes"));
          entity.setMicrochipNumber(getIntOrNull(rs, "Microchip number"));
          entity.setPlumage(getIntOrNull(rs, "Plumage"));
          entity.setPlumageNotes(rs.getString("Plumage notes"));
          entity.setRecoveryid(rs.getString("Recoveryid"));
          entity.setTransmitter(getIntOrNull(rs, "Transmitter"));
          entity.setTransmitterNotes(rs.getString("Transmitter notes"));
          entity.setVent(getIntOrNull(rs, "Vent"));
          entity.setVentNotes(rs.getString("Vent notes"));
          entity.setVentScore(getFloatOrNull(rs, "Vent score"));
          return entity;
        }
    );

    return MigrationUtils.optionalResult(list);
  }

  protected MeasureDetailEntity readMeasureDetail(String parentID) {
    List<MeasureDetailEntity> list = jdbcTemplate.query(
        "SELECT * FROM [Measures] as t WHERE t.[RecoveryID] = ? ", new Object[]{parentID},
        (rs, rowNum) -> {
          MeasureDetailEntity entity = new MeasureDetailEntity();

          entity.setCulmenAndCere(getFloatOrNull(rs, "Exposed Culmen"));
          entity.setCulmenLength(getFloatOrNull(rs, "Culmen Only"));
          entity.setCulmenWidth(getFloatOrNull(rs, "Gape"));
          entity.setFemur(getIntOrNull(rs, "Femur"));
          entity.setLeg(rs.getString("Leg"));
          entity.setLongestToe(getFloatOrNull(rs, "Longest Toe"));
          entity.setLongToeClaw(getFloatOrNull(rs, "Long Toe Claw"));
          entity.setRecoveryID(rs.getString("RecoveryID"));
          entity.setSternumshoulder(getIntOrNull(rs, "Sternum-shoulder"));
          entity.setTailLength(getFloatOrNull(rs, "Tail Length"));
          entity.setTarsusDepthSquashed(getFloatOrNull(rs, "Tarsus Depth Squashed"));
          entity.setTarsusDepthUnsquashed(getFloatOrNull(rs, "Tarsus Depth Unsquashed"));
          entity.setTarsusLength(getFloatOrNull(rs, "Tarsus Length"));
          entity.setTarsusWidthSquashed(getFloatOrNull(rs, "Tarsus Width Squashed"));
          entity.setTarsusWidthUnsquashed(getFloatOrNull(rs, "Tarsus Width Unsquashed"));
          entity.setWingLength(getFloatOrNull(rs, "Wing Length"));
          return entity;
        }
    );

    return MigrationUtils.optionalResult(list);
  }

  protected List<MedicationEntity> readMedicationList(String parentID) {
    return jdbcTemplate.query(
        "SELECT * FROM [Drugs] as t WHERE t.[recoveryid] = ? ", new Object[]{parentID},
        (rs, rowNum) -> {
          MedicationEntity entity = new MedicationEntity();

          entity.setConcentrationUnits(rs.getString("Concentration units"));
          entity.setConcentrationValue(getFloatOrNull(rs, "Concentration value"));
          entity.setCourseLength(getIntOrNull(rs, "Course length"));
          entity.setDayNumber(getIntOrNull(rs, "Day number"));
          entity.setDoseRateUnits(rs.getString("Dose rate units"));
          entity.setDoseRateValue(getFloatOrNull(rs, "Dose rate value"));
          entity.setDrug(rs.getString("Drug"));
          entity.setRecoveryid(rs.getString("recoveryid"));
          entity.setRoute(rs.getString("Route"));
          entity.setTimesPerDay(rs.getString("Times per day"));
          return entity;
        }
    );
  }

  protected SnarkDataEntity readSnarkData(String parentID) {
    List<SnarkDataEntity> list = jdbcTemplate.query(
        "SELECT * FROM [snark records] as t WHERE t.[Snarkrecid] = ? ", new Object[]{parentID},
        (rs, rowNum) -> {
          SnarkDataEntity entity = new SnarkDataEntity();

          entity.setArriveDateTime(rs.getTimestamp("ArriveDateTime"));
          entity.setDepartDateTime(rs.getTimestamp("DepartDateTime"));
          entity.setMating(getBooleanOrNull(rs, "Mating"));
          entity.setOldSnarkRecId(rs.getString("Snarkrecid"));
          entity.setOldTAndBRecId(rs.getString("tandbrecid"));
          // Skip migration for: snarkActivityID
          return entity;
        }
    );

    return MigrationUtils.optionalResult(list);
  }

  protected SupplementaryFeedingEntity readSupplementaryFeeding(String parentID) {
    List<SupplementaryFeedingEntity> list = jdbcTemplate.query(
        "SELECT * FROM [SupFood] as t WHERE t.[RecoveryId] = ? ", new Object[]{parentID},
        (rs, rowNum) -> {
          SupplementaryFeedingEntity entity = new SupplementaryFeedingEntity();

          entity.setFeedingRegime(rs.getString("FeedingRegime"));
          // Skip migration for: foodTypes
          entity.setOldFoodType(rs.getString("FoodType"));
          entity.setRecoveryId(rs.getString("RecoveryId"));
          return entity;
        }
    );

    return MigrationUtils.optionalResult(list);
  }

  protected TransmitterChangeEntity readTransmitterChange(String parentID) {
    List<TransmitterChangeEntity> list = jdbcTemplate.query(
        "SELECT * FROM [TxChange] as t WHERE t.[RecoveryID] = ? ", new Object[]{parentID},
        (rs, rowNum) -> {
          TransmitterChangeEntity entity = new TransmitterChangeEntity();

          // Skip migration for: addedTxFromLastRecordID
          // Skip migration for: addedTxFromStatus
          // Skip migration for: addedTxFromTxFineTune
          entity.setHarnessChangeOnly(getBooleanOrNull(rs, "harness change only"));
          entity.setHoursOff(getFloatOrNull(rs, "hours off"));
          entity.setHoursOn(getFloatOrNull(rs, "hours on"));
          entity.setNewStatus(rs.getString("txtranstype"));
          entity.setNewTxFineTune(rs.getString("NewTxFineTune"));
          entity.setProx(getBooleanOrNull(rs, "Prox"));
          entity.setProxOn(getBooleanOrNull(rs, "Prox on"));
          entity.setRecoveryID(rs.getString("RecoveryID"));
          entity.setRemoved(getBooleanOrNull(rs, "Removed"));
          // Skip migration for: removedTxFromLastRecordID
          // Skip migration for: removedTxFromStatus
          // Skip migration for: starttime
          entity.setTxchangeid(rs.getString("txchangeid"));
          // Skip migration for: txDocId
          // Skip migration for: txFrom
          // Skip migration for: txFromStatus
          entity.setTxId(rs.getString("TxId"));
          entity.setTxLifeExpectancyWeeks(getIntOrNull(rs, "TxLifeExpectancyWeeks"));
          entity.setTxMortality(getFloatOrNull(rs, "TxMortality"));
          // Skip migration for: txTo
          entity.setVhfHoursOn(getFloatOrNull(rs, "vhf hours on"));
          return entity;
        }
    );

    if (!list.isEmpty()) {
      return MigrationUtils.getTransmitterChangeForRecord(list);
    }
    return null;
  }

  protected TxActivityEntity readTxActivity(String parentID) {
    List<TxActivityEntity> list = jdbcTemplate.query(
        "SELECT * FROM [tx activity] as t WHERE t.[Recoveryid] = ? ", new Object[]{parentID},
        (rs, rowNum) -> {
          TxActivityEntity entity = new TxActivityEntity();

          entity.setAlg1Activity2DaysAgo1(getIntOrNull(rs, "1st Activity 2 days ago 1"));
          entity.setAlg1Activity2DaysAgo2(getIntOrNull(rs, "1st Activity 2 days ago 2"));
          entity.setAlg1Activity3DaysAgo1(getIntOrNull(rs, "1st Activity 3 days ago 1"));
          entity.setAlg1Activity3DaysAgo2(getIntOrNull(rs, "1st Activity 3 days ago 2"));
          entity.setAlg1Activity4DaysAgo1(getIntOrNull(rs, "1st Activity 4 days ago 1"));
          entity.setAlg1Activity4DaysAgo2(getIntOrNull(rs, "1st Activity 4 days ago 2"));
          entity.setAlg1Activity5DaysAgo1(getIntOrNull(rs, "1st Activity 5 days ago 1"));
          entity.setAlg1Activity5DaysAgo2(getIntOrNull(rs, "1st Activity 5 days ago 2"));
          entity.setAlg1Activity6DaysAgo1(getIntOrNull(rs, "1st Activity 6 days ago 1"));
          entity.setAlg1Activity6DaysAgo2(getIntOrNull(rs, "1st Activity 6 days ago 2"));
          entity.setAlg1ActivityYesterday1(getIntOrNull(rs, "1st Activity yesterday 1"));
          entity.setAlg1ActivityYesterday2(getIntOrNull(rs, "1st Activity yesterday 2"));
          entity.setAlg2Activity2DaysAgo1(getIntOrNull(rs, "2nd Activity 2 days ago 1"));
          entity.setAlg2Activity2DaysAgo2(getIntOrNull(rs, "2nd Activity 2 days ago 2"));
          entity.setAlg2Activity3DaysAgo1(getIntOrNull(rs, "2nd Activity 3 days ago 1"));
          entity.setAlg2Activity3DaysAgo2(getIntOrNull(rs, "2nd Activity 3 days ago 2"));
          entity.setAlg2Activity4DaysAgo1(getIntOrNull(rs, "2nd Activity 4 days ago 1"));
          entity.setAlg2Activity4DaysAgo2(getIntOrNull(rs, "2nd Activity 4 days ago 2"));
          entity.setAlg2Activity5DaysAgo1(getIntOrNull(rs, "2nd Activity 5 days ago 1"));
          entity.setAlg2Activity5DaysAgo2(getIntOrNull(rs, "2nd Activity 5 days ago 2"));
          entity.setAlg2Activity6DaysAgo1(getIntOrNull(rs, "2nd Activity 6 days ago 1"));
          entity.setAlg2Activity6DaysAgo2(getIntOrNull(rs, "2nd Activity 6 days ago 2"));
          entity.setAlg2ActivityYesterday1(getIntOrNull(rs, "2nd Activity yesterday 1"));
          entity.setAlg2ActivityYesterday2(getIntOrNull(rs, "2nd Activity yesterday 2"));
          entity.setBatteryLife1(getIntOrNull(rs, "Battery life 1"));
          entity.setBatteryLife2(getIntOrNull(rs, "Battery life 2"));
          entity.setHairTrigger1(getIntOrNull(rs, "Hair trigger 1"));
          entity.setHairTrigger2(getIntOrNull(rs, "Hair trigger 2"));
          entity.setLongTrigger1(getIntOrNull(rs, "Long trigger 1"));
          entity.setLongTrigger2(getIntOrNull(rs, "Long trigger 2"));
          entity.setRecoveryid(rs.getString("Recoveryid"));
          return entity;
        }
    );

    return MigrationUtils.optionalResult(list);
  }

  protected WeightEntity readWeight(String parentID) {
    List<WeightEntity> list = jdbcTemplate.query(
        "SELECT * FROM [Weights] as t WHERE t.[recoveryID] = ? ", new Object[]{parentID},
        (rs, rowNum) -> {
          WeightEntity entity = new WeightEntity();

          entity.setMethod(rs.getString("Method"));
          entity.setN(getIntOrNull(rs, "N"));
          entity.setRecoveryID(rs.getString("RecoveryID"));
          entity.setSdDev(getFloatOrNull(rs, "SdDev"));
          entity.setWeight(getFloatOrNull(rs, "Weight"));
          return entity;
        }
    );

    return MigrationUtils.optionalResult(list);
  }

}



package com.fronde.server.migration;

import static com.fronde.server.migration.MigrationUtils.getBooleanOrNull;
import static com.fronde.server.migration.MigrationUtils.getFloatOrNull;
import static com.fronde.server.migration.MigrationUtils.getIntOrNull;

import com.fronde.server.domain.NestObservationEntity;
import com.fronde.server.services.common.ObjectIdFactory;
import com.fronde.server.services.nestobservation.NestObservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public abstract class NestObservationBaseMigrator implements Migrator {


  @Autowired
  protected MigrationUtils migrationUtils;

  @Autowired
  protected JdbcTemplate jdbcTemplate;

  @Autowired
  protected NestObservationRepository repository;

  @Autowired
  protected ProgressMonitor progressMonitor;

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  public void migrate() {
    progressMonitor.reset(NestObservationEntity.class);
    progressMonitor.println("started");

    SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM [nest observations]");
    while (sqlRowSet.next()) {
      NestObservationEntity entity = readEntity(sqlRowSet);
      migrationUtils.createRevision(entity);
      repository.save(entity);
      progressMonitor.tick();
    }

    progressMonitor.end();
    progressMonitor.println("save ok");
  }

  protected NestObservationEntity readEntity(SqlRowSet rs) {
    NestObservationEntity entity = new NestObservationEntity();

    entity.setActiveDayMinutes(getIntOrNull(rs, "Active day minutes"));
    entity.setActiveNightMinutes(getIntOrNull(rs, "Active night minutes"));
    // Skip migration for: birdID
    entity.setBreathingrate1(getIntOrNull(rs, "breathingrate1"));
    entity.setBreathingrate2(getIntOrNull(rs, "breathingrate2"));
    entity.setChick1id(rs.getString("chick1id"));
    entity.setChick1NumberFeeds(getIntOrNull(rs, "chick 1 number feeds"));
    entity.setChick1TimeFed(getIntOrNull(rs, "chick 1 time fed"));
    entity.setChick2id(rs.getString("chick2id"));
    entity.setChick2NumberFeeds(getIntOrNull(rs, "chick 2 number feeds"));
    entity.setChick2TimeFed(getIntOrNull(rs, "chick 2 time fed"));
    entity.setChickfirstfeed1(rs.getTimestamp("chickfirstfeed1"));
    entity.setChickfirstfeed2(rs.getTimestamp("chickfirstfeed2"));
    entity.setChickfirstfeed3(rs.getTimestamp("chickfirstfeed3"));
    entity.setChickfirstfeed4(rs.getTimestamp("chickfirstfeed4"));
    // Skip migration for: comments
    entity.setDate(rs.getTimestamp("date"));
    // Skip migration for: dateTime
    entity.setDocType("NestObservation");
    entity.setFinishObservationTime(rs.getTimestamp("Finish observation time"));
    entity.setFirstTimeOff(rs.getTimestamp("First time off"));
    entity.setHatchDate(rs.getTimestamp("hatch date"));
    entity.setHeatpad1(getBooleanOrNull(rs, "heatpad1"));
    entity.setHeatpad2(getBooleanOrNull(rs, "heatpad2"));
    entity.setHeatpad3(getBooleanOrNull(rs, "heatpad3"));
    entity.setHeatpad4(getBooleanOrNull(rs, "heatpad4"));
    entity.setHeatPadTime(getIntOrNull(rs, "Heat pad time"));
    entity.setId(objectIdFactory.create());
    entity.setLayDate(rs.getTimestamp("Lay date"));
    // Skip migration for: locationID
    entity.setMaxTimeOff(getIntOrNull(rs, "Max time off"));
    entity.setModifiedByPersonId(migrationUtils.getModifiedByPersonId());
    entity.setModifiedTime(migrationUtils.getModifiedTime());
    entity.setMumback1(rs.getTimestamp("mumback1"));
    entity.setMumback2(rs.getTimestamp("mumback2"));
    entity.setMumback3(rs.getTimestamp("mumback3"));
    entity.setMumback4(rs.getTimestamp("mumback4"));
    entity.setNestObsId(rs.getString("Nest obs id"));
    entity.setNotes(rs.getString("Notes"));
    entity.setNumberOfChicks(getIntOrNull(rs, "Number of chicks"));
    entity.setNumberOfEggs(getIntOrNull(rs, "Number of eggs"));
    entity.setNumberOfTimesOff(getIntOrNull(rs, "Number of times off"));
    entity.setOldBirdId(rs.getString("birdid"));
    entity.setOldLocationName(rs.getString("Location name"));
    entity.setPercentageOfDayActive(getFloatOrNull(rs, "Percentage of day active"));
    entity.setPercentageOfNightActive(getFloatOrNull(rs, "Percentage of night active"));
    // Skip migration for: revision
    entity.setRolls(getIntOrNull(rs, "Rolls"));
    entity.setScratches(getIntOrNull(rs, "Scratches"));
    entity.setStartObservationTime(rs.getTimestamp("Start observation time"));
    entity.setTimeOnNestDuringDaylight(getIntOrNull(rs, "Time on nest during daylight"));
    entity.setTimeOnNestDuringNight(getIntOrNull(rs, "Time on nest during night"));
    entity.setTotalTimeOff(getIntOrNull(rs, "Total time off"));
    entity.setUnknownChickNumberFeeds(getIntOrNull(rs, "unknown chick number feeds"));
    entity.setUnknownChickTimeFed(getIntOrNull(rs, "unknown chick time fed"));
    // Skip migration for: weighMethod
    // Skip migration for: chickRecordReferenceList
    // Skip migration for: eggRecordReferenceList
    // Skip migration for: motherTripList
    // Skip migration for: motherTripSummary
    // Skip migration for: nestChamber
    // Skip migration for: nestChickList
    // Skip migration for: nestEggList
    // Skip migration for: observationTimes
    // Skip migration for: observerList

    return entity;
  }

}



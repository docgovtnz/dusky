package com.fronde.server.migration;

import static com.fronde.server.migration.MigrationUtils.getBooleanOrNull;
import static com.fronde.server.migration.MigrationUtils.getFloatOrNull;
import static com.fronde.server.migration.MigrationUtils.getIntOrNull;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.BirdFeatureEntity;
import com.fronde.server.services.bird.BirdRepository;
import com.fronde.server.services.common.ObjectIdFactory;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public abstract class BirdBaseMigrator implements Migrator {


  @Autowired
  protected MigrationUtils migrationUtils;

  @Autowired
  protected JdbcTemplate jdbcTemplate;

  @Autowired
  protected BirdRepository repository;

  @Autowired
  protected ProgressMonitor progressMonitor;

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  public void migrate() {
    progressMonitor.reset(BirdEntity.class);
    progressMonitor.println("started");

    SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM [birds]");
    while (sqlRowSet.next()) {
      BirdEntity entity = readEntity(sqlRowSet);
      migrationUtils.createRevision(entity);
      repository.save(entity);
      progressMonitor.tick();
    }

    progressMonitor.end();
    progressMonitor.println("save ok");
  }

  protected BirdEntity readEntity(SqlRowSet rs) {
    BirdEntity entity = new BirdEntity();

    entity.setActualFreshWeight(getFloatOrNull(rs, "actual fresh weight"));
    entity.setAgeClassOverride(rs.getString("AgeClassOverride"));
    entity.setAlive(getBooleanOrNull(rs, "Alive"));
    entity.setBirdName(rs.getString("Bird Name"));
    // Skip migration for: clutch
    entity.setClutchOrder(getIntOrNull(rs, "clutch order"));
    entity.setComments(rs.getString("Comments"));
    // Skip migration for: currentBandId
    // Skip migration for: currentBandLeg
    // Skip migration for: currentChipId
    entity.setCurrentIsland(rs.getString("Current Island"));
    // Skip migration for: currentLocationID
    // Skip migration for: currentTxRecordId
    entity.setDateFirstFound(rs.getTimestamp("date first found"));
    entity.setDateFledged(rs.getTimestamp("DateFledged"));
    entity.setDateHatched(rs.getTimestamp("DateHatched"));
    entity.setDateIndependent(rs.getTimestamp("DateIndependent"));
    entity.setDateLaid(rs.getTimestamp("DateLaid"));
    entity.setDatesMated(rs.getString("dates mated$"));
    entity.setDateWeaned(rs.getTimestamp("DateWeaned"));
    entity.setDaysOnNest(rs.getString("days on nest$"));
    entity.setDeadEmbryo(rs.getString("dead embryo"));
    entity.setDefiniteFather(getBooleanOrNull(rs, "definite father"));
    entity.setDemise(rs.getTimestamp("Demise"));
    entity.setDiscoveryDate(rs.getTimestamp("Discovery date"));
    entity.setDocType("Bird");
    // Skip migration for: eggName
    entity.setEstAgeWhen1stFound(getFloatOrNull(rs, "est age when 1st found$"));
    entity.setFather(migrationUtils.convertBirdIDForBirds(rs.getString("Father")));
    entity.setFirstDayAtOrVeryCloseToNest(rs.getString("first day at or very close to nest$"));
    entity.setFledged(getBooleanOrNull(rs, "Fledged"));
    entity.getEggMeasurements().setFwCoefficientX104(getFloatOrNull(rs, "FW co efficient x10-4"));
    entity.setGan(rs.getString("GAN"));
    entity.setHouseID(rs.getString("HouseID"));
    entity.setId(objectIdFactory.create());
    entity.setIncubationPeriod(getFloatOrNull(rs, "Incubation Period"));
    entity.setInterClutchLayingInterval(getFloatOrNull(rs, "inter clutch laying interval"));
    entity.setLastDateMated(rs.getString("last date mated"));
    entity.setLastTxChangeId(rs.getString("LastTxChangeId"));
    // Skip migration for: layDateIsEstimate
    entity.setLayIsland(rs.getString("lay island"));
    entity.setLayLocationID(rs.getString("lay location"));
    entity.setLayYear(getIntOrNull(rs, "lay year"));
    entity.setLegColour(migrationUtils.convertLegColour(getIntOrNull(rs, "Leg colour")));
    entity.getEggMeasurements().setEggLength(getFloatOrNull(rs, "Length"));
    entity.setMatedWith(rs.getString("mated with"));
    entity.setMatingToLaying(getFloatOrNull(rs, "mating to laying"));
    entity.setMatingToNestingDays(getFloatOrNull(rs, "mating to nesting: days"));
    entity.setModifiedByPersonId(migrationUtils.getModifiedByPersonId());
    // Skip migration for: modifiedByRecordId
    entity.setModifiedTime(migrationUtils.getModifiedTime());
    entity.setMother(migrationUtils.convertBirdIDForBirds(rs.getString("Mother")));
    entity.setNestingToLaying(getFloatOrNull(rs, "nesting to laying"));
    entity.setNestMother(migrationUtils.convertBirdIDForBirds(rs.getString("Nest mother")));
    entity.setOldBirdID(rs.getString("BirdID"));
    entity
        .setPlumageColour(migrationUtils.convertPlumageColour(getIntOrNull(rs, "Plumage colour")));
    entity.setResults(rs.getString("Results"));
    // Skip migration for: revision
    entity.setSex(migrationUtils.convertSex(rs.getString("Sex")));
    entity.setStudbookno(getIntOrNull(rs, "Studbookno"));
    entity.setTransmitterGroup(rs.getString("Transmitter group"));
    entity.setViable(rs.getString("viable"));
    entity.setHistoricDataFirstEggWeight(getFloatOrNull(rs, "Weight"));
    entity.getEggMeasurements().setEggWidth(getFloatOrNull(rs, "Width"));
    entity.setBirdFeatureList(readBirdFeatureList(rs.getString("birdID")));
    // Skip migration for: eggMeasurements
    // Skip migration for: embryoMeasurements

    return entity;
  }

  protected List<BirdFeatureEntity> readBirdFeatureList(String parentID) {
    return jdbcTemplate.query(
        "SELECT * FROM [bird features] as t WHERE t.[birdid] = ? ", new Object[]{parentID},
        (rs, rowNum) -> {
          BirdFeatureEntity entity = new BirdFeatureEntity();

          entity.setBirdid(rs.getString("birdid"));
          entity.setBodyPart(rs.getString("body part"));
          entity.setDescription(rs.getString("Description"));
          entity.setFeatureid(rs.getString("featureid"));
          return entity;
        }
    );
  }

}



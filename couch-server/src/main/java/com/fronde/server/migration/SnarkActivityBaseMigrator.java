package com.fronde.server.migration;

import static com.fronde.server.migration.MigrationUtils.getBooleanOrNull;
import static com.fronde.server.migration.MigrationUtils.getIntOrNull;

import com.fronde.server.domain.SnarkActivityEntity;
import com.fronde.server.domain.SnarkRecordEntity;
import com.fronde.server.services.common.ObjectIdFactory;
import com.fronde.server.services.snarkactivity.SnarkActivityRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public abstract class SnarkActivityBaseMigrator implements Migrator {


  @Autowired
  protected MigrationUtils migrationUtils;

  @Autowired
  protected JdbcTemplate jdbcTemplate;

  @Autowired
  protected SnarkActivityRepository repository;

  @Autowired
  protected ProgressMonitor progressMonitor;

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  public void migrate() {
    progressMonitor.reset(SnarkActivityEntity.class);
    progressMonitor.println("started");

    SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM [Track and bowl activity]");
    while (sqlRowSet.next()) {
      SnarkActivityEntity entity = readEntity(sqlRowSet);
      migrationUtils.createRevision(entity);
      repository.save(entity);
      progressMonitor.tick();
    }

    progressMonitor.end();
    progressMonitor.println("save ok");
  }

  protected SnarkActivityEntity readEntity(SqlRowSet rs) {
    SnarkActivityEntity entity = new SnarkActivityEntity();

    // Skip migration for: activityType
    entity.setComments(rs.getString("comments"));
    entity.setDate(rs.getTimestamp("Date"));
    entity.setDocType("SnarkActivity");
    entity.setId(objectIdFactory.create());
    entity.setLocationID(migrationUtils.convertLocationName(rs.getString("TandB")));
    entity.setModifiedByPersonId(migrationUtils.getModifiedByPersonId());
    entity.setModifiedTime(migrationUtils.getModifiedTime());
    entity.setObserverPersonID(rs.getString("Observer"));
    entity.setOldBooming(getBooleanOrNull(rs, "Booming"));
    entity.setOldChinging(getBooleanOrNull(rs, "Chinging"));
    entity.setOldFightingSign(getIntOrNull(rs, "Fighting sign"));
    entity.setOldGrubbing(getIntOrNull(rs, "Grubbing"));
    entity.setOldMatingSign(getIntOrNull(rs, "Mating sign"));
    entity.setOldObserver(rs.getString("Observer"));
    entity.setOldSkraaking(getBooleanOrNull(rs, "Skraaking"));
    entity.setOldSticks(getIntOrNull(rs, "Sticks"));
    entity.setOldTAndB(rs.getString("TandB"));
    entity.setOldTAndBRecId(rs.getString("tandbrecid"));
    entity.setOldTapeUsed(getBooleanOrNull(rs, "tape used"));
    entity.setOldTbHopper(getIntOrNull(rs, "TB/hopper"));
    entity.setOldTimeRecorded(getIntOrNull(rs, "time recorded"));
    entity.setOldTrackActivity(getIntOrNull(rs, "Track activity"));
    // Skip migration for: revision
    entity.setTandBrecid(rs.getString("TandBrecid"));
    entity.setSnarkRecordList(readSnarkRecordList(rs.getString("tandbrecid")));
    // Skip migration for: trackAndBowlActivity

    return entity;
  }

  protected List<SnarkRecordEntity> readSnarkRecordList(String parentID) {
    return jdbcTemplate.query(
        "SELECT * FROM [snark records] as t WHERE t.[tandbrecid] = ? ", new Object[]{parentID},
        (rs, rowNum) -> {
          SnarkRecordEntity entity = new SnarkRecordEntity();

          entity.setArriveDateTime(rs.getTimestamp("ArriveDateTime"));
          entity.setBirdCert(rs.getString("bird cert"));
          // Skip migration for: birdID
          // Skip migration for: channel
          entity.setDepartDateTime(rs.getTimestamp("DepartDateTime"));
          entity.setMating(getBooleanOrNull(rs, "Mating"));
          entity.setOldBirdId(rs.getString("birdid"));
          entity.setOldSnarkRecId(rs.getString("Snarkrecid"));
          // Skip migration for: recordID
          entity.setTandbrecid(rs.getString("tandbrecid"));
          // Skip migration for: weight
          return entity;
        }
    );
  }

}



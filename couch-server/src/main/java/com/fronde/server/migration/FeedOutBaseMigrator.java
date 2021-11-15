package com.fronde.server.migration;

import static com.fronde.server.migration.MigrationUtils.getIntOrNull;

import com.fronde.server.domain.FeedOutEntity;
import com.fronde.server.services.common.ObjectIdFactory;
import com.fronde.server.services.feedout.FeedOutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public abstract class FeedOutBaseMigrator implements Migrator {


  @Autowired
  protected MigrationUtils migrationUtils;

  @Autowired
  protected JdbcTemplate jdbcTemplate;

  @Autowired
  protected FeedOutRepository repository;

  @Autowired
  protected ProgressMonitor progressMonitor;

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  public void migrate() {
    progressMonitor.reset(FeedOutEntity.class);
    progressMonitor.println("started");

    SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM [new feed]");
    while (sqlRowSet.next()) {
      FeedOutEntity entity = readEntity(sqlRowSet);
      migrationUtils.createRevision(entity);
      repository.save(entity);
      progressMonitor.tick();
    }

    progressMonitor.end();
    progressMonitor.println("save ok");
  }

  protected FeedOutEntity readEntity(SqlRowSet rs) {
    FeedOutEntity entity = new FeedOutEntity();

    entity.setComments(rs.getString("Comments"));
    entity.setDateIn(rs.getTimestamp("Date in"));
    entity.setDateOut(rs.getTimestamp("Date out"));
    entity.setDocType("FeedOut");
    entity.setFeedId(rs.getString("Feed id"));
    entity.setId(objectIdFactory.create());
    entity.setLocationID(migrationUtils.convertLocationName(rs.getString("Location")));
    entity.setModifiedByPersonId(migrationUtils.getModifiedByPersonId());
    entity.setModifiedTime(migrationUtils.getModifiedTime());
    entity.setNewRecord(rs.getString("New record"));
    entity.setOldLocation(rs.getString("Location"));
    entity.setOtherFood(rs.getString("Other food"));
    entity.setOtherIn(getIntOrNull(rs, "Other in"));
    entity.setOtherOut(getIntOrNull(rs, "Other out"));
    // Skip migration for: revision
    // Skip migration for: foodTallyList
    // Skip migration for: targetBirdList

    return entity;
  }

}



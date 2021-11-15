package com.fronde.server.migration;

import static com.fronde.server.migration.MigrationUtils.getFloatOrNull;

import com.fronde.server.domain.IslandEntity;
import com.fronde.server.services.common.ObjectIdFactory;
import com.fronde.server.services.island.IslandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public abstract class IslandBaseMigrator implements Migrator {


  @Autowired
  protected MigrationUtils migrationUtils;

  @Autowired
  protected JdbcTemplate jdbcTemplate;

  @Autowired
  protected IslandRepository repository;

  @Autowired
  protected ProgressMonitor progressMonitor;

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  public void migrate() {
    progressMonitor.reset(IslandEntity.class);
    progressMonitor.println("started");

    SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM [island]");
    while (sqlRowSet.next()) {
      IslandEntity entity = readEntity(sqlRowSet);
      migrationUtils.createRevision(entity);
      repository.save(entity);
      progressMonitor.tick();
    }

    progressMonitor.end();
    progressMonitor.println("save ok");
  }

  protected IslandEntity readEntity(SqlRowSet rs) {
    IslandEntity entity = new IslandEntity();

    entity.setDocType("Island");
    entity.setId(objectIdFactory.create());
    entity.setLowerEasting(getFloatOrNull(rs, "lower easting"));
    entity.setLowerNorthing(getFloatOrNull(rs, "lower northing"));
    entity.setModifiedByPersonId(migrationUtils.getModifiedByPersonId());
    entity.setModifiedTime(migrationUtils.getModifiedTime());
    entity.setName(rs.getString("Name"));
    // Skip migration for: revision
    entity.setUpperEasting(getFloatOrNull(rs, "upper easting"));
    entity.setUpperNorthing(getFloatOrNull(rs, "upper northing"));

    return entity;
  }

}



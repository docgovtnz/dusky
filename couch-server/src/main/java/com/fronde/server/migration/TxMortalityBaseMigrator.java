package com.fronde.server.migration;

import static com.fronde.server.migration.MigrationUtils.getIntOrNull;

import com.fronde.server.domain.TxMortalityEntity;
import com.fronde.server.services.common.ObjectIdFactory;
import com.fronde.server.services.txmortality.TxMortalityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public abstract class TxMortalityBaseMigrator implements Migrator {


  @Autowired
  protected MigrationUtils migrationUtils;

  @Autowired
  protected JdbcTemplate jdbcTemplate;

  @Autowired
  protected TxMortalityRepository repository;

  @Autowired
  protected ProgressMonitor progressMonitor;

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  public void migrate() {
    progressMonitor.reset(TxMortalityEntity.class);
    progressMonitor.println("started");

    SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM [TxMortality]");
    while (sqlRowSet.next()) {
      TxMortalityEntity entity = readEntity(sqlRowSet);
      migrationUtils.createRevision(entity);
      repository.save(entity);
      progressMonitor.tick();
    }

    progressMonitor.end();
    progressMonitor.println("save ok");
  }

  protected TxMortalityEntity readEntity(SqlRowSet rs) {
    TxMortalityEntity entity = new TxMortalityEntity();

    entity.setActivityBpm(getIntOrNull(rs, "ActivityBpm"));
    entity.setDocType("TxMortality");
    entity.setHoursTilMort(getIntOrNull(rs, "HoursTilMort"));
    entity.setId(objectIdFactory.create());
    entity.setModifiedByPersonId(migrationUtils.getModifiedByPersonId());
    entity.setModifiedTime(migrationUtils.getModifiedTime());
    entity.setMortalityBpm(getIntOrNull(rs, "MortalityBpm"));
    entity.setName(rs.getString("Name"));
    entity.setNormalBpm(getIntOrNull(rs, "NormalBpm"));
    entity.setOldTxMortId(getIntOrNull(rs, "Id"));
    // Skip migration for: revision

    return entity;
  }

}



package com.fronde.server.migration;

import static com.fronde.server.migration.MigrationUtils.getFloatOrNull;
import static com.fronde.server.migration.MigrationUtils.getIntOrNull;

import com.fronde.server.domain.TransmitterEntity;
import com.fronde.server.services.common.ObjectIdFactory;
import com.fronde.server.services.transmitter.TransmitterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public abstract class TransmitterBaseMigrator implements Migrator {


  @Autowired
  protected MigrationUtils migrationUtils;

  @Autowired
  protected JdbcTemplate jdbcTemplate;

  @Autowired
  protected TransmitterRepository repository;

  @Autowired
  protected ProgressMonitor progressMonitor;

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  public void migrate() {
    progressMonitor.reset(TransmitterEntity.class);
    progressMonitor.println("started");

    SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM [Transmitter]");
    while (sqlRowSet.next()) {
      TransmitterEntity entity = readEntity(sqlRowSet);
      migrationUtils.createRevision(entity);
      repository.save(entity);
      progressMonitor.tick();
    }

    progressMonitor.end();
    progressMonitor.println("save ok");
  }

  protected TransmitterEntity readEntity(SqlRowSet rs) {
    TransmitterEntity entity = new TransmitterEntity();

    entity.setChannel(getIntOrNull(rs, "Channel"));
    entity.setDocType("Transmitter");
    entity.setFrequency(getFloatOrNull(rs, "Frequency"));
    entity.setId(objectIdFactory.create());
    entity.setIsland(rs.getString("Island"));
    entity.setLastRecordId(rs.getString("LastTxChangeId"));
    // Skip migration for: lifeExpectancy
    entity.setModifiedByPersonId(migrationUtils.getModifiedByPersonId());
    entity.setModifiedTime(migrationUtils.getModifiedTime());
    entity.setOldTxMortId(getIntOrNull(rs, "TxMortalityId"));
    entity.setOrigLifeExpectancyWeeks(getIntOrNull(rs, "OrigLifeExpectancyWeeks"));
    // Skip migration for: revision
    entity.setRigging(rs.getString("Rigging"));
    entity.setSoftware(rs.getString("Software"));
    entity.setStatus(rs.getString("Status"));
    entity.setTxFineTune(rs.getString("TxFineTune"));
    entity.setTxId(rs.getString("TxId"));
    // Skip migration for: txMortalityId

    return entity;
  }

}



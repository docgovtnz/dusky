package com.fronde.server.migration;

import com.fronde.server.domain.TransmitterEntity;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
@Order(40)
public class TransmitterMigrator extends TransmitterBaseMigrator implements Migrator {

  @Override
  public void migrate() {
    super.migrate();
  }

  @Override
  protected TransmitterEntity readEntity(SqlRowSet rs) {
    TransmitterEntity entity = super.readEntity(rs);

    // override the random ID and use the one from the txIdMap
    entity.setId(migrationUtils.convertTxIdForTransmitters(entity.getTxId()));

    readTxMortality(entity);
    readTxLifeExpectancy(entity);
    readLastRecordId(entity);

    return entity;
  }

  public void readTxMortality(TransmitterEntity entity) {
    String txMortality = migrationUtils.convertTxMortId(entity.getOldTxMortId());
    entity.setTxMortalityId(txMortality);
  }

  public void readTxLifeExpectancy(TransmitterEntity entity) {
    String txChangeId = entity.getLastRecordId();
    String sql = "Select * from [TxChange] as tc where tc.[txchangeid] = ? ";
    SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, txChangeId);

    while (sqlRowSet.next()) {
      Integer lifeExpectancy = sqlRowSet.getInt("txLifeExpectancyWeeks");
      if (lifeExpectancy != null) {
        entity.setLifeExpectancy(lifeExpectancy);
      }
    }
  }

  public void readLastRecordId(TransmitterEntity entity) {
    String txChangeId = entity.getLastRecordId();
    String sql = "Select * from [TxChange] as tc where tc.[txchangeid] = ? ";
    SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, txChangeId);

    while (sqlRowSet.next()) {
      String recordID = sqlRowSet.getString("RecoveryID");
      if (recordID != null) {
        entity.setLastRecordId(migrationUtils.convertRecordIDForRecords(recordID));
      }
    }
  }
}



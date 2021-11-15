package com.fronde.server.migration;

import com.fronde.server.domain.TxMortalityEntity;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
@Order(39)
public class TxMortalityMigrator extends TxMortalityBaseMigrator implements Migrator {

  @Override
  public void migrate() {
    super.migrate();
  }

  @Override
  protected TxMortalityEntity readEntity(SqlRowSet rs) {
    TxMortalityEntity entity = super.readEntity(rs);

    entity.setId(migrationUtils.convertTxMortIdForTxMortalities(entity.getOldTxMortId()));

    return entity;
  }

}



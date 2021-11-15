package com.fronde.server.migration;

import com.fronde.server.domain.PersonEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
@Order(10)
public class PersonMigrator extends PersonBaseMigrator implements Migrator {

  @Autowired
  private MigrationUtils migrationUtils;

  @Override
  public void migrate() {
    super.migrate();
  }

  @Override
  protected PersonEntity readEntity(SqlRowSet rs) {
    PersonEntity personEntity = super.readEntity(rs);

    personEntity.setId(migrationUtils.convertPersonNameForPersons(personEntity.getName()));

    return personEntity;
  }

}

package com.fronde.server.migration;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(999)
public class IslandMigrator extends IslandBaseMigrator implements Migrator {

  @Override
  public void migrate() {
    super.migrate();
  }

}



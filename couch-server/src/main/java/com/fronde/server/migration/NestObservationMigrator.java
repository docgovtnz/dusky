package com.fronde.server.migration;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(999)
public class NestObservationMigrator extends NestObservationBaseMigrator implements Migrator {

  @Override
  public void migrate() {
    super.migrate();
  }

}



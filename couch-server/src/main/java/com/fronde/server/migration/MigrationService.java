package com.fronde.server.migration;

import com.fronde.server.CouchServerApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;


@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan("com.fronde.server")
@Component
public class MigrationService {

  @Autowired
  protected Migrator[] migrators;

  public void migrate() {
    long startTime = System.currentTimeMillis();
    for (Migrator migrator : migrators) {
      migrator.migrate();
    }
    float durationInSecs = (float) (System.currentTimeMillis() - startTime) / 1000;
    System.out.println("********** Migration Complete ************");
    System.out.println("Duration: " + durationInSecs + " (s)");
    System.out.println("******************************************");
  }

  public static void main(String[] args) {
    CouchServerApplication.popuplatePasswords();
    ApplicationContext applicationContext = SpringApplication
        .run(CouchServerApplication.class, args);
    MigrationService migrationService = applicationContext.getBean(MigrationService.class);
    migrationService.migrate();
    System.exit(0);
  }
}

package com.fronde.server.migration;

import com.fronde.server.CouchServerApplication;
import com.fronde.server.config.SettingStartup;
import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.LifeStageEntity;
import com.fronde.server.services.bird.BirdRepository;
import com.fronde.server.services.lifestage.LifeStageService;
import com.fronde.server.services.options.BirdSummaryDTO;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(999)
public class LifeStageMigrator implements Migrator {


  @Autowired
  protected BirdRepository birdRepository;

  @Autowired
  protected LifeStageService lifeStageService;


  @Override
  public void migrate() {

    try {
      PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("LifeStage.csv")));
      printHeaders(writer);

      List<BirdSummaryDTO> birdSummaries = birdRepository.findBirdSummaries(null);
      birdSummaries.forEach(birdSummaryDTO -> {
        Optional<BirdEntity> birdEntity = birdRepository.findById(birdSummaryDTO.getId());
        migrate(writer, birdEntity.get());
      });

      writer.flush();
      writer.close();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private void printHeaders(PrintWriter writer) {
    String[] headers = {
        "Name",
        "AgeClass",
        "Age in days",
        "Lay Date",
        "Est Age when found",
        "Date Laid",
        "Date Hatched",
        "Date Discovered",
        "Viable",
        "Alive",
        "Date Demise"
    };

    for (int i = 0; i < headers.length; i++) {
      String nextHeader = headers[i];
      writer.print(nextHeader);
      if (i < headers.length - 1) {
        writer.print(",");
      }
    }
    writer.println();
  }

  private void migrate(PrintWriter writer, BirdEntity bird) {
    String name = bird.getBirdName();
    String ageClass = getAgeClass(bird);
    Duration age = getAge(bird);
    String ageInDays = age == null ? null : String.valueOf(age.toDays());

    writer.println(name + "," +
        ageClass + "," +
        ageInDays + "," +
        getLayDate(bird) + "," +
        bird.getEstAgeWhen1stFound() + "," +
        toString(bird.getDateLaid()) + "," +
        toString(bird.getDateHatched()) + "," +
        toString(bird.getDiscoveryDate()) + "," +
        bird.getViable() + "," +
        bird.getAlive() + "," +
        toString(bird.getDemise()));

    LifeStageEntity entity = new LifeStageEntity();
    entity.setBirdID(bird.getId());
    entity.setAgeClass(ageClass);
    entity.setChangeType("Data Migration");
    //entity.setDateTime(todo);

    lifeStageService.saveWithThrow(entity);
  }

  private String toString(Date date) {
    return date == null ? null : DateTimeFormatter.ISO_INSTANT.format(date.toInstant());
  }

  private String getAgeClass(BirdEntity bird) {

    Duration age = getAge(bird);

    if (bird.getDiscoveryDate() != null) {
      return "Adult";
    } else if (bird.getDateHatched() != null) {
      if (age == null) {
        return "Unknown";
      } else if (age.toDays() >= (SettingStartup.adultThresholdYears * 365)) {
        return "Adult";
      } else if (age.toDays() >= SettingStartup.juvenileThresholdDays) {
        return "Juvenile";
      } else {
        return "Chick";
      }
    } else {
      if (bird.getAlive() && (age == null || age.toDays() >= 35)) {
        return "Unknown";
      } else {
        return "Egg";
      }
    }
  }

  private Duration getAge(BirdEntity bird) {

    Instant birthDate =
        bird.getDateHatched() == null ? getLayDate(bird) : bird.getDateHatched().toInstant();

    if (birthDate != null) {
      if (bird.getAlive()) {
        return Duration.between(birthDate, ZonedDateTime.now());
      } else {
        if (bird.getViable() != null && bird.getViable().equals("infert")) {
          return Duration.ZERO;
        } else if (bird.getDemise() != null) {
          return Duration.between(birthDate, bird.getDemise().toInstant());
        } else {
          return null;
        }
      }
    } else {
      return null;
    }
  }

  private Instant getLayDate(BirdEntity bird) {
    if (bird.getDateLaid() != null) {
      return bird.getDateLaid().toInstant();
    } else if (bird.getDateFirstFound() != null && bird.getEstAgeWhen1stFound() != null) {
      Float estimatedAgeWhenFound = Float.valueOf(bird.getEstAgeWhen1stFound());
      long inDays = Math.round(estimatedAgeWhenFound);
      return bird.getDateFirstFound().toInstant().minus(inDays, ChronoUnit.DAYS);
    } else {
      return null;
    }
  }


  public static void main(String[] args) {
    ApplicationContext applicationContext = SpringApplication
        .run(CouchServerApplication.class, args);
    LifeStageMigrator lifeStageMigrator = applicationContext.getBean(LifeStageMigrator.class);
    lifeStageMigrator.migrate();
    System.exit(0);
  }
}

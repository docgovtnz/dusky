package com.fronde.server.services.bird;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class BirdAgeUtils {

  public static Integer calculateAgeInDays(Date layDate, Date hatchDate, Date firstFoundDate,
      Float estimatedAgeWhenFirstFoundInDays, Boolean alive, String viable, Date demiseDate) {
    Duration d = calculateAge(layDate, hatchDate, firstFoundDate, estimatedAgeWhenFirstFoundInDays,
        alive, viable, demiseDate);
    if (d != null) {
      return Integer.valueOf((int) d.toDays());
    } else {
      return null;
    }
  }

  private static Duration calculateAge(Date layDate, Date hatchDate, Date firstFoundDate,
      Float estimatedAgeWhenFirstFoundInDays, Boolean alive, String viable, Date demiseDate) {
    if (alive == null || !alive) {
      if ("infert".equals(viable)) {
        return Duration.ZERO;
      } else {
        Date birthDate = getBirthDate(layDate, hatchDate, firstFoundDate,
            estimatedAgeWhenFirstFoundInDays);
        if (demiseDate != null && birthDate != null) {
          return Duration.between(birthDate.toInstant(), demiseDate.toInstant());
        } else {
          return null;
        }
      }
    } else {
      Date birthDate = getBirthDate(layDate, hatchDate, firstFoundDate,
          estimatedAgeWhenFirstFoundInDays);
      if (birthDate != null) {
        // get the start of day (which needs to be in NZST/NZDT)
        // apparently this way takes into account DST
        Date startOfToday = Date.from(
            ZonedDateTime.now(ZoneId.of("Pacific/Auckland")).truncatedTo(ChronoUnit.DAYS)
                .toInstant());
        return Duration.between(birthDate.toInstant(), startOfToday.toInstant());
      } else {
        return null;
      }
    }
  }

  private static Date getBirthDate(Date layDate, Date hatchDate, Date firstFoundDate,
      Float estimatedAgeWhenFirstFoundInDays) {
    if (hatchDate != null) {
      return hatchDate;
    } else if (layDate != null) {
      return layDate;
    } else if (firstFoundDate != null && estimatedAgeWhenFirstFoundInDays != null) {
      // we assume it is OK to round the value to the nearest day
      return Date.from(firstFoundDate.toInstant()
          .minus(estimatedAgeWhenFirstFoundInDays.intValue(), ChronoUnit.DAYS));
    } else {
      return null;
    }
  }

}

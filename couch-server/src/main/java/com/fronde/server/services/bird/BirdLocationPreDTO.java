package com.fronde.server.services.bird;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class BirdLocationPreDTO {

  private BirdLocationDTO dto;

  private Boolean alive;
  private Date dateFirstFound;
  private Date dateHatched;
  private Date dateLaid;
  private Date demise;
  private Date discoveryDate;
  private Float estAgeWhen1stFound;
  private String viable;

  public BirdLocationPreDTO() {
  }

  public BirdLocationDTO getDto() {
    return dto;
  }

  public void setDto(BirdLocationDTO dto) {
    this.dto = dto;
  }

  public Boolean getAlive() {
    return alive;
  }

  public void setAlive(Boolean alive) {
    this.alive = alive;
  }

  public Date getDateFirstFound() {
    return dateFirstFound;
  }

  public void setDateFirstFound(Date dateFirstFound) {
    this.dateFirstFound = dateFirstFound;
  }

  public Date getDateHatched() {
    return dateHatched;
  }

  public void setDateHatched(Date dateHatched) {
    this.dateHatched = dateHatched;
  }

  public Date getDateLaid() {
    return dateLaid;
  }

  public void setDateLaid(Date dateLaid) {
    this.dateLaid = dateLaid;
  }

  public Date getDemise() {
    return demise;
  }

  public void setDemise(Date demise) {
    this.demise = demise;
  }

  public Date getDiscoveryDate() {
    return discoveryDate;
  }

  public void setDiscoveryDate(Date discoveryDate) {
    this.discoveryDate = discoveryDate;
  }

  public Float getEstAgeWhen1stFound() {
    return estAgeWhen1stFound;
  }

  public void setEstAgeWhen1stFound(Float estAgeWhen1stFound) {
    this.estAgeWhen1stFound = estAgeWhen1stFound;
  }

  public String getViable() {
    return viable;
  }

  public void setViable(String viable) {
    this.viable = viable;
  }

  public String calcAgeClass(Date asAt) {
    if (discoveryDate != null) {
      return "Adult";
    }
    if (dateHatched != null) {
      Float ageInYears = calcAgeInYears(asAt);
      if (ageInYears == null) {
        return null;
      } else if (ageInYears >= 4.5) {
        return "Adult";
      } else {
        Integer ageInDays = calcAgeInDays(asAt);
        if (ageInDays >= 150) {
          return "Juvenile";
        } else {
          return "Chick";
        }
      }
    } else {
      Integer ageInDays = 0;
      Date layDate = calcLayDate();
      if (layDate != null) {
        LocalDate layDateAsLocal = toLocalDate(layDate);
        LocalDate asAtAsLocal = toLocalDate(asAt);
        ageInDays = Period.between(layDateAsLocal, asAtAsLocal).getDays();
      }
      if (alive != null && alive && ageInDays >= 35) {
        return "Unknown";
      } else {
        return "Egg";
      }
    }
  }

  private Date calcLayDate() {
    if (dateLaid != null) {
      return dateLaid;
    }
    if (dateFirstFound != null && estAgeWhen1stFound != null) {
      LocalDate dateFirstFoundAsLocal = toLocalDate(dateFirstFound);
      Integer estAgeWhen1stFoundInDays = Math.round(365 * estAgeWhen1stFound);
      return Date.from(dateFirstFoundAsLocal.minusDays(estAgeWhen1stFoundInDays)
          .atStartOfDay(ZoneId.of("Pacific/Auckland")).toInstant());
    } else {
      return null;
    }
  }

  private Float calcAgeInYears(Date asAt) {
    Integer ageInDays = calcAgeInDays(asAt);
    if (ageInDays != null) {
      return (float) ageInDays / 365;
    } else {
      return null;
    }
  }

  private Integer calcAgeInDays(Date asAt) {
    Date birthDate;
    if (dateHatched != null) {
      birthDate = dateHatched;
    } else {
      birthDate = calcLayDate();
    }
    if (birthDate == null) {
      return null;
    }
    if (alive != null && alive) {
      LocalDate birthDateAsLocal = toLocalDate(birthDate);
      LocalDate asAtAsLocal = toLocalDate(asAt);
      return (int) ChronoUnit.DAYS.between(birthDateAsLocal, asAtAsLocal);
    } else if ("infert".equals(viable)) {
      return 0;
    } else if (demise != null) {
      LocalDate demiseAsLocal = toLocalDate(demise);
      LocalDate birthDateAsLocal = toLocalDate(birthDate);
      return (int) ChronoUnit.DAYS.between(birthDateAsLocal, demiseAsLocal);
    } else {
      return null;
    }
  }

  private static LocalDate toLocalDate(Date date) {
    return date.toInstant().atZone(ZoneId.of("Pacific/Auckland")).toLocalDate();
  }

}

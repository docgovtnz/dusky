package com.fronde.server.services.bird;

import com.fronde.server.domain.criteria.AbstractCriteria;
import java.util.List;

public class BirdCriteria extends AbstractCriteria {

  private List<String> birdNames;
  private String sex;
  private Boolean showAlive;
  private Boolean showDead;
  private String ageClass;
  private String currentIsland;
  private String currentLocationID;
  private String transmitterGroup;
  private Boolean excludeEgg;

  public List<String> getBirdNames() {
    return birdNames;
  }

  public void setBirdNames(List<String> birdNames) {
    this.birdNames = birdNames;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public Boolean getShowAlive() {
    return showAlive;
  }

  public void setShowAlive(Boolean showAlive) {
    this.showAlive = showAlive;
  }

  public Boolean getShowDead() {
    return showDead;
  }

  public void setShowDead(Boolean showDead) {
    this.showDead = showDead;
  }

  public String getAgeClass() {
    return ageClass;
  }

  public void setAgeClass(String ageClass) {
    this.ageClass = ageClass;
  }

  public String getCurrentIsland() {
    return currentIsland;
  }

  public void setCurrentIsland(String currentIsland) {
    this.currentIsland = currentIsland;
  }

  public String getCurrentLocationID() {
    return currentLocationID;
  }

  public void setCurrentLocationID(String currentLocationID) {
    this.currentLocationID = currentLocationID;
  }

  public String getTransmitterGroup() {
    return transmitterGroup;
  }

  public void setTransmitterGroup(String transmitterGroup) {
    this.transmitterGroup = transmitterGroup;
  }

  public Boolean getExcludeEgg() {
    return excludeEgg;
  }

  public void setExcludeEgg(Boolean excludeEgg) {
    this.excludeEgg = excludeEgg;
  }
}

package com.fronde.server.services.bird;

public class BirdSearchDTO {

  private String birdID;
  private String birdName;
  private String houseID;
  private String currentIsland;
  private String sex;
  private String ageClass;
  private Integer ageInDays;
  private Boolean alive;
  private String transmitterGroup;
  private String currentLocation;

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public String getBirdName() {
    return birdName;
  }

  public void setBirdName(String birdName) {
    this.birdName = birdName;
  }

  public String getHouseID() {
    return houseID;
  }

  public void setHouseID(String houseID) {
    this.houseID = houseID;
  }

  public String getCurrentIsland() {
    return currentIsland;
  }

  public void setCurrentIsland(String currentIsland) {
    this.currentIsland = currentIsland;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public String getAgeClass() {
    return ageClass;
  }

  public void setAgeClass(String ageClass) {
    this.ageClass = ageClass;
  }

  public Boolean getAlive() {
    return alive;
  }

  public void setAlive(Boolean alive) {
    this.alive = alive;
  }

  public Integer getAgeInDays() {
    return ageInDays;
  }

  public void setAgeInDays(Integer ageInDays) {
    this.ageInDays = ageInDays;
  }

  public String getTransmitterGroup() {
    return transmitterGroup;
  }

  public void setTransmitterGroup(String transmitterGroup) {
    this.transmitterGroup = transmitterGroup;
  }

  public String getCurrentLocation() {
    return currentLocation;
  }

  public void setCurrentLocation(String currentLocation) {
    this.currentLocation = currentLocation;
  }

}

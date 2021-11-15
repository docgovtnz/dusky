package com.fronde.server.services.location;

public class LocationSearchDTO {

  private Boolean active;
  private String birdID;
  private String birdName;
  private Float easting;
  private String island;
  private String locationName;
  private String locationType;
  private Float northing;
  private String locationID;

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

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

  public Float getEasting() {
    return easting;
  }

  public void setEasting(Float easting) {
    this.easting = easting;
  }

  public String getIsland() {
    return island;
  }

  public void setIsland(String island) {
    this.island = island;
  }

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public String getLocationType() {
    return locationType;
  }

  public void setLocationType(String locationType) {
    this.locationType = locationType;
  }

  public Float getNorthing() {
    return northing;
  }

  public void setNorthing(Float northing) {
    this.northing = northing;
  }

  public String getLocationID() {
    return locationID;
  }

  public void setLocationID(String locationID) {
    this.locationID = locationID;
  }
}

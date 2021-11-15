package com.fronde.server.services.location;

import com.fronde.server.domain.criteria.AbstractCriteria;
import java.util.List;

public class LocationCriteria extends AbstractCriteria {

  private List<String> locationIDs;
  private String island;
  private String birdID;
  private Boolean activeOnly;
  private String locationType;

  public List<String> getLocationIDs() {
    return locationIDs;
  }

  public void setLocationIDs(List<String> locationIDs) {
    this.locationIDs = locationIDs;
  }

  public String getIsland() {
    return island;
  }

  public void setIsland(String island) {
    this.island = island;
  }

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public Boolean getActiveOnly() {
    return activeOnly;
  }

  public void setActiveOnly(Boolean activeOnly) {
    this.activeOnly = activeOnly;
  }

  public String getLocationType() {
    return locationType;
  }

  public void setLocationType(String locationType) {
    this.locationType = locationType;
  }

}

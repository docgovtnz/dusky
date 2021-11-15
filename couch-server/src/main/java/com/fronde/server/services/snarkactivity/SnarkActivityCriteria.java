package com.fronde.server.services.snarkactivity;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fronde.server.domain.criteria.AbstractCriteria;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public class SnarkActivityCriteria extends AbstractCriteria {

  private String island;
  private String locationID;
  private String birdID;
  // assume dates in all criteria objects are dates and not date times
  @DateTimeFormat(iso = ISO.DATE)
  private Date fromDate;
  // assume dates in all criteria objects are dates and not date times
  @DateTimeFormat(iso = ISO.DATE)
  private Date toDate;
  private Boolean includeTrackAndBowl;
  private Boolean includeHopper;
  private Boolean includeNest;
  private Boolean includeRoost;

  public String getIsland() {
    return island;
  }

  public void setIsland(String island) {
    this.island = island;
  }

  public String getLocationID() {
    return locationID;
  }

  public void setLocationID(String locationID) {
    this.locationID = locationID;
  }

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public Date getFromDate() {
    return fromDate;
  }

  public void setFromDate(Date fromDate) {
    this.fromDate = fromDate;
  }

  public Date getToDate() {
    return toDate;
  }

  public void setToDate(Date toDate) {
    this.toDate = toDate;
  }

  public Boolean getIncludeTrackAndBowl() {
    return includeTrackAndBowl;
  }

  public void setIncludeTrackAndBowl(Boolean includeTrackAndBowl) {
    this.includeTrackAndBowl = includeTrackAndBowl;
  }

  public Boolean getIncludeHopper() {
    return includeHopper;
  }

  public void setIncludeHopper(Boolean includeHopper) {
    this.includeHopper = includeHopper;
  }

  public Boolean getIncludeNest() {
    return includeNest;
  }

  public void setIncludeNest(Boolean includeNest) {
    this.includeNest = includeNest;
  }

  public Boolean getIncludeRoost() {
    return includeRoost;
  }

  public void setIncludeRoost(Boolean includeRoost) {
    this.includeRoost = includeRoost;
  }

}

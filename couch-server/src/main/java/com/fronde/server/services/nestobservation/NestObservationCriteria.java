package com.fronde.server.services.nestobservation;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fronde.server.domain.criteria.AbstractCriteria;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public class NestObservationCriteria extends AbstractCriteria {

  private String birdID;
  private String childBirdID;
  private String island;
  private String locationID;
  private String observerPersonID;
  // assume dates in all criteria objects are dates and not date times
  @DateTimeFormat(iso = ISO.DATE)
  private Date fromDate;
  // assume dates in all criteria objects are dates and not date times
  @DateTimeFormat(iso = ISO.DATE)
  private Date toDate;
  private String locationType;

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public String getChildBirdID() {
    return childBirdID;
  }

  public void setChildBirdID(String childBirdID) {
    this.childBirdID = childBirdID;
  }

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

  public String getObserverPersonID() {
    return observerPersonID;
  }

  public void setObserverPersonID(String observerPersonID) {
    this.observerPersonID = observerPersonID;
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

  public String getLocationType() {
    return locationType;
  }

  public void setLocationType(String locationType) {
    this.locationType = locationType;
  }

}

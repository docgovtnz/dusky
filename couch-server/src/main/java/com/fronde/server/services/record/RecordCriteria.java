package com.fronde.server.services.record;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fronde.server.domain.criteria.AbstractCriteria;
import java.util.Date;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public class RecordCriteria extends AbstractCriteria {

  private List<String> birdIDs;
  private Boolean significantEventOnly;
  private List<String> recordTypes;
  private List<String>  reasons;
  private String activity;
  private Boolean withWeightOnly;
  private String island;
  private String locationID;
  private String observerPersonID;
  // assume dates in all criteria objects are dates and not date times
  @DateTimeFormat(iso = ISO.DATE)
  private Date fromDate;
  // assume dates in all criteria objects are dates and not date times
  @DateTimeFormat(iso = ISO.DATE)
  private Date toDate;

  public List<String> getBirdIDs() {
    return birdIDs;
  }

  public void setBirdIDs(List<String> birdIDs) {
    this.birdIDs = birdIDs;
  }

  public Boolean getSignificantEventOnly() {
    return significantEventOnly;
  }

  public void setSignificantEventOnly(Boolean significantEventOnly) {
    this.significantEventOnly = significantEventOnly;
  }

  public List<String>  getRecordTypes() {
    return recordTypes;
  }

  public void setRecordTypes(List<String>  recordTypes) {
    this.recordTypes = recordTypes;
  }

  public List<String>  getReasons() {
    return reasons;
  }

  public void setReasons(List<String>  reasons) {
    this.reasons = reasons;
  }

  public String getActivity() {
    return activity;
  }

  public void setActivity(String activity) {
    this.activity = activity;
  }

  public Boolean getWithWeightOnly() {
    return withWeightOnly;
  }

  public void setWithWeightOnly(Boolean withWeightOnly) {
    this.withWeightOnly = withWeightOnly;
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

}

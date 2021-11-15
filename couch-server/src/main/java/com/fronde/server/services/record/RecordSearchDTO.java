package com.fronde.server.services.record;

import java.util.Date;

public class RecordSearchDTO {

  private String recordID;
  private Date dateTime;
  private String birdID;
  private String birdName;
  private String recorder;
  private String island;
  private String locationID;
  private String locationName;
  private Float locationEasting;
  private Float locationNorthing;
  private String recordType;
  private String activity;
  private Float easting;
  private Float northing;
  private String reason;
  private String subReason;
  private Float weight;
  private boolean hasComment;
  private boolean hasSample;

  public RecordSearchDTO() {
  }

  public String getRecordID() {
    return recordID;
  }

  public void setRecordID(String recordID) {
    this.recordID = recordID;
  }

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
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

  public String getRecorder() {
    return recorder;
  }

  public void setRecorder(String recorder) {
    this.recorder = recorder;
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

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public Float getLocationEasting() {
    return locationEasting;
  }

  public void setLocationEasting(Float locationEasting) {
    this.locationEasting = locationEasting;
  }

  public Float getLocationNorthing() {
    return locationNorthing;
  }

  public void setLocationNorthing(Float locationNorthing) {
    this.locationNorthing = locationNorthing;
  }

  public String getRecordType() {
    return recordType;
  }

  public void setRecordType(String recordType) {
    this.recordType = recordType;
  }

  public String getActivity() {
    return activity;
  }

  public void setActivity(String activity) {
    this.activity = activity;
  }

  public Float getEasting() {
    return easting;
  }

  public void setEasting(Float easting) {
    this.easting = easting;
  }

  public Float getNorthing() {
    return northing;
  }

  public void setNorthing(Float northing) {
    this.northing = northing;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public String getSubReason() { return subReason; }

  public void setSubReason(String subReason) { this.subReason = subReason; }

  public Float getWeight() {
    return weight;
  }

  public void setWeight(Float weight) {
    this.weight = weight;
  }

  public boolean isHasComment() { return hasComment; }

  public void setHasComment(boolean hasComment) { this.hasComment = hasComment; }

  public boolean isHasSample() { return hasSample; }

  public void setHasSample(boolean hasSample) { this.hasSample = hasSample; }
}

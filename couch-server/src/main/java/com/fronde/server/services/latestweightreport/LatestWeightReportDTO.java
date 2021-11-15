package com.fronde.server.services.latestweightreport;

import java.util.Date;

public class LatestWeightReportDTO {

  private String birdName;
  private String birdID;
  private Date latestDate;
  private Float latestWeight;
  private Date previousDate;
  private Float previousWeight;

  public String getBirdName() {
    return birdName;
  }

  public void setBirdName(String birdName) {
    this.birdName = birdName;
  }

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public Date getLatestDate() {
    return latestDate;
  }

  public void setLatestDate(Date latestDate) {
    this.latestDate = latestDate;
  }

  public Float getLatestWeight() {
    return latestWeight;
  }

  public void setLatestWeight(Float latestWeight) {
    this.latestWeight = latestWeight;
  }

  public Date getPreviousDate() {
    return previousDate;
  }

  public void setPreviousDate(Date previousDate) {
    this.previousDate = previousDate;
  }

  public Float getPreviousWeight() {
    return previousWeight;
  }

  public void setPreviousWeight(Float previousWeight) {
    this.previousWeight = previousWeight;
  }
}

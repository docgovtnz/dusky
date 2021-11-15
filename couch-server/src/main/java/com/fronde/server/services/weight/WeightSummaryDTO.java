package com.fronde.server.services.weight;

import java.util.Date;

/**
 *
 */
public class WeightSummaryDTO {

  private String recordId;
  private String method;
  private Float weight;
  private Float ageDays;
  private Date dateTime;
  private Float dailyWeightChange;
  private String cropStatus;

  public WeightSummaryDTO() {
  }

  public String getRecordId() {
    return recordId;
  }

  public void setRecordId(String recordId) {
    this.recordId = recordId;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public Float getWeight() {
    return weight;
  }

  public void setWeight(Float weight) {
    this.weight = weight;
  }

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public Float getDailyWeightChange() {
    return dailyWeightChange;
  }

  public void setDailyWeightChange(Float dailyWeightChange) {
    this.dailyWeightChange = dailyWeightChange;
  }

  public Float getAgeDays() {
    return ageDays;
  }

  public void setAgeDays(Float ageDays) {
    this.ageDays = ageDays;
  }

  public String getCropStatus() {
    return cropStatus;
  }

  public void setCropStatus(String cropStatus) {
    this.cropStatus = cropStatus;
  }
}

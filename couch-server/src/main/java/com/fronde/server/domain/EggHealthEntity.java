package com.fronde.server.domain;

import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class EggHealthEntity {

  private Float lengthInMms;
  private Float widthInMms;
  private Integer candlingAgeEstimateInDays;
  private Float temperature;
  private Integer heartRate;
  private String embryoMoving;

  public Float getLengthInMms() {
    return lengthInMms;
  }

  public void setLengthInMms(Float lengthInMms) {
    this.lengthInMms = lengthInMms;
  }

  public Float getWidthInMms() {
    return widthInMms;
  }

  public void setWidthInMms(Float widthInMms) {
    this.widthInMms = widthInMms;
  }

  public Integer getCandlingAgeEstimateInDays() {
    return candlingAgeEstimateInDays;
  }

  public void setCandlingAgeEstimateInDays(Integer candlingAgeEstimateInDays) {
    this.candlingAgeEstimateInDays = candlingAgeEstimateInDays;
  }

  public Float getTemperature() { return temperature; }

  public void setTemperature(Float temperature) { this.temperature = temperature; }

  public Integer getHeartRate() { return heartRate; }

  public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }

  public String getEmbryoMoving() {
    return embryoMoving;
  }

  public void setEmbryoMoving(String embryoMoving) {
    this.embryoMoving = embryoMoving;
  }

}
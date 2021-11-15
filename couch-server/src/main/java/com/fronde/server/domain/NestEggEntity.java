package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class NestEggEntity {

  @Field
  private String recordID;
  @Field
  private String birdID;
  @Field
  private Float lengthInMms;
  @Field
  private Float widthInMms;
  @Field
  private Float weightInGrams;
  @Field
  private Integer candlingAgeEstimateInDays;
  @Field
  private Float temperature;
  @Field
  private Integer heartRate;
  @Field
  private String embryoMoving;
  @Field
  private String activity;
  @Field
  private String comments;
  @Field
  private String ageClass;
  @Field
  private String milestone;
  @Field
  private String mortality;

  public String getRecordID() {
    return recordID;
  }

  public void setRecordID(String recordID) {
    this.recordID = recordID;
  }

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

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

  public Float getWeightInGrams() {
    return weightInGrams;
  }

  public void setWeightInGrams(Float weightInGrams) {
    this.weightInGrams = weightInGrams;
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

  public String getActivity() {
    return activity;
  }

  public void setActivity(String activity) {
    this.activity = activity;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getAgeClass() {
    return ageClass;
  }

  public void setAgeClass(String ageClass) {
    this.ageClass = ageClass;
  }

  public String getMilestone() {
    return milestone;
  }

  public void setMilestone(String milestone) {
    this.milestone = milestone;
  }

  public String getMortality() {
    return mortality;
  }

  public void setMortality(String mortality) {
    this.mortality = mortality;
  }

}
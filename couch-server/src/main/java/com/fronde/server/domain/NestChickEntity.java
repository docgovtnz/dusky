package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class NestChickEntity {

  @Field
  private String recordID;
  @Field
  private String birdID;
  @Field
  private Float weightInGrams;
  @Field
  private String cropStatus;
  @Field
  private Integer respiratoryRate;
  @Field
  private String comments;
  @Field
  private String activity;
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

  public Float getWeightInGrams() {
    return weightInGrams;
  }

  public void setWeightInGrams(Float weightInGrams) {
    this.weightInGrams = weightInGrams;
  }

  public String getCropStatus() {
    return cropStatus;
  }

  public void setCropStatus(String cropStatus) {
    this.cropStatus = cropStatus;
  }

  public Integer getRespiratoryRate() {
    return respiratoryRate;
  }

  public void setRespiratoryRate(Integer respiratoryRate) {
    this.respiratoryRate = respiratoryRate;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getActivity() {
    return activity;
  }

  public void setActivity(String activity) {
    this.activity = activity;
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
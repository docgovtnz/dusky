package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class HandRaiseEntity {

  @Field
  private Integer amountFed;
  @Field
  private String brooderType;
  @Field
  private String cropStatus;
  @Field
  private Integer dietaryFibre;
  @Field
  private String handRearingFormula;
  @Field
  private Integer largestMeal;
  @Field
  private Boolean medication;
  @Field
  private String newRecord;
  @Field
  private Integer numberOfFeeds;
  @Field
  private Integer percentSolids;
  @Field
  private String recoveryid;
  @Field
  private Integer relativeHumidity;
  @Field
  private Float temp;

  public Integer getAmountFed() {
    return amountFed;
  }

  public void setAmountFed(Integer amountFed) {
    this.amountFed = amountFed;
  }

  public String getBrooderType() {
    return brooderType;
  }

  public void setBrooderType(String brooderType) {
    this.brooderType = brooderType;
  }

  public String getCropStatus() {
    return cropStatus;
  }

  public void setCropStatus(String cropStatus) {
    this.cropStatus = cropStatus;
  }

  public Integer getDietaryFibre() {
    return dietaryFibre;
  }

  public void setDietaryFibre(Integer dietaryFibre) {
    this.dietaryFibre = dietaryFibre;
  }

  public String getHandRearingFormula() {
    return handRearingFormula;
  }

  public void setHandRearingFormula(String handRearingFormula) {
    this.handRearingFormula = handRearingFormula;
  }

  public Integer getLargestMeal() {
    return largestMeal;
  }

  public void setLargestMeal(Integer largestMeal) {
    this.largestMeal = largestMeal;
  }

  public Boolean getMedication() {
    return medication;
  }

  public void setMedication(Boolean medication) {
    this.medication = medication;
  }

  public String getNewRecord() {
    return newRecord;
  }

  public void setNewRecord(String newRecord) {
    this.newRecord = newRecord;
  }

  public Integer getNumberOfFeeds() {
    return numberOfFeeds;
  }

  public void setNumberOfFeeds(Integer numberOfFeeds) {
    this.numberOfFeeds = numberOfFeeds;
  }

  public Integer getPercentSolids() {
    return percentSolids;
  }

  public void setPercentSolids(Integer percentSolids) {
    this.percentSolids = percentSolids;
  }

  public String getRecoveryid() {
    return recoveryid;
  }

  public void setRecoveryid(String recoveryid) {
    this.recoveryid = recoveryid;
  }

  public Integer getRelativeHumidity() {
    return relativeHumidity;
  }

  public void setRelativeHumidity(Integer relativeHumidity) {
    this.relativeHumidity = relativeHumidity;
  }

  public Float getTemp() {
    return temp;
  }

  public void setTemp(Float temp) {
    this.temp = temp;
  }

}
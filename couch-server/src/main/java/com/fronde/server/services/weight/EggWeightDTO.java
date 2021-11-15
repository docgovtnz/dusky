package com.fronde.server.services.weight;

public class EggWeightDTO {

  private String recordID;
  private float ageInDays;
  private float weightInGrams;

  public float getAgeInDays() {
    return ageInDays;
  }

  public void setAgeInDays(float ageInDays) {
    this.ageInDays = ageInDays;
  }

  public float getWeightInGrams() {
    return weightInGrams;
  }

  public void setWeightInGrams(float weightInGrams) {
    this.weightInGrams = weightInGrams;
  }

  public String getRecordID() {
    return recordID;
  }

  public void setRecordID(String recordID) {
    this.recordID = recordID;
  }
}

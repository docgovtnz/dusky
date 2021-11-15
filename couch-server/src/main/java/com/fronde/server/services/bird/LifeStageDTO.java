package com.fronde.server.services.bird;

public class LifeStageDTO {

  private String birdID;
  private String ageClass;
  private String milestone;
  private String mortality;


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

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }
}

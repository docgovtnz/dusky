package com.fronde.server.services.weight;

public class EggReferencePoint {

  private float ageInDays;
  private float predictedCumulativeWeightLossPercentage;
  private float predictedWeight;


  public float getAgeInDays() {
    return ageInDays;
  }

  public void setAgeInDays(float ageInDays) {
    this.ageInDays = ageInDays;
  }

  public float getPredictedCumulativeWeightLossPercentage() {
    return predictedCumulativeWeightLossPercentage;
  }

  public void setPredictedCumulativeWeightLossPercentage(
      float predictedCumulativeWeightLossPercentage) {
    this.predictedCumulativeWeightLossPercentage = predictedCumulativeWeightLossPercentage;
  }

  public float getPredictedWeight() {
    return predictedWeight;
  }

  public void setPredictedWeight(float predictedWeight) {
    this.predictedWeight = predictedWeight;
  }
}

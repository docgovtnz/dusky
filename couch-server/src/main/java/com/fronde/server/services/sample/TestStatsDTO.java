package com.fronde.server.services.sample;

public class TestStatsDTO {

  private String test;
  private Float mean;
  private Float standardDeviation;
  private Float minimum;
  private Float maximum;
  private Float n;

  public String getTest() {
    return test;
  }

  public void setTest(String test) {
    this.test = test;
  }

  public Float getMean() {
    return mean;
  }

  public void setMean(Float mean) {
    this.mean = mean;
  }

  public Float getStandardDeviation() {
    return standardDeviation;
  }

  public void setStandardDeviation(Float standardDeviation) {
    this.standardDeviation = standardDeviation;
  }

  public Float getMinimum() {
    return minimum;
  }

  public void setMinimum(Float minimum) {
    this.minimum = minimum;
  }

  public Float getMaximum() {
    return maximum;
  }

  public void setMaximum(Float maximum) {
    this.maximum = maximum;
  }

  public Float getN() {
    return n;
  }

  public void setN(Float n) {
    this.n = n;
  }

}

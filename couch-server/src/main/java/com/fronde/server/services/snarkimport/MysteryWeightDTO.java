package com.fronde.server.services.snarkimport;

import java.util.Date;

public class MysteryWeightDTO {

  private Date dateTime;
  private Float weight;

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public Float getWeight() {
    return weight;
  }

  public void setWeight(Float weight) {
    this.weight = weight;
  }

  @Override
  public String toString() {
    return "MysteryWeightDTO{" +
        "dateTime=" + dateTime +
        ", weight=" + weight +
        '}';
  }
}

package com.fronde.server.services.bird;

import java.util.Date;

public class CurrentBandDTO {

  private Date dateTime;
  private String newBandNumber;
  private String leg;

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public String getNewBandNumber() {
    return newBandNumber;
  }

  public void setNewBandNumber(String newBandNumber) {
    this.newBandNumber = newBandNumber;
  }

  public String getLeg() {
    return leg;
  }

  public void setLeg(String leg) {
    this.leg = leg;
  }

}

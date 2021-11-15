package com.fronde.server.services.options;

import java.util.Date;

public class TransmitterBirdHistoryDTO2 {

  private String birdId;
  private String birdName;
  private Date installedDate;
  private Date removedDate;
  private String tune;

  public String getBirdId() {
    return birdId;
  }

  public void setBirdId(String birdId) {
    this.birdId = birdId;
  }

  public String getBirdName() {
    return birdName;
  }

  public void setBirdName(String birdName) {
    this.birdName = birdName;
  }

  public Date getInstalledDate() {
    return installedDate;
  }

  public void setInstalledDate(Date installedDate) {
    this.installedDate = installedDate;
  }

  public Date getRemovedDate() {
    return removedDate;
  }

  public void setRemovedDate(Date removedDate) {
    this.removedDate = removedDate;
  }

  public String getTune() {
    return tune;
  }

  public void setTune(String tune) {
    this.tune = tune;
  }
}

package com.fronde.server.services.record;

import java.util.Date;

public class TransmitterBirdHistoryDTO {

  private String birdID;
  private String birdName;
  private Date deployedDateTime;
  private Date removedDateTime;
  private String newTxFineTune;

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public String getBirdName() {
    return birdName;
  }

  public void setBirdName(String birdName) {
    this.birdName = birdName;
  }

  public Date getDeployedDateTime() {
    return deployedDateTime;
  }

  public void setDeployedDateTime(Date deployedDateTime) {
    this.deployedDateTime = deployedDateTime;
  }

  public Date getRemovedDateTime() {
    return removedDateTime;
  }

  public void setRemovedDateTime(Date removedDateTime) {
    this.removedDateTime = removedDateTime;
  }

  public String getNewTxFineTune() {
    return newTxFineTune;
  }

  public void setNewTxFineTune(String newTxFineTune) {
    this.newTxFineTune = newTxFineTune;
  }

}

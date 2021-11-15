package com.fronde.server.services.options;

import java.util.Date;

public class TransmitterSearchDTO {

  private String txDocId;
  private String txId;
  private String status;
  private String mortType;
  private String island;
  private Integer channel;
  private String birdID;
  private String birdName;
  private Integer lifeExpectancy;
  private Date expiryDate;
  private String rigging;
  private Integer uhfId;

  public String getTxDocId() {
    return txDocId;
  }

  public void setTxDocId(String txDocId) {
    this.txDocId = txDocId;
  }

  public String getTxId() {
    return txId;
  }

  public void setTxId(String txId) {
    this.txId = txId;
  }

  public String getMortType() {
    return mortType;
  }

  public void setMortType(String mortType) {
    this.mortType = mortType;
  }

  public String getIsland() {
    return island;
  }

  public void setIsland(String island) {
    this.island = island;
  }

  public Integer getChannel() {
    return channel;
  }

  public void setChannel(Integer channel) {
    this.channel = channel;
  }

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public Integer getLifeExpectancy() {
    return lifeExpectancy;
  }

  public void setLifeExpectancy(Integer lifeExpectancy) {
    this.lifeExpectancy = lifeExpectancy;
  }

  public Date getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(Date expiryDate) {
    this.expiryDate = expiryDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getBirdName() {
    return birdName;
  }

  public void setBirdName(String birdName) {
    this.birdName = birdName;
  }

  public String getRigging() {
    return rigging;
  }

  public void setRigging(String rigging) {
    this.rigging = rigging;
  }

  public Integer getUhfId() {
    return uhfId;
  }

  public void setUhfId(Integer uhfId) {
    this.uhfId = uhfId;
  }
}
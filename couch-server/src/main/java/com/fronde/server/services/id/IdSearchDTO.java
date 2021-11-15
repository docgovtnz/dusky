package com.fronde.server.services.id;

import java.util.Date;

public class IdSearchDTO {

  private String birdId;
  private String birdName;
  private String island;
  private String transmitterGroup;
  private String sex;
  private String ageClass;
  private String txId;
  private String txTo;
  private String txFromId;
  private String txFrom;
  private Date dateTime;
  private Double channel;
  private Float frequency;
  private String txFineTune;
  private Double txRemainingLife;
  private Date expiryDate;
  private String mortType;
  private String action;
  private String software;
  private String chip;
  private String band;
  private String leg;
  private Integer uhfId;

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

  public String getIsland() {
    return island;
  }

  public void setIsland(String island) {
    this.island = island;
  }

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public Double getChannel() {
    return channel;
  }

  public void setChannel(Double channel) {
    this.channel = channel;
  }

  public Float getFrequency() {
    return frequency;
  }

  public void setFrequency(Float frequency) {
    this.frequency = frequency;
  }

  public String getTxFineTune() {
    return txFineTune;
  }

  public void setTxFineTune(String txFineTune) {
    this.txFineTune = txFineTune;
  }

  public Double getTxRemainingLife() {
    return txRemainingLife;
  }

  public void setTxRemainingLife(Double txRemainingLife) {
    this.txRemainingLife = txRemainingLife;
  }

  public String getMortType() {
    return mortType;
  }

  public void setMortType(String mortType) {
    this.mortType = mortType;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getSoftware() {
    return software;
  }

  public void setSoftware(String software) {
    this.software = software;
  }

  public String getChip() {
    return chip;
  }

  public void setChip(String chip) {
    this.chip = chip;
  }

  public String getBand() {
    return band;
  }

  public void setBand(String band) {
    this.band = band;
  }

  public String getLeg() {
    return leg;
  }

  public void setLeg(String leg) {
    this.leg = leg;
  }

  public String getTxId() {
    return txId;
  }

  public void setTxId(String txId) {
    this.txId = txId;
  }

  public String getTxTo() {
    return txTo;
  }

  public void setTxTo(String txTo) {
    this.txTo = txTo;
  }

  public String getTxFromId() {
    return txFromId;
  }

  public void setTxFromId(String txFromId) {
    this.txFromId = txFromId;
  }

  public String getTxFrom() {
    return txFrom;
  }

  public void setTxFrom(String txFrom) {
    this.txFrom = txFrom;
  }

  public Date getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(Date expiryDate) {
    this.expiryDate = expiryDate;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public String getAgeClass() {
    return ageClass;
  }

  public void setAgeClass(String ageClass) {
    this.ageClass = ageClass;
  }

  public String getTransmitterGroup() {
    return transmitterGroup;
  }

  public void setTransmitterGroup(String transmitterGroup) {
    this.transmitterGroup = transmitterGroup;
  }

  public Integer getUhfId() {
    return uhfId;
  }

  public void setUhfId(Integer uhfId) {
    this.uhfId = uhfId;
  }
}

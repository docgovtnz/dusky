package com.fronde.server.services.id;

import com.fronde.server.domain.criteria.AbstractCriteria;

public class IdSearchCriteria extends AbstractCriteria {

  private String bird;
  private String ageClass;
  private String sex;
  private String island;
  private String txId;
  private String transmitterGroup;
  private String mortType;
  private String microchip;
  private String band;
  private Boolean aliveOnly;
  private Boolean latestOnly;
  private String channel;
  private Integer uhfId;

  public String getBird() {
    return bird;
  }

  public void setBird(String bird) {
    this.bird = bird;
  }

  public String getAgeClass() {
    return ageClass;
  }

  public void setAgeClass(String ageClass) {
    this.ageClass = ageClass;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public String getIsland() {
    return island;
  }

  public void setIsland(String island) {
    this.island = island;
  }

  public String getTxId() {
    return txId;
  }

  public void setTxId(String txId) {
    this.txId = txId;
  }

  public String getTransmitterGroup() {
    return transmitterGroup;
  }

  public void setTransmitterGroup(String transmitterGroup) {
    this.transmitterGroup = transmitterGroup;
  }

  public String getMortType() {
    return mortType;
  }

  public void setMortType(String mortType) {
    this.mortType = mortType;
  }

  public String getMicrochip() {
    return microchip;
  }

  public void setMicrochip(String microchip) {
    this.microchip = microchip;
  }

  public String getBand() {
    return band;
  }

  public void setBand(String band) {
    this.band = band;
  }

  public Boolean getAliveOnly() {
    return aliveOnly;
  }

  public void setAliveOnly(Boolean aliveOnly) {
    this.aliveOnly = aliveOnly;
  }

  public Boolean getLatestOnly() {
    return latestOnly;
  }

  public void setLatestOnly(Boolean latestOnly) {
    this.latestOnly = latestOnly;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public Integer getUhfId() {
    return uhfId;
  }

  public void setUhfId(Integer uhfId) {
    this.uhfId = uhfId;
  }
}

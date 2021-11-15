package com.fronde.server.services.transmitter;

import com.fronde.server.domain.criteria.AbstractCriteria;

public class TransmitterCriteria extends AbstractCriteria {

  private String txId;
  private String transmitterGroup;
  private String currentIsland;
  private String txMortalityTypes;
  private String rigging;
  private String channel;
  private Boolean spareOnly;
  private Boolean showDecommissioned;
  private String birdID;
  private String ageClass;
  private Boolean aliveOnly;
  private String sex;
  private Integer uhfId;

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

  public String getCurrentIsland() {
    return currentIsland;
  }

  public void setCurrentIsland(String currentIsland) {
    this.currentIsland = currentIsland;
  }

  public String getTxMortalityTypes() {
    return txMortalityTypes;
  }

  public void setTxMortalityTypes(String txMortalityTypes) {
    this.txMortalityTypes = txMortalityTypes;
  }

  public String getRigging() {
    return rigging;
  }

  public void setRigging(String rigging) {
    this.rigging = rigging;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public Boolean getSpareOnly() {
    return spareOnly;
  }

  public void setSpareOnly(Boolean spareOnly) {
    this.spareOnly = spareOnly;
  }

  public Boolean getShowDecommissioned() {
    return showDecommissioned;
  }

  public void setShowDecommissioned(Boolean showDecommissioned) {
    this.showDecommissioned = showDecommissioned;
  }

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public String getAgeClass() {
    return ageClass;
  }

  public void setAgeClass(String ageClass) {
    this.ageClass = ageClass;
  }

  public Boolean getAliveOnly() {
    return aliveOnly;
  }

  public void setAliveOnly(Boolean aliveOnly) {
    this.aliveOnly = aliveOnly;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public Integer getUhfId() {
    return uhfId;
  }

  public void setUhfId(Integer uhfId) {
    this.uhfId = uhfId;
  }
}

package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class TransmitterEntity extends BaseEntity {

  @Field
  private Integer channel;
  @Field
  private Float frequency;
  @Field
  private String island;
  @Field
  private String lastRecordId;
  @Field
  private Integer lifeExpectancy;
  @Field
  private Integer oldTxMortId;
  @Field
  private Integer origLifeExpectancyWeeks;
  @Field
  private String rigging;
  @Field
  private String software;
  @Field
  private String status;
  @Field
  private String txFineTune;
  @Field
  private String txId;
  @Field
  private String txMortalityId;
  @Field
  private Integer uhfId;

  public Integer getChannel() {
    return channel;
  }

  public void setChannel(Integer channel) {
    this.channel = channel;
  }

  public Float getFrequency() {
    return frequency;
  }

  public void setFrequency(Float frequency) {
    this.frequency = frequency;
  }

  public String getIsland() {
    return island;
  }

  public void setIsland(String island) {
    this.island = island;
  }

  public String getLastRecordId() {
    return lastRecordId;
  }

  public void setLastRecordId(String lastRecordId) {
    this.lastRecordId = lastRecordId;
  }

  public Integer getLifeExpectancy() {
    return lifeExpectancy;
  }

  public void setLifeExpectancy(Integer lifeExpectancy) {
    this.lifeExpectancy = lifeExpectancy;
  }

  public Integer getOldTxMortId() {
    return oldTxMortId;
  }

  public void setOldTxMortId(Integer oldTxMortId) {
    this.oldTxMortId = oldTxMortId;
  }

  public Integer getOrigLifeExpectancyWeeks() {
    return origLifeExpectancyWeeks;
  }

  public void setOrigLifeExpectancyWeeks(Integer origLifeExpectancyWeeks) {
    this.origLifeExpectancyWeeks = origLifeExpectancyWeeks;
  }

  public String getRigging() {
    return rigging;
  }

  public void setRigging(String rigging) {
    this.rigging = rigging;
  }

  public String getSoftware() {
    return software;
  }

  public void setSoftware(String software) {
    this.software = software;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getTxFineTune() {
    return txFineTune;
  }

  public void setTxFineTune(String txFineTune) {
    this.txFineTune = txFineTune;
  }

  public String getTxId() {
    return txId;
  }

  public void setTxId(String txId) {
    this.txId = txId;
  }

  public String getTxMortalityId() {
    return txMortalityId;
  }

  public void setTxMortalityId(String txMortalityId) {
    this.txMortalityId = txMortalityId;
  }

  public Integer getUhfId() {
    return uhfId;
  }

  public void setUhfId(Integer uhfId) {
    this.uhfId = uhfId;
  }
}
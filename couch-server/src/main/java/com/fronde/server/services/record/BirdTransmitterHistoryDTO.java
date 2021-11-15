package com.fronde.server.services.record;

import java.util.Date;

/**
 * Data transfer object for historical data of a transmitter associated with a bird.
 */
public class BirdTransmitterHistoryDTO {

  private String txDocId;
  private Date recordDateTime;
  private String recordIsland;
  private Integer channel;
  private String mortType;
  private String software;
  private String newStatus;
  private String txFineTune;
  private String txId;
  private Integer uhfId;

  public String getTxDocId() {
    return txDocId;
  }

  public void setTxDocId(String txDocId) {
    this.txDocId = txDocId;
  }

  public Date getRecordDateTime() {
    return recordDateTime;
  }

  public void setRecordDateTime(Date recordDateTime) {
    this.recordDateTime = recordDateTime;
  }

  public String getRecordIsland() {
    return recordIsland;
  }

  public void setRecordIsland(String recordIsland) {
    this.recordIsland = recordIsland;
  }

  public Integer getChannel() {
    return channel;
  }

  public void setChannel(Integer channel) {
    this.channel = channel;
  }

  public String getMortType() {
    return mortType;
  }

  public void setMortType(String mortType) {
    this.mortType = mortType;
  }

  public String getSoftware() {
    return software;
  }

  public void setSoftware(String software) {
    this.software = software;
  }

  public String getNewStatus() {
    return newStatus;
  }

  public void setNewStatus(String newStatus) {
    this.newStatus = newStatus;
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

  public Integer getUhfId() {
    return uhfId;
  }

  public void setUhfId(Integer uhfId) {
    this.uhfId = uhfId;
  }
}

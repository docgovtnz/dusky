package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class TransmitterChangeEntity {

  @Field
  private String addedTxFromLastRecordID;
  @Field
  private String addedTxFromStatus;
  @Field
  private String addedTxFromTxFineTune;
  @Field
  private Boolean harnessChangeOnly;
  @Field
  private Float hoursOff;
  @Field
  private Float hoursOn;
  @Field
  private String newStatus;
  @Field
  private String newTxFineTune;
  @Field
  private Boolean prox;
  @Field
  private Boolean proxOn;
  @Field
  private String recoveryID;
  @Field
  private Boolean removed;
  @Field
  private String removedTxFromLastRecordID;
  @Field
  private String removedTxFromStatus;
  @Field
  private Date starttime;
  @Field
  private String txchangeid;
  @Field
  private String txDocId;
  @Field
  private String txFrom;
  @Field
  private String txFromStatus;
  @Field
  private String txId;
  @Field
  private Integer txLifeExpectancyWeeks;
  @Field
  private Float txMortality;
  @Field
  private String txTo;
  @Field
  private Float vhfHoursOn;
  @Field
  private Integer uhfId;

  public String getAddedTxFromLastRecordID() {
    return addedTxFromLastRecordID;
  }

  public void setAddedTxFromLastRecordID(String addedTxFromLastRecordID) {
    this.addedTxFromLastRecordID = addedTxFromLastRecordID;
  }

  public String getAddedTxFromStatus() {
    return addedTxFromStatus;
  }

  public void setAddedTxFromStatus(String addedTxFromStatus) {
    this.addedTxFromStatus = addedTxFromStatus;
  }

  public String getAddedTxFromTxFineTune() {
    return addedTxFromTxFineTune;
  }

  public void setAddedTxFromTxFineTune(String addedTxFromTxFineTune) {
    this.addedTxFromTxFineTune = addedTxFromTxFineTune;
  }

  public Boolean getHarnessChangeOnly() {
    return harnessChangeOnly;
  }

  public void setHarnessChangeOnly(Boolean harnessChangeOnly) {
    this.harnessChangeOnly = harnessChangeOnly;
  }

  public Float getHoursOff() {
    return hoursOff;
  }

  public void setHoursOff(Float hoursOff) {
    this.hoursOff = hoursOff;
  }

  public Float getHoursOn() {
    return hoursOn;
  }

  public void setHoursOn(Float hoursOn) {
    this.hoursOn = hoursOn;
  }

  public String getNewStatus() {
    return newStatus;
  }

  public void setNewStatus(String newStatus) {
    this.newStatus = newStatus;
  }

  public String getNewTxFineTune() {
    return newTxFineTune;
  }

  public void setNewTxFineTune(String newTxFineTune) {
    this.newTxFineTune = newTxFineTune;
  }

  public Boolean getProx() {
    return prox;
  }

  public void setProx(Boolean prox) {
    this.prox = prox;
  }

  public Boolean getProxOn() {
    return proxOn;
  }

  public void setProxOn(Boolean proxOn) {
    this.proxOn = proxOn;
  }

  public String getRecoveryID() {
    return recoveryID;
  }

  public void setRecoveryID(String recoveryID) {
    this.recoveryID = recoveryID;
  }

  public Boolean getRemoved() {
    return removed;
  }

  public void setRemoved(Boolean removed) {
    this.removed = removed;
  }

  public String getRemovedTxFromLastRecordID() {
    return removedTxFromLastRecordID;
  }

  public void setRemovedTxFromLastRecordID(String removedTxFromLastRecordID) {
    this.removedTxFromLastRecordID = removedTxFromLastRecordID;
  }

  public String getRemovedTxFromStatus() {
    return removedTxFromStatus;
  }

  public void setRemovedTxFromStatus(String removedTxFromStatus) {
    this.removedTxFromStatus = removedTxFromStatus;
  }

  public Date getStarttime() {
    return starttime;
  }

  public void setStarttime(Date starttime) {
    this.starttime = starttime;
  }

  public String getTxchangeid() {
    return txchangeid;
  }

  public void setTxchangeid(String txchangeid) {
    this.txchangeid = txchangeid;
  }

  public String getTxDocId() {
    return txDocId;
  }

  public void setTxDocId(String txDocId) {
    this.txDocId = txDocId;
  }

  public String getTxFrom() {
    return txFrom;
  }

  public void setTxFrom(String txFrom) {
    this.txFrom = txFrom;
  }

  public String getTxFromStatus() {
    return txFromStatus;
  }

  public void setTxFromStatus(String txFromStatus) {
    this.txFromStatus = txFromStatus;
  }

  public String getTxId() {
    return txId;
  }

  public void setTxId(String txId) {
    this.txId = txId;
  }

  public Integer getTxLifeExpectancyWeeks() {
    return txLifeExpectancyWeeks;
  }

  public void setTxLifeExpectancyWeeks(Integer txLifeExpectancyWeeks) {
    this.txLifeExpectancyWeeks = txLifeExpectancyWeeks;
  }

  public Float getTxMortality() {
    return txMortality;
  }

  public void setTxMortality(Float txMortality) {
    this.txMortality = txMortality;
  }

  public String getTxTo() {
    return txTo;
  }

  public void setTxTo(String txTo) {
    this.txTo = txTo;
  }

  public Float getVhfHoursOn() {
    return vhfHoursOn;
  }

  public void setVhfHoursOn(Float vhfHoursOn) {
    this.vhfHoursOn = vhfHoursOn;
  }

  public Integer getUhfId() {
    return uhfId;
  }

  public void setUhfId(Integer uhfId) {
    this.uhfId = uhfId;
  }
}
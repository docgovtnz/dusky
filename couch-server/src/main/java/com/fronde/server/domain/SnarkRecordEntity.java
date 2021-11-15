package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import java.util.List;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class SnarkRecordEntity {

  @Field
  private String activity;
  @Field
  private Date arriveDateTime;
  @Field
  private String birdCert;
  // if more than one Bird will be null until the user picks one bird from possibleBirdList
  @Field
  private String birdID;
  @Field
  private String comments;
  // list of possible BirdIDs at the time the snark record was processed
  @Field
  private List<String> possibleBirdList;
  @Field
  private Integer channel;
  @Field
  private Date dateTime;
  @Field
  private Date departDateTime;
  @Field
  private boolean include;
  @Field
  private Boolean mating;
  @Field
  private String oldBirdId;
  @Field
  private String oldSnarkRecId;
  @Field
  private String recordID;
  @Field
  private String recordType;
  @Field
  private String tandbrecid;
  @Field
  private Integer uhfId;
  @Field
  private Float weight;
  @Field
  private Integer weightQuality;
  @Field
  private Integer lockCount;

  public String getActivity() {
    return activity;
  }

  public void setActivity(String activity) {
    this.activity = activity;
  }

  public Date getArriveDateTime() {
    return arriveDateTime;
  }

  public void setArriveDateTime(Date arriveDateTime) {
    this.arriveDateTime = arriveDateTime;
  }

  public String getBirdCert() {
    return birdCert;
  }

  public void setBirdCert(String birdCert) {
    this.birdCert = birdCert;
  }

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public Integer getChannel() {
    return channel;
  }

  public void setChannel(Integer channel) {
    this.channel = channel;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public Date getDepartDateTime() {
    return departDateTime;
  }

  public void setDepartDateTime(Date departDateTime) {
    this.departDateTime = departDateTime;
  }

  public Boolean getMating() {
    return mating;
  }

  public void setMating(Boolean mating) {
    this.mating = mating;
  }

  public String getOldBirdId() {
    return oldBirdId;
  }

  public void setOldBirdId(String oldBirdId) {
    this.oldBirdId = oldBirdId;
  }

  public String getOldSnarkRecId() {
    return oldSnarkRecId;
  }

  public void setOldSnarkRecId(String oldSnarkRecId) {
    this.oldSnarkRecId = oldSnarkRecId;
  }

  public List<String> getPossibleBirdList() {
    return possibleBirdList;
  }

  public void setPossibleBirdList(List<String> possibleBirdList) {
    this.possibleBirdList = possibleBirdList;
  }

  public String getRecordID() {
    return recordID;
  }

  public void setRecordID(String recordID) {
    this.recordID = recordID;
  }

  public String getRecordType() {
    return recordType;
  }

  public void setRecordType(String recordType) {
    this.recordType = recordType;
  }

  public String getTandbrecid() {
    return tandbrecid;
  }

  public void setTandbrecid(String tandbrecid) {
    this.tandbrecid = tandbrecid;
  }

  public Integer getUhfId() {
    return uhfId;
  }

  public void setUhfId(Integer uhfId) {
    this.uhfId = uhfId;
  }

  public Float getWeight() {
    return weight;
  }

  public void setWeight(Float weight) {
    this.weight = weight;
  }

  public Integer getWeightQuality() {
    return weightQuality;
  }

  public void setWeightQuality(Integer weightQuality) {
    this.weightQuality = weightQuality;
  }

  public boolean isInclude() {
    return include;
  }

  public void setInclude(boolean include) {
    this.include = include;
  }

  public Integer getLockCount() { return lockCount; }

  public void setLockCount(Integer lockCount) { this.lockCount = lockCount; }
}

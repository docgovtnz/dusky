package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class SnarkImportEntity extends BaseEntity {

  @Field
  private String activityType;
  @Field
  private String island;
  @Field
  private String locationID;
  @Field
  private String observerPersonID;
  @Field
  private Integer qualityOverride;
  @Field
  private String snarkFileContent;
  @Field
  private String snarkFileHash;
  @Field
  private String snarkFileName;
  @Field
  private Boolean showLockRecords;

  public String getActivityType() {
    return activityType;
  }

  public void setActivityType(String activityType) {
    this.activityType = activityType;
  }

  public String getIsland() {
    return island;
  }

  public void setIsland(String island) {
    this.island = island;
  }

  public String getLocationID() {
    return locationID;
  }

  public void setLocationID(String locationID) {
    this.locationID = locationID;
  }

  public String getObserverPersonID() {
    return observerPersonID;
  }

  public void setObserverPersonID(String observerPersonID) {
    this.observerPersonID = observerPersonID;
  }

  public Integer getQualityOverride() {
    return qualityOverride;
  }

  public void setQualityOverride(Integer qualityOverride) {
    this.qualityOverride = qualityOverride;
  }

  public String getSnarkFileContent() {
    return snarkFileContent;
  }

  public void setSnarkFileContent(String snarkFileContent) {
    this.snarkFileContent = snarkFileContent;
  }

  public String getSnarkFileHash() {
    return snarkFileHash;
  }

  public void setSnarkFileHash(String snarkFileHash) {
    this.snarkFileHash = snarkFileHash;
  }

  public String getSnarkFileName() {
    return snarkFileName;
  }

  public void setSnarkFileName(String snarkFileName) {
    this.snarkFileName = snarkFileName;
  }

}
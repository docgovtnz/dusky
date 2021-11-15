package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;
import java.util.Date;
import java.util.List;

/**
 * Entity to store NoraNet data in Dusky couchbase database, docType is "NoraNet"
 *
 * @version 1.0
 * @date 14/07/2021
 */

@Document
public class NoraNetEntity extends BaseEntity {

  @Field
  private String island;
  @Field
  private String stationId;
  @Field
  private Date fileDate;
  @Field
  private Date activityDate;
  @Field
  private Integer dataVersion;
  @Field
  private Integer locationCode;
  @Field
  private Integer batteryVolts;
  @Field
  private String recordCounts;
  @Field
  private List<NoraNetDetectionEntity> detectionList;
  @Field
  private List<NoraNetStandardEntity> standardList;
  @Field
  private List<NoraNetEggTimerEntity> eggTimerList;
  @Field
  private List<NoraNetCmShortEntity> cmShortList;
  @Field
  private List<NoraNetCmLongEntity> cmLongList;
  @Field
  private List<NoraNetWeightEntity> weightList;

  public String getIsland() {
    return island;
  }

  public void setIsland(String island) {
    this.island = island;
  }

  public String getStationId() {
    return stationId;
  }

  public void setStationId(String stationId) {
    this.stationId = stationId;
  }

  public Date getFileDate() {
    return fileDate;
  }

  public void setFileDate(Date fileDate) {
    this.fileDate = fileDate;
  }

  public Date getActivityDate() {
    return activityDate;
  }

  public void setActivityDate(Date activityDate) {
    this.activityDate = activityDate;
  }

  public Integer getDataVersion() {
    return dataVersion;
  }

  public void setDataVersion(Integer dataVersion) {
    this.dataVersion = dataVersion;
  }

  public Integer getLocationCode() { return locationCode; }

  public void setLocationCode(Integer locationCode) { this.locationCode = locationCode; }

  public Integer getBatteryVolts() {
    return batteryVolts;
  }

  public void setBatteryVolts(Integer batteryVolts) {
    this.batteryVolts = batteryVolts;
  }

  public String getRecordCounts() {
    return recordCounts;
  }

  public void setRecordCounts(String recordCounts) {
    this.recordCounts = recordCounts;
  }

  public List<NoraNetDetectionEntity> getDetectionList() {
    return detectionList;
  }

  public void setDetectionList(List<NoraNetDetectionEntity> detectionList) {
    this.detectionList = detectionList;
  }

  public List<NoraNetStandardEntity> getStandardList() {
    return standardList;
  }

  public void setStandardList(List<NoraNetStandardEntity> standardList) {
    this.standardList = standardList;
  }

  public List<NoraNetEggTimerEntity> getEggTimerList() {
    return eggTimerList;
  }

  public void setEggTimerList(List<NoraNetEggTimerEntity> eggTimerList) {
    this.eggTimerList = eggTimerList;
  }

  public List<NoraNetCmShortEntity> getCmShortList() {
    return cmShortList;
  }

  public void setCmShortList(List<NoraNetCmShortEntity> cmShortList) {
    this.cmShortList = cmShortList;
  }

  public List<NoraNetCmLongEntity> getCmLongList() {
    return cmLongList;
  }

  public void setCmLongList(List<NoraNetCmLongEntity> cmLongList) {
    this.cmLongList = cmLongList;
  }

  public List<NoraNetWeightEntity> getWeightList() { return weightList; }

  public void setWeightList(List<NoraNetWeightEntity> weightList) { this.weightList = weightList; }
}

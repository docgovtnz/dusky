package com.fronde.server.services.noranet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * Data entity de-serialised directly from the Json file (name and data) received from AWS S3 bucket
 *
 * @version 1.0
 * @date 14/07/2021
 */

public class NoraNetS3DataFile {

  // information from the file name
  @JsonIgnore
  private String dataType;
  @JsonIgnore
  private Integer islandNo;
  @JsonIgnore
  private String station;
  @JsonIgnore
  private Date fileDate;

  // file data
  @JsonProperty("IDofSnark")
  private Integer idOfSnark;
  @JsonProperty("TypeOfSnark")
  private Integer typeOfSnark;
  @JsonProperty("IDofIsland")
  private Integer idOfIsland;
  @JsonProperty("DataVersion")
  private Integer dataVersion;
  @JsonProperty("BatteryVolts")
  private Integer batteryVolts;
  @JsonProperty("LocationCode")
  private Integer locationCode;
  @JsonProperty("HeaderDateTime")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private Date headerDateTime;
  @JsonProperty("RecordCounts")
  private String recordCounts;
  @JsonProperty("Records")
  private List<NoraNetS3DataRecord> recordList;

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public Integer getIslandNo() {
    return islandNo;
  }

  public void setIslandNo(Integer islandNo) {
    this.islandNo = islandNo;
  }

  public String getStation() {
    return station;
  }

  public void setStation(String station) {
    this.station = station;
  }

  public Date getFileDate() {
    return fileDate;
  }

  public void setFileDate(Date fileDate) {
    this.fileDate = fileDate;
  }

  public Integer getIdOfSnark() {
    return idOfSnark;
  }

  public void setIdOfSnark(Integer idOfSnark) {
    this.idOfSnark = idOfSnark;
  }

  public Integer getTypeOfSnark() {
    return typeOfSnark;
  }

  public void setTypeOfSnark(Integer typeOfSnark) {
    this.typeOfSnark = typeOfSnark;
  }

  public Integer getIdOfIsland() {
    return idOfIsland;
  }

  public void setIdOfIsland(Integer idOfIsland) {
    this.idOfIsland = idOfIsland;
  }

  public Integer getDataVersion() {
    return dataVersion;
  }

  public void setDataVersion(Integer dataVersion) {
    this.dataVersion = dataVersion;
  }

  public Integer getBatteryVolts() {
    return batteryVolts;
  }

  public void setBatteryVolts(Integer batteryVolts) {
    this.batteryVolts = batteryVolts;
  }

  public Integer getLocationCode() {
    return locationCode;
  }

  public void setLocationCode(Integer locationCode) {
    this.locationCode = locationCode;
  }

  public Date getHeaderDateTime() { return headerDateTime; }

  public void setHeaderDateTime(Date headerDateTime) { this.headerDateTime = headerDateTime; }

  public String getRecordCounts() { return recordCounts; }

  public void setRecordCounts(String recordCounts) { this.recordCounts = recordCounts; }

  public List<NoraNetS3DataRecord> getRecordList() { return recordList; }

  public void setRecordList(List<NoraNetS3DataRecord> recordList) {
    this.recordList = recordList;
  }

}

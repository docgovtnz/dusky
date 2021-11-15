package com.fronde.server.services.noranet;

import com.fronde.server.domain.criteria.AbstractCriteria;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import java.util.Date;
import java.util.List;

/**
 * @version 1.0
 * @date 14/07/2021
 */

public class NoraNetCriteria extends AbstractCriteria {

  private String island;
  private String stationId;
  private Date fileDate;
  private String dataType;
  // assume dates in all criteria objects are dates and not date times
  @DateTimeFormat(iso = ISO.DATE)
  private Date fromActivityDate;
  // assume dates in all criteria objects are dates and not date times
  @DateTimeFormat(iso = ISO.DATE)
  private Date toActivityDate;
  private Date activityDate;
  private List<String> birdIDs;
  private Integer uhfId;
  private boolean undetectedBirds;

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

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public Date getFromActivityDate() {
    return fromActivityDate;
  }

  public void setFromActivityDate(Date fromActivityDate) {
    this.fromActivityDate = fromActivityDate;
  }

  public Date getToActivityDate() {
    return toActivityDate;
  }

  public void setToActivityDate(Date toActivityDate) {
    this.toActivityDate = toActivityDate;
  }

  public Date getActivityDate() {
    return activityDate;
  }

  public void setActivityDate(Date activityDate) {
    this.activityDate = activityDate;
  }

  public List<String> getBirdIDs() {
    return birdIDs;
  }

  public void setBirdIDs(List<String> birdIDs) {
    this.birdIDs = birdIDs;
  }

  public Integer getUhfId() {
    return uhfId;
  }

  public void setUhfId(Integer uhfId) {
    this.uhfId = uhfId;
  }

  public boolean isUndetectedBirds() { return undetectedBirds; }

  public void setUndetectedBirds(boolean undetectedBirds) {
    this.undetectedBirds = undetectedBirds;
  }
}

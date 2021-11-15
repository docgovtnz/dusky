package com.fronde.server.services.matingreport;

import java.util.Date;

public class MatingReportDTO {

  private Date dateTime;
  private String island;
  private String femaleBirdID;
  private String femaleBirdName;
  private String maleBirdID;
  private String maleBirdName;
  private Integer duration;
  private Integer quality;
  private Integer femaleMatingCount;

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public String getIsland() {
    return island;
  }

  public void setIsland(String island) {
    this.island = island;
  }

  public String getFemaleBirdID() {
    return femaleBirdID;
  }

  public void setFemaleBirdID(String femaleBirdID) {
    this.femaleBirdID = femaleBirdID;
  }

  public String getFemaleBirdName() {
    return femaleBirdName;
  }

  public void setFemaleBirdName(String femaleBirdName) {
    this.femaleBirdName = femaleBirdName;
  }

  public String getMaleBirdID() {
    return maleBirdID;
  }

  public void setMaleBirdID(String maleBirdID) {
    this.maleBirdID = maleBirdID;
  }

  public String getMaleBirdName() {
    return maleBirdName;
  }

  public void setMaleBirdName(String maleBirdName) {
    this.maleBirdName = maleBirdName;
  }

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public Integer getQuality() {
    return quality;
  }

  public void setQuality(Integer quality) {
    this.quality = quality;
  }

  public Integer getFemaleMatingCount() {
    return femaleMatingCount;
  }

  public void setFemaleMatingCount(Integer femaleMatingCount) {
    this.femaleMatingCount = femaleMatingCount;
  }

}

package com.fronde.server.services.noranet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoraNetSearchDTO {

  private String noraNetId;
  private String island;
  private Date activityDate;
  private String stationId;
  private Integer batteryVolts;
  private String dataType;
  private Integer uhfId;
  private List<Bird> birdList;
  private String category;
  private Integer pulseCount;
  private Integer peakTwitch;
  private Float activity;
  private Integer batteryLife;
  private Boolean incubating;
  private Integer daysSinceChange;
  private Integer matingAge;
  private Integer cmHour;
  private Integer cmMinute;
  private Integer lastCmHour;
  private Integer lastCmMinute;
  private List<Female> cmFemaleList = new ArrayList<>();

  public NoraNetSearchDTO() {
  }

  public String getNoraNetId() {
    return noraNetId;
  }

  public void setNoraNetId(String noraNetId) {
    this.noraNetId = noraNetId;
  }

  public String getIsland() {
    return island;
  }

  public void setIsland(String island) {
    this.island = island;
  }

  public Date getActivityDate() { return activityDate; }

  public void setActivityDate(Date activityDate) {
    this.activityDate = activityDate;
  }

  public String getStationId() {
    return stationId;
  }

  public void setStationId(String stationId) {
    this.stationId = stationId;
  }

  public Integer getBatteryVolts() {
    return batteryVolts;
  }

  public void setBatteryVolts(Integer batteryVolts) {
    this.batteryVolts = batteryVolts;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public Integer getUhfId() {
    return uhfId;
  }

  public void setUhfId(Integer uhfId) {
    this.uhfId = uhfId;
  }

  public List<Bird> getBirdList() { return birdList; }

  public void setBirdList(List<Bird> birdList) { this.birdList = birdList; }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public Integer getPulseCount() {
    return pulseCount;
  }

  public void setPulseCount(Integer pulseCount) {
    this.pulseCount = pulseCount;
  }

  public Integer getPeakTwitch() {
    return peakTwitch;
  }

  public void setPeakTwitch(Integer peakTwitch) {
    this.peakTwitch = peakTwitch;
  }

  public Float getActivity() {
    return activity;
  }

  public void setActivity(Float activity) {
    this.activity = activity;
  }

  public Integer getBatteryLife() {
    return batteryLife;
  }

  public void setBatteryLife(Integer batteryLife) {
    this.batteryLife = batteryLife;
  }

  public Boolean getIncubating() { return incubating; }

  public void setIncubating(Boolean incubating) { this.incubating = incubating; }

  public Integer getDaysSinceChange() { return daysSinceChange; }

  public void setDaysSinceChange(Integer daysSinceChange) {
    this.daysSinceChange = daysSinceChange;
  }

  public Integer getMatingAge() { return matingAge; }

  public void setMatingAge(Integer matingAge) { this.matingAge = matingAge; }

  public Integer getCmHour() { return cmHour; }

  public void setCmHour(Integer cmHour) { this.cmHour = cmHour; }

  public Integer getCmMinute() { return cmMinute; }

  public void setCmMinute(Integer cmMinute) { this.cmMinute = cmMinute; }

  public Integer getLastCmHour() { return lastCmHour; }

  public void setLastCmHour(Integer lastCmHour) { this.lastCmHour = lastCmHour; }

  public Integer getLastCmMinute() { return lastCmMinute; }

  public void setLastCmMinute(Integer lastCmMinute) { this.lastCmMinute = lastCmMinute; }

  public List<Female> getCmFemaleList() { return cmFemaleList; }

  public void setCmFemaleList(List<Female> cmFemaleList) { this.cmFemaleList = cmFemaleList; }

  public static class Female {

    private Integer uhfId;
    private List<Bird> birdList;
    private Integer startTimeHoursAgo;
    private Integer duration;
    private Integer quality;

    public Integer getUhfId() { return uhfId; }

    public void setUhfId(Integer uhfId) { this.uhfId = uhfId; }

    public List<Bird> getBirdList() { return birdList; }

    public void setBirdList(List<Bird> birdList) { this.birdList = birdList; }

    public Integer getStartTimeHoursAgo() { return startTimeHoursAgo; }

    public void setStartTimeHoursAgo(Integer startTimeHoursAgo) {
      this.startTimeHoursAgo = startTimeHoursAgo;
    }

    public Integer getDuration() { return duration; }

    public void setDuration(Integer duration) { this.duration = duration; }

    public Integer getQuality() { return quality; }

    public void setQuality(Integer quality) { this.quality = quality; }
  }

  public static class Bird {
    private String birdName;
    private String birdID;

    public String getBirdName() { return birdName; }

    public void setBirdName(String birdName) { this.birdName = birdName; }

    public String getBirdID() { return birdID; }

    public void setBirdID(String birdID) { this.birdID = birdID; }
  }
}

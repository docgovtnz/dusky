package com.fronde.server.services.noranet;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data entity de-serialised directly from the Json data "Records" received from AWS S3 bucket
 *
 * @version 1.0
 * @date 14/07/2021
 */

public class NoraNetS3DataRecord {

  // file bird record data
  @JsonProperty("KakapoID")
  @JsonAlias("MaleKakapoID")
  private Integer kakapoId;
  @JsonProperty("Category")
  private String category;
  @JsonProperty("PulseCount")
  private Integer pulseCount;
  @JsonProperty("PeakTwitch")
  private Integer peakTwitch;
  @JsonProperty("Activity")
  private Float activity;
  @JsonProperty("BatteryLife")
  @JsonAlias("BatteryWeeks")
  private Integer batteryLife;
  @JsonProperty("IncubatingFlag")
  private Boolean incubatingFlag;
  @JsonProperty("DaysSinceChange")
  private Integer daysSinceChange;
  @JsonProperty("MatingAge")
  private Integer matingAge;
  @JsonProperty("CMhour")
  private Integer cmHour;
  @JsonProperty("CMminute")
  private Integer cmMinute;
  @JsonProperty("LastCMhour")
  private Integer lastCmHour;
  @JsonProperty("LastCMminute")
  private Integer lastCmMinute;

  @JsonProperty("FemaleID1")
  private Integer femaleId1;
  @JsonProperty("StartTimeHoursAgo1")
  private Integer startTimeHoursAgo1;
  @JsonProperty("Duration1")
  private Integer duration1;
  @JsonProperty("Quality1")
  private Integer quality1;
  @JsonProperty("FemaleID2")
  private Integer femaleId2;
  @JsonProperty("StartTimeHoursAgo2")
  private Integer startTimeHoursAgo2;
  @JsonProperty("Duration2")
  private Integer duration2;
  @JsonProperty("Quality2")
  private Integer quality2;
  @JsonProperty("FemaleID3")
  private Integer femaleId3;
  @JsonProperty("StartTimeHoursAgo3")
  private Integer startTimeHoursAgo3;
  @JsonProperty("Duration3")
  private Integer duration3;
  @JsonProperty("Quality3")
  private Integer quality3;
  @JsonProperty("FemaleID4")
  private Integer femaleId4;
  @JsonProperty("StartTimeHoursAgo4")
  private Integer startTimeHoursAgo4;
  @JsonProperty("Duration4")
  private Integer duration4;
  @JsonProperty("Quality4")
  private Integer quality4;

  @JsonProperty("WeightBin")
  private Integer weightBin;
  @JsonProperty("BinCount")
  private Integer binCount;
  @JsonProperty("MaxQuality")
  private Integer maxQuality;

  public Integer getKakapoId() { return kakapoId; }

  public void setKakapoId(Integer kakapoId) { this.kakapoId = kakapoId; }

  public String getCategory() { return category; }

  public void setCategory(String category) { this.category = category; }

  public Integer getPulseCount() { return pulseCount; }

  public void setPulseCount(Integer pulseCount) { this.pulseCount = pulseCount; }

  public Integer getPeakTwitch() { return peakTwitch; }

  public void setPeakTwitch(Integer peakTwitch) { this.peakTwitch = peakTwitch; }

  public Float getActivity() { return activity; }

  public void setActivity(Float activity) { this.activity = activity; }

  public Integer getBatteryLife() { return batteryLife; }

  public void setBatteryLife(Integer batteryLife) { this.batteryLife = batteryLife; }

  public Boolean getIncubatingFlag() { return incubatingFlag; }

  public void setIncubatingFlag(Boolean incubatingFlag) { this.incubatingFlag = incubatingFlag; }

  public Integer getDaysSinceChange() { return daysSinceChange; }

  public void setDaysSinceChange(Integer daysSinceChange) { this.daysSinceChange = daysSinceChange; }

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

  public Integer getFemaleId1() { return femaleId1; }

  public void setFemaleId1(Integer femaleId1) { this.femaleId1 = femaleId1; }

  public Integer getStartTimeHoursAgo1() { return startTimeHoursAgo1; }

  public void setStartTimeHoursAgo1(Integer startTimeHoursAgo1) {
    this.startTimeHoursAgo1 = startTimeHoursAgo1;
  }

  public Integer getDuration1() { return duration1; }

  public void setDuration1(Integer duration1) { this.duration1 = duration1; }

  public Integer getQuality1() { return quality1; }

  public void setQuality1(Integer quality1) { this.quality1 = quality1; }

  public Integer getFemaleId2() { return femaleId2; }

  public void setFemaleId2(Integer femaleId2) { this.femaleId2 = femaleId2; }

  public Integer getStartTimeHoursAgo2() { return startTimeHoursAgo2; }

  public void setStartTimeHoursAgo2(Integer startTimeHoursAgo2) {
    this.startTimeHoursAgo2 = startTimeHoursAgo2;
  }

  public Integer getDuration2() { return duration2; }

  public void setDuration2(Integer duration2) { this.duration2 = duration2; }

  public Integer getQuality2() { return quality2; }

  public void setQuality2(Integer quality2) { this.quality2 = quality2; }

  public Integer getFemaleId3() { return femaleId3; }

  public void setFemaleId3(Integer femaleId3) { this.femaleId3 = femaleId3; }

  public Integer getStartTimeHoursAgo3() { return startTimeHoursAgo3; }

  public void setStartTimeHoursAgo3(Integer startTimeHoursAgo3) {
    this.startTimeHoursAgo3 = startTimeHoursAgo3;
  }

  public Integer getDuration3() { return duration3; }

  public void setDuration3(Integer duration3) { this.duration3 = duration3; }

  public Integer getQuality3() { return quality3; }

  public void setQuality3(Integer quality3) { this.quality3 = quality3; }

  public Integer getFemaleId4() { return femaleId4; }

  public void setFemaleId4(Integer femaleId4) { this.femaleId4 = femaleId4; }

  public Integer getStartTimeHoursAgo4() { return startTimeHoursAgo4; }

  public void setStartTimeHoursAgo4(Integer startTimeHoursAgo4) {
    this.startTimeHoursAgo4 = startTimeHoursAgo4;
  }

  public Integer getDuration4() { return duration4; }

  public void setDuration4(Integer duration4) { this.duration4 = duration4; }

  public Integer getQuality4() { return quality4; }

  public void setQuality4(Integer quality4) { this.quality4 = quality4; }

  public Integer getWeightBin() { return weightBin; }

  public void setWeightBin(Integer weightBin) { this.weightBin = weightBin; }

  public Integer getBinCount() { return binCount; }

  public void setBinCount(Integer binCount) { this.binCount = binCount; }

  public Integer getMaxQuality() { return maxQuality; }

  public void setMaxQuality(Integer maxQuality) { this.maxQuality = maxQuality; }
}

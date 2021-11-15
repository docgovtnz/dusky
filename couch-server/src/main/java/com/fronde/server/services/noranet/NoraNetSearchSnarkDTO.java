package com.fronde.server.services.noranet;

import java.util.Date;
import java.util.List;

public class NoraNetSearchSnarkDTO {

  private String noraNetId;
  private String island;
  private String stationId;
  private Date activityDate;
  private List<String> weightData;

  public String getNoraNetId() { return noraNetId; }

  public void setNoraNetId(String noraNetId) { this.noraNetId = noraNetId; }

  public String getIsland() { return island; }

  public void setIsland(String island) { this.island = island; }

  public String getStationId() { return stationId; }

  public void setStationId(String stationId) { this.stationId = stationId; }

  public Date getActivityDate() { return activityDate; }

  public void setActivityDate(Date activityDate) { this.activityDate = activityDate; }

  public List<String> getWeightData() { return weightData; }

  public void setWeightData(List<String> weightData) { this.weightData = weightData; }
}

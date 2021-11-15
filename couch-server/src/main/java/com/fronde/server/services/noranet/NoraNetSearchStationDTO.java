package com.fronde.server.services.noranet;

import com.fronde.server.services.noranet.NoraNetSearchDTO.Bird;
import java.util.Date;
import java.util.List;

public class NoraNetSearchStationDTO {
  private String noraNetId;
  private String island;
  private String stationId;
  private Date activityDate;
  private Integer batteryVolts;
  private Integer uhfId;
  private List<Bird> birdList;
  private Integer pulseCount;
  private String detectionType;

  public String getNoraNetId() { return noraNetId; }

  public void setNoraNetId(String noraNetId) { this.noraNetId = noraNetId; }

  public String getIsland() { return island; }

  public void setIsland(String island) { this.island = island; }

  public String getStationId() { return stationId; }

  public void setStationId(String stationId) { this.stationId = stationId; }

  public Date getActivityDate() { return activityDate; }

  public void setActivityDate(Date activityDate) { this.activityDate = activityDate; }

  public Integer getBatteryVolts() { return batteryVolts; }

  public void setBatteryVolts(Integer batteryVolts) { this.batteryVolts = batteryVolts; }

  public Integer getUhfId() { return uhfId; }

  public void setUhfId(Integer uhfId) { this.uhfId = uhfId; }

  public List<Bird> getBirdList() { return birdList; }

  public void setBirdList(List<Bird> birdList) { this.birdList = birdList; }

  public Integer getPulseCount() { return pulseCount; }

  public void setPulseCount(Integer pulseCount) { this.pulseCount = pulseCount; }

  public String getDetectionType() { return detectionType; }

  public void setDetectionType(String detectionType) { this.detectionType = detectionType; }
}

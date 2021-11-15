package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;
import java.util.List;

/**
 * NoraNet "EGG1" data, embedded within a NoraNetEntity
 *
 * @version 1.0
 * @date 14/07/2021
 */

@Document
public class NoraNetEggTimerEntity {

  @Field
  private List<NoraNetBirdEntity> birdList;
  @Field
  private Integer uhfId;
  @Field
  private Float activity;
  @Field
  private Integer batteryLife;
  @Field
  private Boolean incubating;
  @Field
  private Integer daysSinceChange;
  @Field
  private String comments;

  public List<NoraNetBirdEntity> getBirdList() {
    return birdList;
  }

  public void setBirdList(List<NoraNetBirdEntity> birdList) {
    this.birdList = birdList;
  }

  public Integer getUhfId() {
    return uhfId;
  }

  public void setUhfId(Integer uhfId) {
    this.uhfId = uhfId;
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

  public void setBatteryLife(Integer batteryLife) { this.batteryLife = batteryLife; }

  public Boolean getIncubating() { return incubating; }

  public void setIncubating(Boolean incubating) { this.incubating = incubating; }

  public Integer getDaysSinceChange() { return daysSinceChange; }

  public void setDaysSinceChange(Integer daysSinceChange) {
    this.daysSinceChange = daysSinceChange;
  }

  public String getComments() { return comments; }

  public void setComments(String comments) { this.comments = comments; }
}

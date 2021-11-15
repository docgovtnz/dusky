package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;
import java.util.List;

/**
 * NoraNet "CMSHORT1" data, embedded within a NoraNetEntity
 *
 * @version 1.0
 * @date 14/07/2021
 */

@Document
public class NoraNetCmShortEntity {

  @Field
  private List<NoraNetBirdEntity> birdList;
  @Field
  private Integer uhfId;
  @Field
  private Float activity;
  @Field
  private Integer batteryLife;
  @Field
  private Integer matingAge;
  @Field
  private Integer cmHour;
  @Field
  private Integer cmMinute;
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

  public Integer getMatingAge() { return matingAge; }

  public void setMatingAge(Integer matingAge) { this.matingAge = matingAge; }

  public Integer getCmHour() { return cmHour; }

  public void setCmHour(Integer cmHour) { this.cmHour = cmHour; }

  public Integer getCmMinute() { return cmMinute; }

  public void setCmMinute(Integer cmMinute) { this.cmMinute = cmMinute; }

  public String getComments() { return comments; }

  public void setComments(String comments) { this.comments = comments; }
}

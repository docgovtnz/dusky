package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;
import java.util.List;

/**
 * NoraNet "DETECT1" data, embedded within a NoraNetEntity
 *
 * @version 1.0
 * @date 14/07/2021
 */

@Document
public class NoraNetDetectionEntity {

  @Field
  private List<NoraNetBirdEntity> birdList;
  @Field
  private Integer uhfId;
  @Field
  private String category;
  @Field
  private Integer pulseCount;
  @Field
  private Integer peakTwitch;
  @Field
  private Float activity;
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

  public String getComments() { return comments; }

  public void setComments(String comments) { this.comments = comments; }
}

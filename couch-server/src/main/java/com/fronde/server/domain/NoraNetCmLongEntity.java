package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;
import java.util.List;

/**
 * NoraNet "CMLONG1" data, embedded within a NoraNetEntity
 *
 * @version 1.0
 * @date 14/07/2021
 */

@Document
public class NoraNetCmLongEntity {

  @Field
  private List<NoraNetBirdEntity> birdList;
  @Field
  private Integer uhfId;
  @Field
  private Integer matingAge;
  @Field
  private Integer cmHour;
  @Field
  private Integer cmMinute;
  @Field
  private Integer lastCmHour;
  @Field
  private Integer lastCmMinute;
  @Field
  private String comments;
  @Field
  private List<NoraNetCmFemaleEntity> cmFemaleList;

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

  public String getComments() { return comments; }

  public void setComments(String comments) { this.comments = comments; }

  public List<NoraNetCmFemaleEntity> getCmFemaleList() { return cmFemaleList; }

  public void setCmFemaleList(List<NoraNetCmFemaleEntity> cmFemaleList) {
    this.cmFemaleList = cmFemaleList;
  }
}

package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;
import java.util.List;

/**
 * NoraNet "CMLONG1" data of females recorded mating, embedded within a NoraNetCmLongEntity
 *
 * @version 1.0
 * @date 14/07/2021
 */

@Document
public class NoraNetCmFemaleEntity {

  @Field
  private List<NoraNetBirdEntity> birdList;
  @Field
  private Integer uhfId;
  @Field
  private Integer startTimeHoursAgo;
  @Field
  private Integer duration;
  @Field
  private Integer quality;

  public NoraNetCmFemaleEntity(List<NoraNetBirdEntity> birdList, Integer uhfId, Integer startTimeHoursAgo,
      Integer duration, Integer quality) {
    this.birdList = birdList;
    this.uhfId = uhfId;
    this.startTimeHoursAgo = startTimeHoursAgo;
    this.duration = duration;
    this.quality = quality;
  }

  public List<NoraNetBirdEntity> getBirdList() { return birdList; }

  public void setBirdList(List<NoraNetBirdEntity> birdList) { this.birdList = birdList; }

  public Integer getUhfId() { return uhfId; }

  public void setUhfId(Integer uhfId) { this.uhfId = uhfId; }

  public Integer getStartTimeHoursAgo() { return startTimeHoursAgo; }

  public void setStartTimeHoursAgo(Integer startTimeHoursAgo) {
    this.startTimeHoursAgo = startTimeHoursAgo;
  }

  public Integer getDuration() { return duration; }

  public void setDuration(Integer duration) { this.duration = duration; }

  public Integer getQuality() { return quality; }

  public void setQuality(Integer quality) { this.quality = quality; }
}

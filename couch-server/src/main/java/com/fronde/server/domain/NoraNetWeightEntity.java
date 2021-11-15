package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.List;
import org.springframework.data.couchbase.core.mapping.Document;

/**
 * NoraNet "WEIGHT1" data, embedded within a NoraNetEntity
 *
 * @version 1.0
 */

@Document
public class NoraNetWeightEntity {

  @Field
  private Integer weightBin;
  @Field
  private Integer binCount;
  @Field
  private Integer maxQuality;
  @Field
  private String comments;

  public Integer getWeightBin() { return weightBin; }

  public void setWeightBin(Integer weightBin) { this.weightBin = weightBin; }

  public Integer getBinCount() { return binCount; }

  public void setBinCount(Integer binCount) { this.binCount = binCount; }

  public Integer getMaxQuality() { return maxQuality; }

  public void setMaxQuality(Integer maxQuality) { this.maxQuality = maxQuality; }

  public String getComments() { return comments; }

  public void setComments(String comments) { this.comments = comments; }

}

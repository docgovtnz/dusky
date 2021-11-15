package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class BirdFeatureEntity {

  @Field
  private String birdid;
  @Field
  private String bodyPart;
  @Field
  private String description;
  @Field
  private String featureid;

  public String getBirdid() {
    return birdid;
  }

  public void setBirdid(String birdid) {
    this.birdid = birdid;
  }

  public String getBodyPart() {
    return bodyPart;
  }

  public void setBodyPart(String bodyPart) {
    this.bodyPart = bodyPart;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getFeatureid() {
    return featureid;
  }

  public void setFeatureid(String featureid) {
    this.featureid = featureid;
  }

}
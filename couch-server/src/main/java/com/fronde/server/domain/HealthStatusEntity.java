package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class HealthStatusEntity {

  @Field
  private String healthStatus;
  @Field
  private Integer boomingIntensity;

  public String getHealthStatus() {
    return healthStatus;
  }

  public void setHealthStatus(String healthStatus) {
    this.healthStatus = healthStatus;
  }

  public Integer getBoomingIntensity() {
    return boomingIntensity;
  }

  public void setBoomingIntensity(Integer boomingIntensity) {
    this.boomingIntensity = boomingIntensity;
  }

}
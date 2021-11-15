package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class SkyRangerEntity {

  @Field
  private Integer battLifeWeeks;
  @Field
  private String recoveryid;

  public Integer getBattLifeWeeks() {
    return battLifeWeeks;
  }

  public void setBattLifeWeeks(Integer battLifeWeeks) {
    this.battLifeWeeks = battLifeWeeks;
  }

  public String getRecoveryid() {
    return recoveryid;
  }

  public void setRecoveryid(String recoveryid) {
    this.recoveryid = recoveryid;
  }

}
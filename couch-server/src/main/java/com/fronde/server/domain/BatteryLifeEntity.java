package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class BatteryLifeEntity {

  @Field
  private Integer battLifeWeeks;
  @Field
  private String recoveryid;
  @Field
  private Integer batteryLife1;
  @Field
  private Integer batteryLife2;

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

  public Integer getBatteryLife1() {
    return batteryLife1;
  }

  public void setBatteryLife1(Integer batteryLife1) {
    this.batteryLife1 = batteryLife1;
  }

  public Integer getBatteryLife2() {
    return batteryLife2;
  }

  public void setBatteryLife2(Integer batteryLife2) {
    this.batteryLife2 = batteryLife2;
  }

}
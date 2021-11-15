package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class TxMortalityEntity extends BaseEntity {

  @Field
  private Integer activityBpm;
  @Field
  private Integer hoursTilMort;
  @Field
  private Integer mortalityBpm;
  @Field
  private String name;
  @Field
  private Integer normalBpm;
  @Field
  private Integer oldTxMortId;

  public Integer getActivityBpm() {
    return activityBpm;
  }

  public void setActivityBpm(Integer activityBpm) {
    this.activityBpm = activityBpm;
  }

  public Integer getHoursTilMort() {
    return hoursTilMort;
  }

  public void setHoursTilMort(Integer hoursTilMort) {
    this.hoursTilMort = hoursTilMort;
  }

  public Integer getMortalityBpm() {
    return mortalityBpm;
  }

  public void setMortalityBpm(Integer mortalityBpm) {
    this.mortalityBpm = mortalityBpm;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getNormalBpm() {
    return normalBpm;
  }

  public void setNormalBpm(Integer normalBpm) {
    this.normalBpm = normalBpm;
  }

  public Integer getOldTxMortId() {
    return oldTxMortId;
  }

  public void setOldTxMortId(Integer oldTxMortId) {
    this.oldTxMortId = oldTxMortId;
  }

}
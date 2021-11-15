package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class EmbryoMeasurementsEntity {

  @Field
  private Float headLengthInMms;
  @Field
  private Float totalLengthInMms;
  @Field
  private Float lengthInSitu;
  @Field
  private Float yolkSacMembraneDiameterInMms;
  @Field
  private Integer estimatedAgeAtDeathInDays;

  public Float getHeadLengthInMms() {
    return headLengthInMms;
  }

  public void setHeadLengthInMms(Float headLengthInMms) {
    this.headLengthInMms = headLengthInMms;
  }

  public Float getTotalLengthInMms() {
    return totalLengthInMms;
  }

  public void setTotalLengthInMms(Float totalLengthInMms) {
    this.totalLengthInMms = totalLengthInMms;
  }

  public Float getLengthInSitu() {
    return lengthInSitu;
  }

  public void setLengthInSitu(Float lengthInSitu) {
    this.lengthInSitu = lengthInSitu;
  }

  public Float getYolkSacMembraneDiameterInMms() {
    return yolkSacMembraneDiameterInMms;
  }

  public void setYolkSacMembraneDiameterInMms(Float yolkSacMembraneDiameterInMms) {
    this.yolkSacMembraneDiameterInMms = yolkSacMembraneDiameterInMms;
  }

  public Integer getEstimatedAgeAtDeathInDays() {
    return estimatedAgeAtDeathInDays;
  }

  public void setEstimatedAgeAtDeathInDays(Integer estimatedAgeAtDeathInDays) {
    this.estimatedAgeAtDeathInDays = estimatedAgeAtDeathInDays;
  }

}
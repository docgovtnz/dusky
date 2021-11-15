package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class BandsEntity {

  @Field
  private String newBandNumber;
  @Field
  private String leg;

  public String getNewBandNumber() {
    return newBandNumber;
  }

  public void setNewBandNumber(String newBandNumber) {
    this.newBandNumber = newBandNumber;
  }

  public String getLeg() {
    return leg;
  }

  public void setLeg(String leg) {
    this.leg = leg;
  }

}
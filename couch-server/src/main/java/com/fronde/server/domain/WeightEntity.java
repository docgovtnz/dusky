package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class WeightEntity {

  @Field
  private String method;
  @Field
  private Integer n;
  @Field
  private String recoveryID;
  @Field
  private Float sdDev;
  @Field
  private Float weight;

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public Integer getN() {
    return n;
  }

  public void setN(Integer n) {
    this.n = n;
  }

  public String getRecoveryID() {
    return recoveryID;
  }

  public void setRecoveryID(String recoveryID) {
    this.recoveryID = recoveryID;
  }

  public Float getSdDev() {
    return sdDev;
  }

  public void setSdDev(Float sdDev) {
    this.sdDev = sdDev;
  }

  public Float getWeight() {
    return weight;
  }

  public void setWeight(Float weight) {
    this.weight = weight;
  }

}
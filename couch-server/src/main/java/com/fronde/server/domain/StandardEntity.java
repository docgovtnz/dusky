package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class StandardEntity {

  @Field
  private Integer battery1;
  @Field
  private Integer battery2;
  @Field
  private String dataCaptureType;

  public Integer getBattery1() {
    return battery1;
  }

  public void setBattery1(Integer battery1) {
    this.battery1 = battery1;
  }

  public Integer getBattery2() {
    return battery2;
  }

  public void setBattery2(Integer battery2) {
    this.battery2 = battery2;
  }

  public String getDataCaptureType() {
    return dataCaptureType;
  }

  public void setDataCaptureType(String dataCaptureType) {
    this.dataCaptureType = dataCaptureType;
  }

}

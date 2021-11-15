package com.fronde.server.domain;

import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class ChickHealthEntity {

  private String cropStatus;
  private Integer respiratoryRate;

  public String getCropStatus() {
    return cropStatus;
  }

  public void setCropStatus(String cropStatus) {
    this.cropStatus = cropStatus;
  }

  public Integer getRespiratoryRate() {
    return respiratoryRate;
  }

  public void setRespiratoryRate(Integer respiratoryRate) {
    this.respiratoryRate = respiratoryRate;
  }

}
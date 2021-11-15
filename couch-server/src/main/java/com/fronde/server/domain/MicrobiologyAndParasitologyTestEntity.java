package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class MicrobiologyAndParasitologyTestEntity {

  @Field
  private String labName;
  @Field
  private String caseNumber;
  @Field
  private Date dateProcessed;
  @Field
  private String test;
  @Field
  private String result;

  public String getLabName() {
    return labName;
  }

  public void setLabName(String labName) {
    this.labName = labName;
  }

  public String getCaseNumber() {
    return caseNumber;
  }

  public void setCaseNumber(String caseNumber) {
    this.caseNumber = caseNumber;
  }

  public Date getDateProcessed() {
    return dateProcessed;
  }

  public void setDateProcessed(Date dateProcessed) {
    this.dateProcessed = dateProcessed;
  }

  public String getTest() {
    return test;
  }

  public void setTest(String test) {
    this.test = test;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

}
package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class SpermMeasureEntity {

  @Field
  private String labName;
  @Field
  private String caseNumber;
  @Field
  private Date dateProcessed;
  @Field
  private String spermMeasure;
  @Field
  private String result;
  @Field
  private String spermHeaderID;
  @Field
  private String spermMeasureID;

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

  public String getSpermMeasure() {
    return spermMeasure;
  }

  public void setSpermMeasure(String spermMeasure) {
    this.spermMeasure = spermMeasure;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String getSpermHeaderID() {
    return spermHeaderID;
  }

  public void setSpermHeaderID(String spermHeaderID) {
    this.spermHeaderID = spermHeaderID;
  }

  public String getSpermMeasureID() {
    return spermMeasureID;
  }

  public void setSpermMeasureID(String spermMeasureID) {
    this.spermMeasureID = spermMeasureID;
  }

}
package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class ChemistryAssayEntity {

  @Field
  private String labName;
  @Field
  private String caseNumber;
  @Field
  private Date dateProcessed;
  @Field
  private String chemistryAssay;
  @Field
  private String result;
  @Field
  private Boolean statsExclude;
  @Field
  private Date timeOfCentrifuge;
  @Field
  private String chemistryMethod;
  @Field
  private String plasmaOrSerum;
  @Field
  private String oldStorageMedium;
  @Field
  private String oldContainer;

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

  public String getChemistryAssay() {
    return chemistryAssay;
  }

  public void setChemistryAssay(String chemistryAssay) {
    this.chemistryAssay = chemistryAssay;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public Boolean getStatsExclude() {
    return statsExclude;
  }

  public void setStatsExclude(Boolean statsExclude) {
    this.statsExclude = statsExclude;
  }

  public Date getTimeOfCentrifuge() {
    return timeOfCentrifuge;
  }

  public void setTimeOfCentrifuge(Date timeOfCentrifuge) {
    this.timeOfCentrifuge = timeOfCentrifuge;
  }

  public String getChemistryMethod() {
    return chemistryMethod;
  }

  public void setChemistryMethod(String chemistryMethod) {
    this.chemistryMethod = chemistryMethod;
  }

  public String getPlasmaOrSerum() {
    return plasmaOrSerum;
  }

  public void setPlasmaOrSerum(String plasmaOrSerum) {
    this.plasmaOrSerum = plasmaOrSerum;
  }

  public String getOldStorageMedium() {
    return oldStorageMedium;
  }

  public void setOldStorageMedium(String oldStorageMedium) {
    this.oldStorageMedium = oldStorageMedium;
  }

  public String getOldContainer() {
    return oldContainer;
  }

  public void setOldContainer(String oldContainer) {
    this.oldContainer = oldContainer;
  }

}
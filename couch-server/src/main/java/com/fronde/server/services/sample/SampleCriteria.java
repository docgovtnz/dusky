package com.fronde.server.services.sample;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fronde.server.domain.criteria.AbstractCriteria;
import java.util.Date;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public class SampleCriteria extends AbstractCriteria {

  private List<String> birdIDs;
  private String ageClass;
  private String sex;
  private String collectionIsland;
  private String collectionLocationID;
  private String sampleTakenBy;
  private Boolean showArchived;
  private Boolean showNotArchived;
  private String sampleName;
  private String sampleCategory;
  private String container;
  private String sampleType;
  private Boolean showResultsEntered;
  private Boolean showResultsNotEntered;
  // assume dates in all criteria objects are dates and not date times
  @DateTimeFormat(iso = ISO.DATE)
  private Date fromDate;
  // assume dates in all criteria objects are dates and not date times
  @DateTimeFormat(iso = ISO.DATE)
  private Date toDate;

  public List<String> getBirdIDs() {
    return birdIDs;
  }

  public void setBirdIDs(List<String> birdIDs) {
    this.birdIDs = birdIDs;
  }

  public String getAgeClass() {
    return ageClass;
  }

  public void setAgeClass(String ageClass) {
    this.ageClass = ageClass;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public String getCollectionIsland() {
    return collectionIsland;
  }

  public void setCollectionIsland(String collectionIsland) {
    this.collectionIsland = collectionIsland;
  }

  public String getCollectionLocationID() {
    return collectionLocationID;
  }

  public void setCollectionLocationID(String collectionLocationID) {
    this.collectionLocationID = collectionLocationID;
  }

  public String getSampleTakenBy() {
    return sampleTakenBy;
  }

  public void setSampleTakenBy(String sampleTakenBy) {
    this.sampleTakenBy = sampleTakenBy;
  }

  public Boolean getShowArchived() {
    return showArchived;
  }

  public void setShowArchived(Boolean showArchived) {
    this.showArchived = showArchived;
  }

  public Boolean getShowNotArchived() {
    return showNotArchived;
  }

  public void setShowNotArchived(Boolean showNotArchived) {
    this.showNotArchived = showNotArchived;
  }

  public String getSampleName() {
    return sampleName;
  }

  public void setSampleName(String sampleName) {
    this.sampleName = sampleName;
  }

  public String getSampleCategory() {
    return sampleCategory;
  }

  public void setSampleCategory(String sampleCategory) {
    this.sampleCategory = sampleCategory;
  }

  public String getContainer() {
    return container;
  }

  public void setContainer(String container) {
    this.container = container;
  }

  public String getSampleType() {
    return sampleType;
  }

  public void setSampleType(String sampleType) {
    this.sampleType = sampleType;
  }

  public Boolean getShowResultsEntered() {
    return showResultsEntered;
  }

  public void setShowResultsEntered(Boolean showResultsEntered) {
    this.showResultsEntered = showResultsEntered;
  }

  public Boolean getShowResultsNotEntered() {
    return showResultsNotEntered;
  }

  public void setShowResultsNotEntered(Boolean showResultsNotEntered) {
    this.showResultsNotEntered = showResultsNotEntered;
  }

  public Date getFromDate() {
    return fromDate;
  }

  public void setFromDate(Date fromDate) {
    this.fromDate = fromDate;
  }

  public Date getToDate() {
    return toDate;
  }

  public void setToDate(Date toDate) {
    this.toDate = toDate;
  }

}

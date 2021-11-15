package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class SwabSampleEntity {

  @Field
  private String sampleID;
  @Field
  private String sampleName;
  @Field
  private String swabSite;
  @Field
  private String storageMedium;
  @Field
  private String container;
  @Field
  private String storageConditions;
  @Field
  private Integer quantity;
  @Field
  private String sampleTakenBy;
  @Field
  private Integer headerUniqueTestSwabSiteCount;
  @Field
  private String microbiologyHeaderID;
  @Field
  private String oldCaseNumber;
  @Field
  private String oldComments;
  @Field
  private Date oldDateProcessed;
  @Field
  private String oldHeaderSwabSite;
  @Field
  private String oldTestSwabSite;
  @Field
  private String reasonForSample;

  public String getSampleID() {
    return sampleID;
  }

  public void setSampleID(String sampleID) {
    this.sampleID = sampleID;
  }

  public String getSampleName() {
    return sampleName;
  }

  public void setSampleName(String sampleName) {
    this.sampleName = sampleName;
  }

  public String getSwabSite() {
    return swabSite;
  }

  public void setSwabSite(String swabSite) {
    this.swabSite = swabSite;
  }

  public String getStorageMedium() {
    return storageMedium;
  }

  public void setStorageMedium(String storageMedium) {
    this.storageMedium = storageMedium;
  }

  public String getContainer() {
    return container;
  }

  public void setContainer(String container) {
    this.container = container;
  }

  public String getStorageConditions() {
    return storageConditions;
  }

  public void setStorageConditions(String storageConditions) {
    this.storageConditions = storageConditions;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public String getSampleTakenBy() {
    return sampleTakenBy;
  }

  public void setSampleTakenBy(String sampleTakenBy) {
    this.sampleTakenBy = sampleTakenBy;
  }

  public Integer getHeaderUniqueTestSwabSiteCount() {
    return headerUniqueTestSwabSiteCount;
  }

  public void setHeaderUniqueTestSwabSiteCount(Integer headerUniqueTestSwabSiteCount) {
    this.headerUniqueTestSwabSiteCount = headerUniqueTestSwabSiteCount;
  }

  public String getMicrobiologyHeaderID() {
    return microbiologyHeaderID;
  }

  public void setMicrobiologyHeaderID(String microbiologyHeaderID) {
    this.microbiologyHeaderID = microbiologyHeaderID;
  }

  public String getOldCaseNumber() {
    return oldCaseNumber;
  }

  public void setOldCaseNumber(String oldCaseNumber) {
    this.oldCaseNumber = oldCaseNumber;
  }

  public String getOldComments() {
    return oldComments;
  }

  public void setOldComments(String oldComments) {
    this.oldComments = oldComments;
  }

  public Date getOldDateProcessed() {
    return oldDateProcessed;
  }

  public void setOldDateProcessed(Date oldDateProcessed) {
    this.oldDateProcessed = oldDateProcessed;
  }

  public String getOldHeaderSwabSite() {
    return oldHeaderSwabSite;
  }

  public void setOldHeaderSwabSite(String oldHeaderSwabSite) {
    this.oldHeaderSwabSite = oldHeaderSwabSite;
  }

  public String getOldTestSwabSite() {
    return oldTestSwabSite;
  }

  public void setOldTestSwabSite(String oldTestSwabSite) {
    this.oldTestSwabSite = oldTestSwabSite;
  }

  public String getReasonForSample() {
    return reasonForSample;
  }

  public void setReasonForSample(String reasonForSample) {
    this.reasonForSample = reasonForSample;
  }
}
package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class OtherSampleEntity {

  @Field
  private String sampleID;
  @Field
  private String sampleName;
  @Field
  private String type;
  @Field
  private String storageMedium;
  @Field
  private String container;
  @Field
  private String storageConditions;
  @Field
  private Float amount;
  @Field
  private String units;
  @Field
  private String sampleTakenBy;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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

  public Float getAmount() {
    return amount;
  }

  public void setAmount(Float amount) {
    this.amount = amount;
  }

  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  public String getSampleTakenBy() {
    return sampleTakenBy;
  }

  public void setSampleTakenBy(String sampleTakenBy) {
    this.sampleTakenBy = sampleTakenBy;
  }

  public String getReasonForSample() {
    return reasonForSample;
  }

  public void setReasonForSample(String reasonForSample) {
    this.reasonForSample = reasonForSample;
  }
}
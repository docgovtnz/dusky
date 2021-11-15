package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class SpermSampleEntity {

  private String sampleID;
  private String sampleName;
  @Field
  private String diluent;
  @Field
  private String container;
  @Field
  private String storageConditions;
  private Float volumeInMicroL;
  @Field
  private String collectionMethod;
  @Field
  private String papillaSwelling;
  @Field
  private String stimulation;
  @Field
  private String stress;
  @Field
  private String sampleTakenBy;
  @Field
  private String spermHeaderID;
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

  public String getDiluent() {
    return diluent;
  }

  public void setDiluent(String diluent) {
    this.diluent = diluent;
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

  public Float getVolumeInMicroL() {
    return volumeInMicroL;
  }

  public void setVolumeInMicroL(Float volumeInMicroL) {
    this.volumeInMicroL = volumeInMicroL;
  }

  public String getCollectionMethod() {
    return collectionMethod;
  }

  public void setCollectionMethod(String collectionMethod) {
    this.collectionMethod = collectionMethod;
  }

  public String getPapillaSwelling() {
    return papillaSwelling;
  }

  public void setPapillaSwelling(String papillaSwelling) {
    this.papillaSwelling = papillaSwelling;
  }

  public String getStimulation() {
    return stimulation;
  }

  public void setStimulation(String stimulation) {
    this.stimulation = stimulation;
  }

  public String getStress() {
    return stress;
  }

  public void setStress(String stress) {
    this.stress = stress;
  }

  public String getSampleTakenBy() {
    return sampleTakenBy;
  }

  public void setSampleTakenBy(String sampleTakenBy) {
    this.sampleTakenBy = sampleTakenBy;
  }

  public String getSpermHeaderID() {
    return spermHeaderID;
  }

  public void setSpermHeaderID(String spermHeaderID) {
    this.spermHeaderID = spermHeaderID;
  }

  public String getReasonForSample() {
    return reasonForSample;
  }

  public void setReasonForSample(String reasonForSample) {
    this.reasonForSample = reasonForSample;
  }
}
package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import java.util.List;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class SampleEntity extends BaseEntity {

  @Field
  private Boolean archived;
  @Field
  private String birdID;
  @Field
  private String bloodChemistryComments;
  @Field
  private Date collectionDate;
  @Field
  private String collectionIsland;
  @Field
  private String collectionLocationID;
  @Field
  private String comments;
  @Field
  private String container;
  @Field
  private String currentIsland;
  @Field
  private String haematologyComments;
  @Field
  private Boolean haemolysed;
  @Field
  private String microbiologyAndParasitologyComments;
  @Field
  private String sampleCategory;
  @Field
  private String sampleName;
  @Field
  private String sampleTakenBy;
  @Field
  private String sampleType;
  @Field
  private Boolean smudgeCells;
  @Field
  private String spermComments;
  @Field
  private String storageConditions;
  @Field
  private String storageMedium;
  @Field
  private String reasonForSample;
  @Field
  private BloodDetailEntity bloodDetail;
  @Field
  private List<ChemistryAssayEntity> chemistryAssayList;
  @Field
  private List<HaematologyTestEntity> haematologyTestList;
  @Field
  private List<MicrobiologyAndParasitologyTestEntity> microbiologyAndParasitologyTestList;
  @Field
  private OtherDetailEntity otherDetail;
  @Field
  private SpermDetailEntity spermDetail;
  @Field
  private List<SpermMeasureEntity> spermMeasureList;
  @Field
  private SwabDetailEntity swabDetail;

  public Boolean getArchived() {
    return archived;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public String getBloodChemistryComments() {
    return bloodChemistryComments;
  }

  public void setBloodChemistryComments(String bloodChemistryComments) {
    this.bloodChemistryComments = bloodChemistryComments;
  }

  public Date getCollectionDate() {
    return collectionDate;
  }

  public void setCollectionDate(Date collectionDate) {
    this.collectionDate = collectionDate;
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

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getContainer() {
    return container;
  }

  public void setContainer(String container) {
    this.container = container;
  }

  public String getCurrentIsland() {
    return currentIsland;
  }

  public void setCurrentIsland(String currentIsland) {
    this.currentIsland = currentIsland;
  }

  public String getHaematologyComments() {
    return haematologyComments;
  }

  public void setHaematologyComments(String haematologyComments) {
    this.haematologyComments = haematologyComments;
  }

  public Boolean getHaemolysed() {
    return haemolysed;
  }

  public void setHaemolysed(Boolean haemolysed) {
    this.haemolysed = haemolysed;
  }

  public String getMicrobiologyAndParasitologyComments() {
    return microbiologyAndParasitologyComments;
  }

  public void setMicrobiologyAndParasitologyComments(String microbiologyAndParasitologyComments) {
    this.microbiologyAndParasitologyComments = microbiologyAndParasitologyComments;
  }

  public String getSampleCategory() {
    return sampleCategory;
  }

  public void setSampleCategory(String sampleCategory) {
    this.sampleCategory = sampleCategory;
  }

  public String getSampleName() {
    return sampleName;
  }

  public void setSampleName(String sampleName) {
    this.sampleName = sampleName;
  }

  public String getSampleTakenBy() {
    return sampleTakenBy;
  }

  public void setSampleTakenBy(String sampleTakenBy) {
    this.sampleTakenBy = sampleTakenBy;
  }

  public String getSampleType() {
    return sampleType;
  }

  public void setSampleType(String sampleType) {
    this.sampleType = sampleType;
  }

  public Boolean getSmudgeCells() {
    return smudgeCells;
  }

  public void setSmudgeCells(Boolean smudgeCells) {
    this.smudgeCells = smudgeCells;
  }

  public String getSpermComments() {
    return spermComments;
  }

  public void setSpermComments(String spermComments) {
    this.spermComments = spermComments;
  }

  public String getStorageConditions() {
    return storageConditions;
  }

  public void setStorageConditions(String storageConditions) {
    this.storageConditions = storageConditions;
  }

  public String getStorageMedium() {
    return storageMedium;
  }

  public void setStorageMedium(String storageMedium) {
    this.storageMedium = storageMedium;
  }

  public String getReasonForSample() {
    return reasonForSample;
  }

  public void setReasonForSample(String reasonForSample) {
    this.reasonForSample = reasonForSample;
  }

  public BloodDetailEntity getBloodDetail() {
    return bloodDetail;
  }

  public void setBloodDetail(BloodDetailEntity bloodDetail) {
    this.bloodDetail = bloodDetail;
  }

  public List<ChemistryAssayEntity> getChemistryAssayList() {
    return chemistryAssayList;
  }

  public void setChemistryAssayList(List<ChemistryAssayEntity> chemistryAssayList) {
    this.chemistryAssayList = chemistryAssayList;
  }

  public List<HaematologyTestEntity> getHaematologyTestList() {
    return haematologyTestList;
  }

  public void setHaematologyTestList(List<HaematologyTestEntity> haematologyTestList) {
    this.haematologyTestList = haematologyTestList;
  }

  public List<MicrobiologyAndParasitologyTestEntity> getMicrobiologyAndParasitologyTestList() {
    return microbiologyAndParasitologyTestList;
  }

  public void setMicrobiologyAndParasitologyTestList(
      List<MicrobiologyAndParasitologyTestEntity> microbiologyAndParasitologyTestList) {
    this.microbiologyAndParasitologyTestList = microbiologyAndParasitologyTestList;
  }

  public OtherDetailEntity getOtherDetail() {
    return otherDetail;
  }

  public void setOtherDetail(OtherDetailEntity otherDetail) {
    this.otherDetail = otherDetail;
  }

  public SpermDetailEntity getSpermDetail() {
    return spermDetail;
  }

  public void setSpermDetail(SpermDetailEntity spermDetail) {
    this.spermDetail = spermDetail;
  }

  public List<SpermMeasureEntity> getSpermMeasureList() {
    return spermMeasureList;
  }

  public void setSpermMeasureList(List<SpermMeasureEntity> spermMeasureList) {
    this.spermMeasureList = spermMeasureList;
  }

  public SwabDetailEntity getSwabDetail() {
    return swabDetail;
  }

  public void setSwabDetail(SwabDetailEntity swabDetail) {
    this.swabDetail = swabDetail;
  }

}
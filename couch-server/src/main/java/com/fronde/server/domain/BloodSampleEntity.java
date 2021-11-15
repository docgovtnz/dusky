package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class BloodSampleEntity {

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
  private Float volumeInMl;
  @Field
  private String sampleTakenBy;
  @Field
  private String chemistryHeaderID;
  @Field
  private String oldChemistryComments;
  @Field
  private String chemistryMethod;
  @Field
  private String plasmaOrSerum;
  @Field
  private Date timeOfCentrifuge;
  @Field
  private String bloodRecordID;
  @Field
  private String timeAtCollectionOrCentrifugingOfReplicates;
  @Field
  private Date dateProcessedInLab;
  @Field
  private String caseNumber;
  @Field
  private String haematology;
  @Field
  private String sampleStatus;
  @Field
  private Float oldHct;
  @Field
  private Float oldHB;
  @Field
  private Float oldWBC;
  @Field
  private Float oldBandHeterophils;
  @Field
  private Float oldHeterophils;
  @Field
  private Float oldLymphocytes;
  @Field
  private Float oldMonocytes;
  @Field
  private Float oldEosinophils;
  @Field
  private Float oldBasophils;
  @Field
  private Float oldMyelocytes;
  @Field
  private Float oldFibrinogen;
  @Field
  private String oldRedCellMorphology;
  @Field
  private String oldMetarubricytes;
  @Field
  private String oldPolychromasia;
  @Field
  private String oldAnisocytosis;
  @Field
  private String oldHypochromasia;
  @Field
  private String oldMacrocytosis;
  @Field
  private String oldMicrocytosis;
  @Field
  private String oldPoikilocytosis;
  @Field
  private String oldStippledCells;
  @Field
  private String oldThrombocyteMorphology;
  @Field
  private String oldBloodParasites;
  @Field
  private String oldWhiteCellMorphology;
  @Field
  private String oldToxicChanges;
  @Field
  private String oldReactiveLymphocytes;
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

  public Float getVolumeInMl() {
    return volumeInMl;
  }

  public void setVolumeInMl(Float volumeInMl) {
    this.volumeInMl = volumeInMl;
  }

  public String getSampleTakenBy() {
    return sampleTakenBy;
  }

  public void setSampleTakenBy(String sampleTakenBy) {
    this.sampleTakenBy = sampleTakenBy;
  }

  public String getChemistryHeaderID() {
    return chemistryHeaderID;
  }

  public void setChemistryHeaderID(String chemistryHeaderID) {
    this.chemistryHeaderID = chemistryHeaderID;
  }

  public String getOldChemistryComments() {
    return oldChemistryComments;
  }

  public void setOldChemistryComments(String oldChemistryComments) {
    this.oldChemistryComments = oldChemistryComments;
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

  public Date getTimeOfCentrifuge() {
    return timeOfCentrifuge;
  }

  public void setTimeOfCentrifuge(Date timeOfCentrifuge) {
    this.timeOfCentrifuge = timeOfCentrifuge;
  }

  public String getBloodRecordID() {
    return bloodRecordID;
  }

  public void setBloodRecordID(String bloodRecordID) {
    this.bloodRecordID = bloodRecordID;
  }

  public String getTimeAtCollectionOrCentrifugingOfReplicates() {
    return timeAtCollectionOrCentrifugingOfReplicates;
  }

  public void setTimeAtCollectionOrCentrifugingOfReplicates(
      String timeAtCollectionOrCentrifugingOfReplicates) {
    this.timeAtCollectionOrCentrifugingOfReplicates = timeAtCollectionOrCentrifugingOfReplicates;
  }

  public Date getDateProcessedInLab() {
    return dateProcessedInLab;
  }

  public void setDateProcessedInLab(Date dateProcessedInLab) {
    this.dateProcessedInLab = dateProcessedInLab;
  }

  public String getCaseNumber() {
    return caseNumber;
  }

  public void setCaseNumber(String caseNumber) {
    this.caseNumber = caseNumber;
  }

  public String getHaematology() {
    return haematology;
  }

  public void setHaematology(String haematology) {
    this.haematology = haematology;
  }

  public String getSampleStatus() {
    return sampleStatus;
  }

  public void setSampleStatus(String sampleStatus) {
    this.sampleStatus = sampleStatus;
  }

  public Float getOldHct() {
    return oldHct;
  }

  public void setOldHct(Float oldHct) {
    this.oldHct = oldHct;
  }

  public Float getOldHB() {
    return oldHB;
  }

  public void setOldHB(Float oldHB) {
    this.oldHB = oldHB;
  }

  public Float getOldWBC() {
    return oldWBC;
  }

  public void setOldWBC(Float oldWBC) {
    this.oldWBC = oldWBC;
  }

  public Float getOldBandHeterophils() {
    return oldBandHeterophils;
  }

  public void setOldBandHeterophils(Float oldBandHeterophils) {
    this.oldBandHeterophils = oldBandHeterophils;
  }

  public Float getOldHeterophils() {
    return oldHeterophils;
  }

  public void setOldHeterophils(Float oldHeterophils) {
    this.oldHeterophils = oldHeterophils;
  }

  public Float getOldLymphocytes() {
    return oldLymphocytes;
  }

  public void setOldLymphocytes(Float oldLymphocytes) {
    this.oldLymphocytes = oldLymphocytes;
  }

  public Float getOldMonocytes() {
    return oldMonocytes;
  }

  public void setOldMonocytes(Float oldMonocytes) {
    this.oldMonocytes = oldMonocytes;
  }

  public Float getOldEosinophils() {
    return oldEosinophils;
  }

  public void setOldEosinophils(Float oldEosinophils) {
    this.oldEosinophils = oldEosinophils;
  }

  public Float getOldBasophils() {
    return oldBasophils;
  }

  public void setOldBasophils(Float oldBasophils) {
    this.oldBasophils = oldBasophils;
  }

  public Float getOldMyelocytes() {
    return oldMyelocytes;
  }

  public void setOldMyelocytes(Float oldMyelocytes) {
    this.oldMyelocytes = oldMyelocytes;
  }

  public Float getOldFibrinogen() {
    return oldFibrinogen;
  }

  public void setOldFibrinogen(Float oldFibrinogen) {
    this.oldFibrinogen = oldFibrinogen;
  }

  public String getOldRedCellMorphology() {
    return oldRedCellMorphology;
  }

  public void setOldRedCellMorphology(String oldRedCellMorphology) {
    this.oldRedCellMorphology = oldRedCellMorphology;
  }

  public String getOldMetarubricytes() {
    return oldMetarubricytes;
  }

  public void setOldMetarubricytes(String oldMetarubricytes) {
    this.oldMetarubricytes = oldMetarubricytes;
  }

  public String getOldPolychromasia() {
    return oldPolychromasia;
  }

  public void setOldPolychromasia(String oldPolychromasia) {
    this.oldPolychromasia = oldPolychromasia;
  }

  public String getOldAnisocytosis() {
    return oldAnisocytosis;
  }

  public void setOldAnisocytosis(String oldAnisocytosis) {
    this.oldAnisocytosis = oldAnisocytosis;
  }

  public String getOldHypochromasia() {
    return oldHypochromasia;
  }

  public void setOldHypochromasia(String oldHypochromasia) {
    this.oldHypochromasia = oldHypochromasia;
  }

  public String getOldMacrocytosis() {
    return oldMacrocytosis;
  }

  public void setOldMacrocytosis(String oldMacrocytosis) {
    this.oldMacrocytosis = oldMacrocytosis;
  }

  public String getOldMicrocytosis() {
    return oldMicrocytosis;
  }

  public void setOldMicrocytosis(String oldMicrocytosis) {
    this.oldMicrocytosis = oldMicrocytosis;
  }

  public String getOldPoikilocytosis() {
    return oldPoikilocytosis;
  }

  public void setOldPoikilocytosis(String oldPoikilocytosis) {
    this.oldPoikilocytosis = oldPoikilocytosis;
  }

  public String getOldStippledCells() {
    return oldStippledCells;
  }

  public void setOldStippledCells(String oldStippledCells) {
    this.oldStippledCells = oldStippledCells;
  }

  public String getOldThrombocyteMorphology() {
    return oldThrombocyteMorphology;
  }

  public void setOldThrombocyteMorphology(String oldThrombocyteMorphology) {
    this.oldThrombocyteMorphology = oldThrombocyteMorphology;
  }

  public String getOldBloodParasites() {
    return oldBloodParasites;
  }

  public void setOldBloodParasites(String oldBloodParasites) {
    this.oldBloodParasites = oldBloodParasites;
  }

  public String getOldWhiteCellMorphology() {
    return oldWhiteCellMorphology;
  }

  public void setOldWhiteCellMorphology(String oldWhiteCellMorphology) {
    this.oldWhiteCellMorphology = oldWhiteCellMorphology;
  }

  public String getOldToxicChanges() {
    return oldToxicChanges;
  }

  public void setOldToxicChanges(String oldToxicChanges) {
    this.oldToxicChanges = oldToxicChanges;
  }

  public String getOldReactiveLymphocytes() {
    return oldReactiveLymphocytes;
  }

  public void setOldReactiveLymphocytes(String oldReactiveLymphocytes) {
    this.oldReactiveLymphocytes = oldReactiveLymphocytes;
  }

  public String getReasonForSample() {
    return reasonForSample;
  }

  public void setReasonForSample(String reasonForSample) {
    this.reasonForSample = reasonForSample;
  }
}
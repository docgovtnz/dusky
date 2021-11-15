package com.fronde.server.services.sample;

import java.util.Date;

public class SampleSearchDTO {

  private String sampleID;
  private String sampleName;
  private String birdID;
  private String birdName;
  private String sampleCategory;
  private String sampleType;
  private String container;
  private String sampleTakenByID;
  private String sampleTakenByName;
  private Date collectionDate;
  private String collectionIsland;
  private String collectionLocationID;
  private String collectionLocationName;
  private boolean hasHaematologyTests;
  private boolean hasChemistryAssays;
  private boolean hasMicrobiologyAndParasitologyTests;
  private boolean hasSpermMeasures;

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

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public String getBirdName() {
    return birdName;
  }

  public void setBirdName(String birdName) {
    this.birdName = birdName;
  }

  public String getSampleCategory() {
    return sampleCategory;
  }

  public void setSampleCategory(String sampleCategory) {
    this.sampleCategory = sampleCategory;
  }

  public String getSampleType() {
    return sampleType;
  }

  public void setSampleType(String sampleType) {
    this.sampleType = sampleType;
  }

  public String getContainer() {
    return container;
  }

  public void setContainer(String container) {
    this.container = container;
  }

  public String getSampleTakenByID() {
    return sampleTakenByID;
  }

  public void setSampleTakenByID(String sampleTakenByID) {
    this.sampleTakenByID = sampleTakenByID;
  }

  public String getSampleTakenByName() {
    return sampleTakenByName;
  }

  public void setSampleTakenByName(String sampleTakenByName) {
    this.sampleTakenByName = sampleTakenByName;
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

  public String getCollectionLocationName() {
    return collectionLocationName;
  }

  public void setCollectionLocationName(String collectionLocationName) {
    this.collectionLocationName = collectionLocationName;
  }

  public boolean isHasHaematologyTests() {
    return hasHaematologyTests;
  }

  public void setHasHaematologyTests(boolean hasHaematologyTests) {
    this.hasHaematologyTests = hasHaematologyTests;
  }

  public boolean isHasChemistryAssays() {
    return hasChemistryAssays;
  }

  public void setHasChemistryAssays(boolean hasChemistryAssays) {
    this.hasChemistryAssays = hasChemistryAssays;
  }

  public boolean isHasMicrobiologyAndParasitologyTests() {
    return hasMicrobiologyAndParasitologyTests;
  }

  public void setHasMicrobiologyAndParasitologyTests(boolean hasMicrobiologyAndParasitologyTests) {
    this.hasMicrobiologyAndParasitologyTests = hasMicrobiologyAndParasitologyTests;
  }

  public boolean isHasSpermMeasures() {
    return hasSpermMeasures;
  }

  public void setHasSpermMeasures(boolean hasSpermMeasures) {
    this.hasSpermMeasures = hasSpermMeasures;
  }

}

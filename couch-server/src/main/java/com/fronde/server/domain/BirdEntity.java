package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import java.util.List;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class BirdEntity extends BaseEntity {

  @Field
  private Float actualFreshWeight;
  @Field
  private String ageClassOverride;
  @Field
  private Boolean alive;
  @Field
  private String birdName;
  @Field
  private String clutch;
  @Field
  private Integer clutchOrder;
  @Field
  private String comments;
  @Field
  private String currentBandId;
  @Field
  private String currentBandLeg;
  @Field
  private String currentChipId;
  @Field
  private String currentIsland;
  @Field
  private String currentLocationID;
  @Field
  private String currentTxRecordId;
  @Field
  private Date dateFirstFound;
  @Field
  private Date dateFledged;
  @Field
  private Date dateHatched;
  @Field
  private Date dateIndependent;
  @Field
  private Date dateLaid;
  @Field
  private String datesMated;
  @Field
  private Date dateWeaned;
  @Field
  private String daysOnNest;
  @Field
  private String deadEmbryo;
  @Field
  private Boolean definiteFather;
  @Field
  private Date demise;
  @Field
  private Date discoveryDate;
  @Field
  private String eggName;
  @Field
  private Float estAgeWhen1stFound;
  @Field
  private String father;
  @Field
  private String firstDayAtOrVeryCloseToNest;
  @Field
  private Boolean fledged;
  @Field
  private String gan;
  @Field
  private String houseID;
  @Field
  private Float incubationPeriod;
  @Field
  private Float interClutchLayingInterval;
  @Field
  private String lastDateMated;
  @Field
  private String lastTxChangeId;
  @Field
  private Boolean layDateIsEstimate;
  @Field
  private String layIsland;
  @Field
  private Integer layYear;
  @Field
  private String layLocationID;
  @Field
  private String legColour;
  @Field
  private String matedWith;
  @Field
  private Float matingToLaying;
  @Field
  private Float matingToNestingDays;
  @Field
  private String modifiedByRecordId;
  @Field
  private String mother;
  @Field
  private Float nestingToLaying;
  @Field
  private String nestMother;
  @Field
  private String oldBirdID;
  @Field
  private String plumageColour;
  @Field
  private String results;
  @Field
  private String sex;
  @Field
  private Integer studbookno;
  @Field
  private String transmitterGroup;
  @Field
  private String viable;
  @Field
  private Float historicDataFirstEggWeight;
  @Field
  private List<BirdFeatureEntity> birdFeatureList;
  @Field
  private EggMeasurementsEntity eggMeasurements;
  @Field
  private EmbryoMeasurementsEntity embryoMeasurements;

  @Field
  private Boolean fertilityConfirmed;

  public Float getActualFreshWeight() {
    return actualFreshWeight;
  }

  public void setActualFreshWeight(Float actualFreshWeight) {
    this.actualFreshWeight = actualFreshWeight;
  }

  public String getAgeClassOverride() {
    return ageClassOverride;
  }

  public void setAgeClassOverride(String ageClassOverride) {
    this.ageClassOverride = ageClassOverride;
  }

  public Boolean getAlive() {
    return alive;
  }

  public void setAlive(Boolean alive) {
    this.alive = alive;
  }

  public String getBirdName() {
    return birdName;
  }

  public void setBirdName(String birdName) {
    this.birdName = birdName;
  }

  public String getClutch() {
    return clutch;
  }

  public void setClutch(String clutch) {
    this.clutch = clutch;
  }

  public Integer getClutchOrder() {
    return clutchOrder;
  }

  public void setClutchOrder(Integer clutchOrder) {
    this.clutchOrder = clutchOrder;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getCurrentBandId() {
    return currentBandId;
  }

  public void setCurrentBandId(String currentBandId) {
    this.currentBandId = currentBandId;
  }

  public String getCurrentBandLeg() {
    return currentBandLeg;
  }

  public void setCurrentBandLeg(String currentBandLeg) {
    this.currentBandLeg = currentBandLeg;
  }

  public String getCurrentChipId() {
    return currentChipId;
  }

  public void setCurrentChipId(String currentChipId) {
    this.currentChipId = currentChipId;
  }

  public String getCurrentIsland() {
    return currentIsland;
  }

  public void setCurrentIsland(String currentIsland) {
    this.currentIsland = currentIsland;
  }

  public String getCurrentLocationID() {
    return currentLocationID;
  }

  public void setCurrentLocationID(String currentLocationID) {
    this.currentLocationID = currentLocationID;
  }

  public String getCurrentTxRecordId() {
    return currentTxRecordId;
  }

  public void setCurrentTxRecordId(String currentTxRecordId) {
    this.currentTxRecordId = currentTxRecordId;
  }

  public Date getDateFirstFound() {
    return dateFirstFound;
  }

  public void setDateFirstFound(Date dateFirstFound) {
    this.dateFirstFound = dateFirstFound;
  }

  public Date getDateFledged() {
    return dateFledged;
  }

  public void setDateFledged(Date dateFledged) {
    this.dateFledged = dateFledged;
  }

  public Date getDateHatched() {
    return dateHatched;
  }

  public void setDateHatched(Date dateHatched) {
    this.dateHatched = dateHatched;
  }

  public Date getDateIndependent() {
    return dateIndependent;
  }

  public void setDateIndependent(Date dateIndependent) {
    this.dateIndependent = dateIndependent;
  }

  public Date getDateLaid() {
    return dateLaid;
  }

  public void setDateLaid(Date dateLaid) {
    this.dateLaid = dateLaid;
  }

  public String getDatesMated() {
    return datesMated;
  }

  public void setDatesMated(String datesMated) {
    this.datesMated = datesMated;
  }

  public Date getDateWeaned() {
    return dateWeaned;
  }

  public void setDateWeaned(Date dateWeaned) {
    this.dateWeaned = dateWeaned;
  }

  public String getDaysOnNest() {
    return daysOnNest;
  }

  public void setDaysOnNest(String daysOnNest) {
    this.daysOnNest = daysOnNest;
  }

  public String getDeadEmbryo() {
    return deadEmbryo;
  }

  public void setDeadEmbryo(String deadEmbryo) {
    this.deadEmbryo = deadEmbryo;
  }

  public Boolean getDefiniteFather() {
    return definiteFather;
  }

  public void setDefiniteFather(Boolean definiteFather) {
    this.definiteFather = definiteFather;
  }

  public Date getDemise() {
    return demise;
  }

  public void setDemise(Date demise) {
    this.demise = demise;
  }

  public Date getDiscoveryDate() {
    return discoveryDate;
  }

  public void setDiscoveryDate(Date discoveryDate) {
    this.discoveryDate = discoveryDate;
  }

  public String getEggName() {
    return eggName;
  }

  public void setEggName(String eggName) {
    this.eggName = eggName;
  }

  public Float getEstAgeWhen1stFound() {
    return estAgeWhen1stFound;
  }

  public void setEstAgeWhen1stFound(Float estAgeWhen1stFound) {
    this.estAgeWhen1stFound = estAgeWhen1stFound;
  }

  public String getFather() {
    return father;
  }

  public void setFather(String father) {
    this.father = father;
  }

  public String getFirstDayAtOrVeryCloseToNest() {
    return firstDayAtOrVeryCloseToNest;
  }

  public void setFirstDayAtOrVeryCloseToNest(String firstDayAtOrVeryCloseToNest) {
    this.firstDayAtOrVeryCloseToNest = firstDayAtOrVeryCloseToNest;
  }

  public Boolean getFledged() {
    return fledged;
  }

  public void setFledged(Boolean fledged) {
    this.fledged = fledged;
  }

  public String getGan() {
    return gan;
  }

  public void setGan(String gan) {
    this.gan = gan;
  }

  public String getHouseID() {
    return houseID;
  }

  public void setHouseID(String houseID) {
    this.houseID = houseID;
  }

  public Float getIncubationPeriod() {
    return incubationPeriod;
  }

  public void setIncubationPeriod(Float incubationPeriod) {
    this.incubationPeriod = incubationPeriod;
  }

  public Float getInterClutchLayingInterval() {
    return interClutchLayingInterval;
  }

  public void setInterClutchLayingInterval(Float interClutchLayingInterval) {
    this.interClutchLayingInterval = interClutchLayingInterval;
  }

  public String getLastDateMated() {
    return lastDateMated;
  }

  public void setLastDateMated(String lastDateMated) {
    this.lastDateMated = lastDateMated;
  }

  public String getLastTxChangeId() {
    return lastTxChangeId;
  }

  public void setLastTxChangeId(String lastTxChangeId) {
    this.lastTxChangeId = lastTxChangeId;
  }

  public Boolean getLayDateIsEstimate() {
    return layDateIsEstimate;
  }

  public void setLayDateIsEstimate(Boolean layDateIsEstimate) {
    this.layDateIsEstimate = layDateIsEstimate;
  }

  public String getLayIsland() {
    return layIsland;
  }

  public void setLayIsland(String layIsland) {
    this.layIsland = layIsland;
  }

  public Integer getLayYear() {
    return layYear;
  }

  public void setLayYear(Integer layYear) {
    this.layYear = layYear;
  }

  public String getLegColour() {
    return legColour;
  }

  public void setLegColour(String legColour) {
    this.legColour = legColour;
  }


  public String getMatedWith() {
    return matedWith;
  }

  public void setMatedWith(String matedWith) {
    this.matedWith = matedWith;
  }

  public Float getMatingToLaying() {
    return matingToLaying;
  }

  public void setMatingToLaying(Float matingToLaying) {
    this.matingToLaying = matingToLaying;
  }

  public Float getMatingToNestingDays() {
    return matingToNestingDays;
  }

  public void setMatingToNestingDays(Float matingToNestingDays) {
    this.matingToNestingDays = matingToNestingDays;
  }

  public String getModifiedByRecordId() {
    return modifiedByRecordId;
  }

  public void setModifiedByRecordId(String modifiedByRecordId) {
    this.modifiedByRecordId = modifiedByRecordId;
  }

  public String getMother() {
    return mother;
  }

  public void setMother(String mother) {
    this.mother = mother;
  }

  public Float getNestingToLaying() {
    return nestingToLaying;
  }

  public void setNestingToLaying(Float nestingToLaying) {
    this.nestingToLaying = nestingToLaying;
  }

  public String getNestMother() {
    return nestMother;
  }

  public void setNestMother(String nestMother) {
    this.nestMother = nestMother;
  }

  public String getOldBirdID() {
    return oldBirdID;
  }

  public void setOldBirdID(String oldBirdID) {
    this.oldBirdID = oldBirdID;
  }

  public String getPlumageColour() {
    return plumageColour;
  }

  public void setPlumageColour(String plumageColour) {
    this.plumageColour = plumageColour;
  }

  public String getResults() {
    return results;
  }

  public void setResults(String results) {
    this.results = results;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public Integer getStudbookno() {
    return studbookno;
  }

  public void setStudbookno(Integer studbookno) {
    this.studbookno = studbookno;
  }

  public String getTransmitterGroup() {
    return transmitterGroup;
  }

  public void setTransmitterGroup(String transmitterGroup) {
    this.transmitterGroup = transmitterGroup;
  }

  public String getViable() {
    return viable;
  }

  public void setViable(String viable) {
    this.viable = viable;
  }

  public Float getWeight() {
    return historicDataFirstEggWeight;
  }

  public void setHistoricDataFirstEggWeight(Float historicDataFirstEggWeight) {
    this.historicDataFirstEggWeight = historicDataFirstEggWeight;
  }

  public List<BirdFeatureEntity> getBirdFeatureList() {
    return birdFeatureList;
  }

  public void setBirdFeatureList(List<BirdFeatureEntity> birdFeatureList) {
    this.birdFeatureList = birdFeatureList;
  }

  public EggMeasurementsEntity getEggMeasurements() {
    return eggMeasurements;
  }

  public void setEggMeasurements(EggMeasurementsEntity eggMeasurements) {
    this.eggMeasurements = eggMeasurements;
  }

  public EmbryoMeasurementsEntity getEmbryoMeasurements() {
    return embryoMeasurements;
  }

  public void setEmbryoMeasurements(EmbryoMeasurementsEntity embryoMeasurements) {
    this.embryoMeasurements = embryoMeasurements;
  }

  public Boolean getFertilityConfirmed() {
    return fertilityConfirmed;
  }

  public void setFertilityConfirmed(Boolean fertilityConfirmed) {
    this.fertilityConfirmed = fertilityConfirmed;
  }

  public String getLayLocationID() {
    return layLocationID;
  }

  public void setLayLocationID(String layLocationID) {
    this.layLocationID = layLocationID;
  }
}
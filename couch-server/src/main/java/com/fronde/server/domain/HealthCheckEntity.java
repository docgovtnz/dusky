package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class HealthCheckEntity {

  @Field
  private Integer band;
  @Field
  private Integer bill;
  @Field
  private String billNotes;
  @Field
  private Boolean boomSac;
  @Field
  private Boolean broodPatch;
  @Field
  private Integer cereAndNostrils;
  @Field
  private String cereNotes;
  @Field
  private Integer cloaca;
  @Field
  private String cloacaNotes;
  @Field
  private Float cloacaScore;
  @Field
  private Integer ears;
  @Field
  private String earsNotes;
  @Field
  private Integer eyes;
  @Field
  private String eyesNotes;
  @Field
  private Integer feetAndToes;
  @Field
  private String feetNotes;
  @Field
  private Boolean inMoult;
  @Field
  private Integer legs;
  @Field
  private String legsNotes;
  @Field
  private Integer microchip;
  @Field
  private String microchipNotes;
  @Field
  private Integer microchipNumber;
  @Field
  private Integer plumage;
  @Field
  private String plumageNotes;
  @Field
  private String recoveryid;
  @Field
  private Integer transmitter;
  @Field
  private String transmitterNotes;
  @Field
  private Integer vent;
  @Field
  private String ventNotes;
  @Field
  private Float ventScore;
  @Field
  private Integer patagia;
  @Field
  private Integer wings;
  @Field
  private Integer back;
  @Field
  private Integer chest;

  @Field
  private String patagiaNotes;
  @Field
  private String wingsNotes;
  @Field
  private String backNotes;
  @Field
  private String chestNotes;

  public Integer getBand() {
    return band;
  }

  public void setBand(Integer band) {
    this.band = band;
  }

  public Integer getBill() {
    return bill;
  }

  public void setBill(Integer bill) {
    this.bill = bill;
  }

  public String getBillNotes() {
    return billNotes;
  }

  public void setBillNotes(String billNotes) {
    this.billNotes = billNotes;
  }

  public Boolean getBoomSac() {
    return boomSac;
  }

  public void setBoomSac(Boolean boomSac) {
    this.boomSac = boomSac;
  }

  public Boolean getBroodPatch() {
    return broodPatch;
  }

  public void setBroodPatch(Boolean broodPatch) {
    this.broodPatch = broodPatch;
  }

  public Integer getCereAndNostrils() {
    return cereAndNostrils;
  }

  public void setCereAndNostrils(Integer cereAndNostrils) {
    this.cereAndNostrils = cereAndNostrils;
  }

  public String getCereNotes() {
    return cereNotes;
  }

  public void setCereNotes(String cereNotes) {
    this.cereNotes = cereNotes;
  }

  public Integer getCloaca() {
    return cloaca;
  }

  public void setCloaca(Integer cloaca) {
    this.cloaca = cloaca;
  }

  public String getCloacaNotes() {
    return cloacaNotes;
  }

  public void setCloacaNotes(String cloacaNotes) {
    this.cloacaNotes = cloacaNotes;
  }

  public Float getCloacaScore() {
    return cloacaScore;
  }

  public void setCloacaScore(Float cloacaScore) {
    this.cloacaScore = cloacaScore;
  }

  public Integer getEars() {
    return ears;
  }

  public void setEars(Integer ears) {
    this.ears = ears;
  }

  public String getEarsNotes() {
    return earsNotes;
  }

  public void setEarsNotes(String earsNotes) {
    this.earsNotes = earsNotes;
  }

  public Integer getEyes() {
    return eyes;
  }

  public void setEyes(Integer eyes) {
    this.eyes = eyes;
  }

  public String getEyesNotes() {
    return eyesNotes;
  }

  public void setEyesNotes(String eyesNotes) {
    this.eyesNotes = eyesNotes;
  }

  public Integer getFeetAndToes() {
    return feetAndToes;
  }

  public void setFeetAndToes(Integer feetAndToes) {
    this.feetAndToes = feetAndToes;
  }

  public String getFeetNotes() {
    return feetNotes;
  }

  public void setFeetNotes(String feetNotes) {
    this.feetNotes = feetNotes;
  }

  public Boolean getInMoult() {
    return inMoult;
  }

  public void setInMoult(Boolean inMoult) {
    this.inMoult = inMoult;
  }

  public Integer getLegs() {
    return legs;
  }

  public void setLegs(Integer legs) {
    this.legs = legs;
  }

  public String getLegsNotes() {
    return legsNotes;
  }

  public void setLegsNotes(String legsNotes) {
    this.legsNotes = legsNotes;
  }

  public Integer getMicrochip() {
    return microchip;
  }

  public void setMicrochip(Integer microchip) {
    this.microchip = microchip;
  }

  public String getMicrochipNotes() {
    return microchipNotes;
  }

  public void setMicrochipNotes(String microchipNotes) {
    this.microchipNotes = microchipNotes;
  }

  public Integer getMicrochipNumber() {
    return microchipNumber;
  }

  public void setMicrochipNumber(Integer microchipNumber) {
    this.microchipNumber = microchipNumber;
  }

  public Integer getPlumage() {
    return plumage;
  }

  public void setPlumage(Integer plumage) {
    this.plumage = plumage;
  }

  public String getPlumageNotes() {
    return plumageNotes;
  }

  public void setPlumageNotes(String plumageNotes) {
    this.plumageNotes = plumageNotes;
  }

  public String getRecoveryid() {
    return recoveryid;
  }

  public void setRecoveryid(String recoveryid) {
    this.recoveryid = recoveryid;
  }

  public Integer getTransmitter() {
    return transmitter;
  }

  public void setTransmitter(Integer transmitter) {
    this.transmitter = transmitter;
  }

  public String getTransmitterNotes() {
    return transmitterNotes;
  }

  public void setTransmitterNotes(String transmitterNotes) {
    this.transmitterNotes = transmitterNotes;
  }

  public Integer getVent() {
    return vent;
  }

  public void setVent(Integer vent) {
    this.vent = vent;
  }

  public String getVentNotes() {
    return ventNotes;
  }

  public void setVentNotes(String ventNotes) {
    this.ventNotes = ventNotes;
  }

  public Float getVentScore() {
    return ventScore;
  }

  public void setVentScore(Float ventScore) {
    this.ventScore = ventScore;
  }

  public Integer getPatagia() {
    return patagia;
  }
  public void setPatagia(Integer patagia) {
    this.patagia = patagia;
  }
  public String getPatagiaNotes() {
    return patagiaNotes;
  }
  public void setPatagiaNotes(String patagiaNotes) {
    this.patagiaNotes = patagiaNotes;
  }

  public Integer getWings() {
    return wings;
  }
  public void setWings(Integer wings) {
    this.wings = wings;
  }
  public String getWingsNotes() {
    return wingsNotes;
  }
  public void setWingsNotes(String wingsNotes) {
    this.wingsNotes = wingsNotes;
  }

  public Integer getBack() {
    return back;
  }
  public void setBack(Integer back) {
    this.back = back;
  }
  public String getBackNotes() {
    return backNotes;
  }
  public void setBackNotes(String backNotes) {
    this.backNotes = backNotes;
  }

  public Integer getChest() {
    return chest;
  }
  public void setChest(Integer chest) {
    this.chest = chest;
  }
  public String getChestNotes() {
    return chestNotes;
  }
  public void setChestNotes(String chestNotes) {
    this.chestNotes = chestNotes;
  }


}
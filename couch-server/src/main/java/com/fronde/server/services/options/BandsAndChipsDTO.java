package com.fronde.server.services.options;

import java.util.Date;

public class BandsAndChipsDTO {

  private String newBandNumber;
  private String leg;
  private String microchip;
  private Date bandDateTime;
  private Date chipDateTime;

  public String getNewBandNumber() {
    return newBandNumber;
  }

  public void setNewBandNumber(String newBandNumber) {
    this.newBandNumber = newBandNumber;
  }

  public String getLeg() {
    return leg;
  }

  public void setLeg(String leg) {
    this.leg = leg;
  }

  public String getMicrochip() {
    return microchip;
  }

  public void setMicrochip(String microchip) {
    this.microchip = microchip;
  }

  public Date getBandDateTime() {
    return bandDateTime;
  }

  public void setBandDateTime(Date bandDateTime) {
    this.bandDateTime = bandDateTime;
  }

  public Date getChipDateTime() {
    return chipDateTime;
  }

  public void setChipDateTime(Date chipDateTime) {
    this.chipDateTime = chipDateTime;
  }
}

package com.fronde.server.services.bird;

import java.util.Date;

public class CurrentChipDTO {

  private Date dateTime;
  private String microchip;

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public String getMicrochip() {
    return microchip;
  }

  public void setMicrochip(String microchip) {
    this.microchip = microchip;
  }

}

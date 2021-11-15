package com.fronde.server.services.checksheetreport;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class ChecksheetDTO {

  private String birdID;

  private String birdName;

  private Date dateTime;

  private String recordType;

  private String reason;

  private String interactionType;

  private LocalDate alignedDate;


  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public Date getDateTime() {
    return dateTime;
  }

  public void seDdateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public String getInteractionType() {
    return interactionType;
  }

  public void setInteractionType(String interactionType) {
    this.interactionType = interactionType;
  }

  public String getBirdName() {
    return birdName;
  }

  public void setBirdName(String birdName) {
    this.birdName = birdName;
  }

  public void deriveAlignedDate(LocalDate startDate, int daysInPeriod) {
    this.setAlignedDate(startDate.plusDays(
        startDate.until(toLocalDate(dateTime), ChronoUnit.DAYS) / daysInPeriod * daysInPeriod));
  }

  private LocalDate toLocalDate(Date dt) {
    return dt.toInstant().atZone(ZoneId.of("Pacific/Auckland")).toLocalDate();
  }

  public LocalDate getAlignedDate() {
    return alignedDate;
  }

  public void setAlignedDate(LocalDate alignedDate) {
    this.alignedDate = alignedDate;
  }

  public String getRecordType() {
    return recordType;
  }

  public void setRecordType(String recordType) {
    this.recordType = recordType;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }
}

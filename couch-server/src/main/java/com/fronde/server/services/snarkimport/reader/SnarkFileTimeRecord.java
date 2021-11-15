package com.fronde.server.services.snarkimport.reader;

import java.time.LocalDateTime;

public class SnarkFileTimeRecord extends SnarkFileRecord {

  private LocalDateTime dateTime;

  public SnarkFileTimeRecord(LocalDateTime dateTime) {
    this.recordType = RECORD_TYPE_TIME;
    this.dateTime = dateTime;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public void setDateTime(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  @Override
  public String toString() {
    return "SnarkFileTimeRecord{" +
        "dateTime=" + dateTime +
        '}';
  }
}

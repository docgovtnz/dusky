package com.fronde.server.services.snarkimport.reader;

import java.time.LocalTime;

public class SnarkFileArrivalRecord extends SnarkFileRecord implements SnarkFileTimedRecord,
    SnarkFileBirdRecord {

  private LocalTime time;
  private int uhfId;

  public SnarkFileArrivalRecord(LocalTime time, int uhfId) {
    this.recordType = RECORD_TYPE_ARRIVAL;
    this.time = time;
    this.uhfId = uhfId;
  }

  @Override
  public LocalTime getTime() {
    return time;
  }

  public void setTime(LocalTime time) {
    this.time = time;
  }

  @Override
  public int getUhfId() {
    return uhfId;
  }

  public void setUhfId(int uhfId) {
    this.uhfId = uhfId;
  }

  @Override
  public String toString() {
    return "SnarkFileArrivalRecord{" +
        "time=" + time +
        ", uhfId=" + uhfId +
        '}';
  }

}

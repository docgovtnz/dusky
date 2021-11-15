package com.fronde.server.services.snarkimport.reader;

import java.time.LocalTime;

public class SnarkFileBatteryRecord extends SnarkFileRecord implements SnarkFileTimedRecord {

  private LocalTime time;
  private int type;

  public SnarkFileBatteryRecord(LocalTime time, int type) {
    this.recordType = RECORD_TYPE_BATTERY;
    this.time = time;
    this.type = type;
  }

  @Override
  public LocalTime getTime() {
    return time;
  }

  public void setTime(LocalTime time) {
    this.time = time;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "SnarkFileBatteryRecord{" +
        "time=" + time +
        ", type=" + type +
        '}';
  }

}

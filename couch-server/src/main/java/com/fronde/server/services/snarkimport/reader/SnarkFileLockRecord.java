package com.fronde.server.services.snarkimport.reader;

import java.time.LocalTime;

public class SnarkFileLockRecord extends SnarkFileRecord implements SnarkFileTimedRecord {

  private LocalTime time;
  private int count;

  public SnarkFileLockRecord(LocalTime time, int count) {
    this.recordType = RECORD_TYPE_LOCK;
    this.time = time;
    this.count = count;
  }

  @Override
  public LocalTime getTime() {
    return time;
  }

  public void setTime(LocalTime time) {
    this.time = time;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  @Override
  public String toString() {
    return "SnarkFileLockRecord{" +
        "time=" + time +
        ", count=" + count +
        '}';
  }

}

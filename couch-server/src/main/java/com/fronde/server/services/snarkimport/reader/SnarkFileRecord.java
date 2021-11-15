package com.fronde.server.services.snarkimport.reader;

public class SnarkFileRecord {

  public static final String RECORD_TYPE_TIME = "Time";
  public static final String RECORD_TYPE_ARRIVAL = "Arrival";
  public static final String RECORD_TYPE_DEPARTURE = "Departure";
  public static final String RECORD_TYPE_WEIGHT = "Weight";
  public static final String RECORD_TYPE_LOCK = "Lock";
  public static final String RECORD_TYPE_BATTERY = "Battery";
  public static final String RECORD_TYPE_EGG_TIMER = "Egg Timer";

  protected String recordType;

  public String getRecordType() {
    return recordType;
  }

  public void setRecordType(String recordType) {
    this.recordType = recordType;
  }

}


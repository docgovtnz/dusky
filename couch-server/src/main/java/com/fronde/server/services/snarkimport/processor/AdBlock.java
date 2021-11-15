package com.fronde.server.services.snarkimport.processor;

import com.fronde.server.services.snarkimport.reader.SnarkFileWeightRecord;
import java.time.LocalDateTime;
import java.util.List;

public class AdBlock {

  private Integer birdID;
  private LocalDateTime arrival;
  private LocalDateTime departure;
  private List<SnarkFileWeightRecord> weightRecordList;

  public Integer getBirdID() {
    return birdID;
  }

  public void setBirdID(Integer birdID) {
    this.birdID = birdID;
  }

  public LocalDateTime getArrival() {
    return arrival;
  }

  public void setArrival(LocalDateTime arrival) {
    this.arrival = arrival;
  }

  public LocalDateTime getDeparture() {
    return departure;
  }

  public void setDeparture(LocalDateTime departure) {
    this.departure = departure;
  }

  public List<SnarkFileWeightRecord> getWeightRecordList() {
    return weightRecordList;
  }

  public void setWeightRecordList(List<SnarkFileWeightRecord> weightRecordList) {
    this.weightRecordList = weightRecordList;
  }

}

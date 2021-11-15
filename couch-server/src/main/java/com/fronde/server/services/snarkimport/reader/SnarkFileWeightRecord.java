package com.fronde.server.services.snarkimport.reader;

import java.time.LocalTime;

public class SnarkFileWeightRecord extends SnarkFileRecord implements SnarkFileTimedRecord {

  private LocalTime time;
  private int weight;
  private int quality;

  public SnarkFileWeightRecord(LocalTime time, int weight, int quality) {
    this.recordType = RECORD_TYPE_WEIGHT;
    this.time = time;
    this.weight = weight;
    this.quality = quality;
  }

  @Override
  public LocalTime getTime() {
    return time;
  }

  public void setTime(LocalTime time) {
    this.time = time;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public int getQuality() {
    return quality;
  }

  public void setQuality(int quality) {
    this.quality = quality;
  }

  @Override
  public String toString() {
    return "SnarkFileWeightRecord{" +
        "time=" + time +
        ", weight=" + weight +
        ", quality=" + quality +
        '}';
  }

}

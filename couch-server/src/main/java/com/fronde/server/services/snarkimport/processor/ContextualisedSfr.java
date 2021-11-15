package com.fronde.server.services.snarkimport.processor;

import com.fronde.server.services.snarkimport.reader.SnarkFileRecord;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * The context for each snark record is established by using information from previous records. This
 * data structure allows to attach this information to each record making it easier to process.
 */
public class ContextualisedSfr {

  LocalDateTime dateTime;
  Set<Integer> presentBirds;
  SnarkFileRecord record;

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public void setDateTime(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  public Set<Integer> getPresentBirds() {
    return presentBirds;
  }

  public void setPresentBirds(Set<Integer> presentBirds) {
    this.presentBirds = presentBirds;
  }

  public SnarkFileRecord getRecord() {
    return record;
  }

  public void setRecord(SnarkFileRecord record) {
    this.record = record;
  }
}

package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class InfertileEggEntity extends BaseEntity {

  @Field
  private String birdID;
  @Field
  private EggMeasurementsEntity eggMeasurements;

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public EggMeasurementsEntity getEggMeasurements() {
    return eggMeasurements;
  }

  public void setEggMeasurements(EggMeasurementsEntity eggMeasurements) {
    this.eggMeasurements = eggMeasurements;
  }

}
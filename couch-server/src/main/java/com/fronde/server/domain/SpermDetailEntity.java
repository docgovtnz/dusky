package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class SpermDetailEntity {

  @Field
  private String diluent;
  private Float volumeInMicroL;
  @Field
  private String collectionMethod;
  @Field
  private String papillaSwelling;
  @Field
  private String stimulation;
  @Field
  private String stress;

  public String getDiluent() {
    return diluent;
  }

  public void setDiluent(String diluent) {
    this.diluent = diluent;
  }

  public Float getVolumeInMicroL() {
    return volumeInMicroL;
  }

  public void setVolumeInMicroL(Float volumeInMicroL) {
    this.volumeInMicroL = volumeInMicroL;
  }

  public String getCollectionMethod() {
    return collectionMethod;
  }

  public void setCollectionMethod(String collectionMethod) {
    this.collectionMethod = collectionMethod;
  }

  public String getPapillaSwelling() {
    return papillaSwelling;
  }

  public void setPapillaSwelling(String papillaSwelling) {
    this.papillaSwelling = papillaSwelling;
  }

  public String getStimulation() {
    return stimulation;
  }

  public void setStimulation(String stimulation) {
    this.stimulation = stimulation;
  }

  public String getStress() {
    return stress;
  }

  public void setStress(String stress) {
    this.stress = stress;
  }

}
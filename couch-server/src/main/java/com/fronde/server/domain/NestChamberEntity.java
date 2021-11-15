package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class NestChamberEntity {

  @Field
  private Float temperature;
  @Field
  private Integer relativeHumidity;
  @Field
  private Boolean parasitesPresent;
  @Field
  private String conditions;

  public Float getTemperature() { return temperature; }

  public void setTemperature(Float temperature) { this.temperature = temperature; }

  public Integer getRelativeHumidity() {
    return relativeHumidity;
  }

  public void setRelativeHumidity(Integer relativeHumidity) {
    this.relativeHumidity = relativeHumidity;
  }

  public Boolean getParasitesPresent() {
    return parasitesPresent;
  }

  public void setParasitesPresent(Boolean parasitesPresent) {
    this.parasitesPresent = parasitesPresent;
  }

  public String getConditions() {
    return conditions;
  }

  public void setConditions(String conditions) {
    this.conditions = conditions;
  }
}
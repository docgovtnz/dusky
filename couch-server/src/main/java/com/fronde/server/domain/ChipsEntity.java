package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class ChipsEntity {

  @Field
  private String microchip;

  public String getMicrochip() {
    return microchip;
  }

  public void setMicrochip(String microchip) {
    this.microchip = microchip;
  }

}
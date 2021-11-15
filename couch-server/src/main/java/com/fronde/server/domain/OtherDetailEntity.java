package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class OtherDetailEntity {

  @Field
  private String type;
  private Float amount;
  private String units;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Float getAmount() {
    return amount;
  }

  public void setAmount(Float amount) {
    this.amount = amount;
  }

  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
  }

}
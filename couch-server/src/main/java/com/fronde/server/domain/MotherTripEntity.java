package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class MotherTripEntity {

  @Field
  private Date motherLeft;
  @Field
  private Date motherBack;

  public Date getMotherLeft() {
    return motherLeft;
  }

  public void setMotherLeft(Date motherLeft) {
    this.motherLeft = motherLeft;
  }

  public Date getMotherBack() {
    return motherBack;
  }

  public void setMotherBack(Date motherBack) {
    this.motherBack = motherBack;
  }

}
package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class MotherTripsEntity {

  @Field
  private Date motherBack;
  @Field
  private Date motherLeft;

  public Date getMotherBack() {
    return motherBack;
  }

  public void setMotherBack(Date motherBack) {
    this.motherBack = motherBack;
  }

  public Date getMotherLeft() {
    return motherLeft;
  }

  public void setMotherLeft(Date motherLeft) {
    this.motherLeft = motherLeft;
  }

}
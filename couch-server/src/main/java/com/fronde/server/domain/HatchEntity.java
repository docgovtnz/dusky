package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class HatchEntity extends BaseEntity {

  @Field
  private String birdID;
  @Field
  private Date hatchDate;

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public Date getHatchDate() {
    return hatchDate;
  }

  public void setHatchDate(Date hatchDate) {
    this.hatchDate = hatchDate;
  }

}
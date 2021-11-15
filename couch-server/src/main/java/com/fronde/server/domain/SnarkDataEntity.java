package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class SnarkDataEntity {

  @Field
  private Date arriveDateTime;
  @Field
  private Date departDateTime;
  @Field
  private Boolean mating;
  @Field
  private String oldSnarkRecId;
  @Field
  private String oldTAndBRecId;
  @Field
  private String snarkActivityID;

  public Date getArriveDateTime() {
    return arriveDateTime;
  }

  public void setArriveDateTime(Date arriveDateTime) {
    this.arriveDateTime = arriveDateTime;
  }

  public Date getDepartDateTime() {
    return departDateTime;
  }

  public void setDepartDateTime(Date departDateTime) {
    this.departDateTime = departDateTime;
  }

  public Boolean getMating() {
    return mating;
  }

  public void setMating(Boolean mating) {
    this.mating = mating;
  }

  public String getOldSnarkRecId() {
    return oldSnarkRecId;
  }

  public void setOldSnarkRecId(String oldSnarkRecId) {
    this.oldSnarkRecId = oldSnarkRecId;
  }

  public String getOldTAndBRecId() {
    return oldTAndBRecId;
  }

  public void setOldTAndBRecId(String oldTAndBRecId) {
    this.oldTAndBRecId = oldTAndBRecId;
  }

  public String getSnarkActivityID() {
    return snarkActivityID;
  }

  public void setSnarkActivityID(String snarkActivityID) {
    this.snarkActivityID = snarkActivityID;
  }

}
package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import java.util.List;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class WeanedChickEntity extends BaseEntity {

  @Field
  private String birdID;
  @Field
  private Date date;
  @Field
  private List<ObserverEntity> observerList;

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public List<ObserverEntity> getObserverList() {
    return observerList;
  }

  public void setObserverList(List<ObserverEntity> observerList) {
    this.observerList = observerList;
  }

}
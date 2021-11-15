package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import java.util.List;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class NewEggEntity extends BaseEntity {

  @Field private String birdID;
  @Field private Integer clutchOrder;
  @Field private String eggName;
  @Field private Float fwCoefficientX10P4;
  @Field private Date layDate;
  @Field private Boolean layDateIsEstimate;
  @Field private Float eggLength;
  @Field private String locationID;
  @Field private String mother;
  @Field private Float eggWidth;
  @Field private List<ObserverEntity> observerList;

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public Integer getClutchOrder() {
    return clutchOrder;
  }

  public void setClutchOrder(Integer clutchOrder) {
    this.clutchOrder = clutchOrder;
  }

  public String getEggName() {
    return eggName;
  }

  public void setEggName(String eggName) {
    this.eggName = eggName;
  }

  public Float getFwCoefficientX10P4() {
    return fwCoefficientX10P4;
  }

  public void setFwCoefficientX10P4(Float fwCoefficientX10P4) {
    this.fwCoefficientX10P4 = fwCoefficientX10P4;
  }

  public Date getLayDate() {
    return layDate;
  }

  public void setLayDate(Date layDate) {
    this.layDate = layDate;
  }

  public Boolean getLayDateIsEstimate() {
    return layDateIsEstimate;
  }

  public void setLayDateIsEstimate(Boolean layDateIsEstimate) {
    this.layDateIsEstimate = layDateIsEstimate;
  }

  public Float getEggLength() {
    return eggLength;
  }

  public void setEggLength(Float eggLength) {
    this.eggLength = eggLength;
  }

  public String getLocationID() {
    return locationID;
  }

  public void setLocationID(String locationID) {
    this.locationID = locationID;
  }

  public String getMother() {
    return mother;
  }

  public void setMother(String mother) {
    this.mother = mother;
  }

  public Float getEggWidth() {
    return eggWidth;
  }

  public void setEggWidth(Float eggWidth) {
    this.eggWidth = eggWidth;
  }

  public List<ObserverEntity> getObserverList() {
    return observerList;
  }

  public void setObserverList(List<ObserverEntity> observerList) {
    this.observerList = observerList;
  }
}

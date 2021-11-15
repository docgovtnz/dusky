package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class IslandEntity extends BaseEntity {

  @Field
  private Float lowerEasting;
  @Field
  private Float lowerNorthing;
  @Field
  private Float magneticDeclination;
  @Field
  private Integer magneticDeclinationAsOfYear;
  @Field
  private String name;
  @Field
  private Float upperEasting;
  @Field
  private Float upperNorthing;
  @Field
  private Integer islandId;

  public Float getLowerEasting() {
    return lowerEasting;
  }

  public void setLowerEasting(Float lowerEasting) {
    this.lowerEasting = lowerEasting;
  }

  public Float getLowerNorthing() {
    return lowerNorthing;
  }

  public void setLowerNorthing(Float lowerNorthing) {
    this.lowerNorthing = lowerNorthing;
  }

  public Float getMagneticDeclination() {
    return magneticDeclination;
  }

  public void setMagneticDeclination(Float magneticDeclination) {
    this.magneticDeclination = magneticDeclination;
  }

  public Integer getMagneticDeclinationAsOfYear() {
    return magneticDeclinationAsOfYear;
  }

  public void setMagneticDeclinationAsOfYear(Integer magneticDeclinationAsOfYear) {
    this.magneticDeclinationAsOfYear = magneticDeclinationAsOfYear;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Float getUpperEasting() {
    return upperEasting;
  }

  public void setUpperEasting(Float upperEasting) {
    this.upperEasting = upperEasting;
  }

  public Float getUpperNorthing() {
    return upperNorthing;
  }

  public void setUpperNorthing(Float upperNorthing) {
    this.upperNorthing = upperNorthing;
  }

  public Integer getIslandId() {
    return islandId;
  }

  public void setIslandId(Integer islandId) {
    this.islandId = islandId;
  }
}
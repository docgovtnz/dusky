package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class NestDetailsEntity {

  @Field
  private Float chamberHeight;
  @Field
  private Float chamberLength;
  @Field
  private Float chamberRise;
  @Field
  private Float chamberWidth;
  @Field
  private String clutch;
  @Field
  private Float entranceHeight;
  @Field
  private Float entranceRise;
  @Field
  private Float entranceWidth;
  @Field
  private String locationName;
  @Field
  private Boolean nestBox;
  @Field
  private Date nestBoxDateAdded;
  @Field
  private String nestType;
  @Field
  private String oldSiteDescription;

  public Float getChamberHeight() {
    return chamberHeight;
  }

  public void setChamberHeight(Float chamberHeight) {
    this.chamberHeight = chamberHeight;
  }

  public Float getChamberLength() {
    return chamberLength;
  }

  public void setChamberLength(Float chamberLength) {
    this.chamberLength = chamberLength;
  }

  public Float getChamberRise() {
    return chamberRise;
  }

  public void setChamberRise(Float chamberRise) {
    this.chamberRise = chamberRise;
  }

  public Float getChamberWidth() {
    return chamberWidth;
  }

  public void setChamberWidth(Float chamberWidth) {
    this.chamberWidth = chamberWidth;
  }

  public String getClutch() {
    return clutch;
  }

  public void setClutch(String clutch) {
    this.clutch = clutch;
  }

  public Float getEntranceHeight() {
    return entranceHeight;
  }

  public void setEntranceHeight(Float entranceHeight) {
    this.entranceHeight = entranceHeight;
  }

  public Float getEntranceRise() {
    return entranceRise;
  }

  public void setEntranceRise(Float entranceRise) {
    this.entranceRise = entranceRise;
  }

  public Float getEntranceWidth() {
    return entranceWidth;
  }

  public void setEntranceWidth(Float entranceWidth) {
    this.entranceWidth = entranceWidth;
  }

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public Boolean getNestBox() {
    return nestBox;
  }

  public void setNestBox(Boolean nestBox) {
    this.nestBox = nestBox;
  }

  public Date getNestBoxDateAdded() {
    return nestBoxDateAdded;
  }

  public void setNestBoxDateAdded(Date nestBoxDateAdded) {
    this.nestBoxDateAdded = nestBoxDateAdded;
  }

  public String getNestType() {
    return nestType;
  }

  public void setNestType(String nestType) {
    this.nestType = nestType;
  }

  public String getOldSiteDescription() {
    return oldSiteDescription;
  }

  public void setOldSiteDescription(String oldSiteDescription) {
    this.oldSiteDescription = oldSiteDescription;
  }

}
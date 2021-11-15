package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class LocationBearingEntity {

  @Field
  private String locationID;
  @Field
  private Float easting;
  @Field
  private Float northing;
  @Field
  private Float compassBearing;
  @Field
  private Boolean active;

  public String getLocationID() {
    return locationID;
  }

  public void setLocationID(String locationID) {
    this.locationID = locationID;
  }

  public Float getEasting() {
    return easting;
  }

  public void setEasting(Float easting) {
    this.easting = easting;
  }

  public Float getNorthing() {
    return northing;
  }

  public void setNorthing(Float northing) {
    this.northing = northing;
  }

  public Float getCompassBearing() {
    return compassBearing;
  }

  public void setCompassBearing(Float compassBearing) {
    this.compassBearing = compassBearing;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

}
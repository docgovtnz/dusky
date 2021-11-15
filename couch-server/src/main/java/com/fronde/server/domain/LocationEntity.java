package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class LocationEntity extends BaseEntity {

  @Field
  private Boolean active;
  @Field
  private String birdID;
  @Field
  private String captivityType;
  @Field
  private String comments;
  @Field
  private Float easting;
  @Field
  private Date firstDate;
  @Field
  private Integer gpscount;
  @Field
  private String island;
  @Field
  private String locationName;
  @Field
  private String locationType;
  @Field
  private String mappingMethod;
  @Field
  private Float northing;
  @Field
  private NestDetailsEntity nestDetails;

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public String getCaptivityType() {
    return captivityType;
  }

  public void setCaptivityType(String captivityType) {
    this.captivityType = captivityType;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public Float getEasting() {
    return easting;
  }

  public void setEasting(Float easting) {
    this.easting = easting;
  }

  public Date getFirstDate() {
    return firstDate;
  }

  public void setFirstDate(Date firstDate) {
    this.firstDate = firstDate;
  }

  public Integer getGpscount() {
    return gpscount;
  }

  public void setGpscount(Integer gpscount) {
    this.gpscount = gpscount;
  }

  public String getIsland() {
    return island;
  }

  public void setIsland(String island) {
    this.island = island;
  }

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public String getLocationType() {
    return locationType;
  }

  public void setLocationType(String locationType) {
    this.locationType = locationType;
  }

  public String getMappingMethod() {
    return mappingMethod;
  }

  public void setMappingMethod(String mappingMethod) {
    this.mappingMethod = mappingMethod;
  }

  public Float getNorthing() {
    return northing;
  }

  public void setNorthing(Float northing) {
    this.northing = northing;
  }

  public NestDetailsEntity getNestDetails() {
    return nestDetails;
  }

  public void setNestDetails(NestDetailsEntity nestDetails) {
    this.nestDetails = nestDetails;
  }

}
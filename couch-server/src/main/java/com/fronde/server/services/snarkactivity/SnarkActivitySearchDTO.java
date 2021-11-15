package com.fronde.server.services.snarkactivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SnarkActivitySearchDTO {

  private String snarkActivityId;
  private String activityType;
  private Date date;
  private String locationID;
  private String locationName;
  private Boolean boom;
  private Boolean ching;
  private String grubbing;
  private String matingSign;
  private Boolean skraak;
  private String sticks;
  private String trackActivity;
  private List<Bird> birds = new ArrayList<>();

  public SnarkActivitySearchDTO() {
  }

  public String getSnarkActivityId() {
    return snarkActivityId;
  }

  public void setSnarkActivityId(String snarkActivityId) {
    this.snarkActivityId = snarkActivityId;
  }

  public String getActivityType() {
    return activityType;
  }

  public void setActivityType(String activityType) {
    this.activityType = activityType;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getLocationID() {
    return locationID;
  }

  public void setLocationID(String locationID) {
    this.locationID = locationID;
  }

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public Boolean getBoom() {
    return boom;
  }

  public void setBoom(Boolean boom) {
    this.boom = boom;
  }

  public Boolean getChing() {
    return ching;
  }

  public void setChing(Boolean ching) {
    this.ching = ching;
  }

  public String getGrubbing() {
    return grubbing;
  }

  public void setGrubbing(String grubbing) {
    this.grubbing = grubbing;
  }

  public String getMatingSign() {
    return matingSign;
  }

  public void setMatingSign(String matingSign) {
    this.matingSign = matingSign;
  }

  public Boolean getSkraak() {
    return skraak;
  }

  public void setSkraak(Boolean skraak) {
    this.skraak = skraak;
  }

  public String getSticks() {
    return sticks;
  }

  public void setSticks(String sticks) {
    this.sticks = sticks;
  }

  public String getTrackActivity() {
    return trackActivity;
  }

  public void setTrackActivity(String trackActivity) {
    this.trackActivity = trackActivity;
  }

  public List<Bird> getBirds() {
    return birds;
  }

  public void setBirds(List<Bird> birds) {
    this.birds = birds;
  }

  public static class Bird {

    private String id;
    private String name;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

  }

}

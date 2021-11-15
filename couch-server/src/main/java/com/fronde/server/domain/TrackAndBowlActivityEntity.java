package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class TrackAndBowlActivityEntity {

  @Field
  private Boolean boom;
  @Field
  private Boolean ching;
  @Field
  private String fightingSign;
  @Field
  private String grubbing;
  @Field
  private String matingSign;
  @Field
  private Boolean skraak;
  @Field
  private String sticks;
  @Field
  private String tapeUsed;
  @Field
  private String trackActivity;

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

  public String getFightingSign() {
    return fightingSign;
  }

  public void setFightingSign(String fightingSign) {
    this.fightingSign = fightingSign;
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

  public String getTapeUsed() {
    return tapeUsed;
  }

  public void setTapeUsed(String tapeUsed) {
    this.tapeUsed = tapeUsed;
  }

  public String getTrackActivity() {
    return trackActivity;
  }

  public void setTrackActivity(String trackActivity) {
    this.trackActivity = trackActivity;
  }

}
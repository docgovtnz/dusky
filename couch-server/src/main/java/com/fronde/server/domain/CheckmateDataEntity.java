package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class CheckmateDataEntity {

  @Field
  private String recoveryid;
  @Field
  private Integer femaleTx1;
  @Field
  private Integer femaleTx2;
  @Field
  private Integer femaleTx;
  @Field
  private Integer time1;
  @Field
  private Integer time2;
  @Field
  private Date time;
  @Field
  private Integer duration1;
  @Field
  private Integer duration2;
  @Field
  private Integer duration;
  @Field
  private Integer quality1;
  @Field
  private Integer quality2;
  @Field
  private Integer quality;
  @Field
  private String birdId;
  @Field
  private Integer errolLoc;

  public String getRecoveryid() {
    return recoveryid;
  }

  public void setRecoveryid(String recoveryid) {
    this.recoveryid = recoveryid;
  }

  public Integer getFemaleTx1() {
    return femaleTx1;
  }

  public void setFemaleTx1(Integer femaleTx1) {
    this.femaleTx1 = femaleTx1;
  }

  public Integer getFemaleTx2() {
    return femaleTx2;
  }

  public void setFemaleTx2(Integer femaleTx2) {
    this.femaleTx2 = femaleTx2;
  }

  public Integer getFemaleTx() {
    return femaleTx;
  }

  public void setFemaleTx(Integer femaleTx) {
    this.femaleTx = femaleTx;
  }

  public Integer getTime1() {
    return time1;
  }

  public void setTime1(Integer time1) {
    this.time1 = time1;
  }

  public Integer getTime2() {
    return time2;
  }

  public void setTime2(Integer time2) {
    this.time2 = time2;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public Integer getDuration1() {
    return duration1;
  }

  public void setDuration1(Integer duration1) {
    this.duration1 = duration1;
  }

  public Integer getDuration2() {
    return duration2;
  }

  public void setDuration2(Integer duration2) {
    this.duration2 = duration2;
  }

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public Integer getQuality1() {
    return quality1;
  }

  public void setQuality1(Integer quality1) {
    this.quality1 = quality1;
  }

  public Integer getQuality2() {
    return quality2;
  }

  public void setQuality2(Integer quality2) {
    this.quality2 = quality2;
  }

  public Integer getQuality() {
    return quality;
  }

  public void setQuality(Integer quality) {
    this.quality = quality;
  }

  public String getBirdId() {
    return birdId;
  }

  public void setBirdId(String birdId) {
    this.birdId = birdId;
  }

  public Integer getErrolLoc() {
    return errolLoc;
  }

  public void setErrolLoc(Integer errolLoc) {
    this.errolLoc = errolLoc;
  }

}
package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class MeasureDetailEntity {

  @Field
  private Float culmenAndCere;
  @Field
  private Float culmenLength;
  @Field
  private Float culmenWidth;
  @Field
  private Integer femur;
  @Field
  private String leg;
  @Field
  private Float longestToe;
  @Field
  private Float longToeClaw;
  @Field
  private String recoveryID;
  @Field
  private Integer sternumshoulder;
  @Field
  private Float tailLength;
  @Field
  private Float tarsusDepthSquashed;
  @Field
  private Float tarsusDepthUnsquashed;
  @Field
  private Float tarsusLength;
  @Field
  private Float tarsusWidthSquashed;
  @Field
  private Float tarsusWidthUnsquashed;
  @Field
  private Float wingLength;

  public Float getCulmenAndCere() {
    return culmenAndCere;
  }

  public void setCulmenAndCere(Float culmenAndCere) {
    this.culmenAndCere = culmenAndCere;
  }

  public Float getCulmenLength() {
    return culmenLength;
  }

  public void setCulmenLength(Float culmenLength) {
    this.culmenLength = culmenLength;
  }

  public Float getCulmenWidth() {
    return culmenWidth;
  }

  public void setCulmenWidth(Float culmenWidth) {
    this.culmenWidth = culmenWidth;
  }

  public Integer getFemur() {
    return femur;
  }

  public void setFemur(Integer femur) {
    this.femur = femur;
  }

  public String getLeg() {
    return leg;
  }

  public void setLeg(String leg) {
    this.leg = leg;
  }

  public Float getLongestToe() {
    return longestToe;
  }

  public void setLongestToe(Float longestToe) {
    this.longestToe = longestToe;
  }

  public Float getLongToeClaw() {
    return longToeClaw;
  }

  public void setLongToeClaw(Float longToeClaw) {
    this.longToeClaw = longToeClaw;
  }

  public String getRecoveryID() {
    return recoveryID;
  }

  public void setRecoveryID(String recoveryID) {
    this.recoveryID = recoveryID;
  }

  public Integer getSternumshoulder() {
    return sternumshoulder;
  }

  public void setSternumshoulder(Integer sternumshoulder) {
    this.sternumshoulder = sternumshoulder;
  }

  public Float getTailLength() {
    return tailLength;
  }

  public void setTailLength(Float tailLength) {
    this.tailLength = tailLength;
  }

  public Float getTarsusDepthSquashed() {
    return tarsusDepthSquashed;
  }

  public void setTarsusDepthSquashed(Float tarsusDepthSquashed) {
    this.tarsusDepthSquashed = tarsusDepthSquashed;
  }

  public Float getTarsusDepthUnsquashed() {
    return tarsusDepthUnsquashed;
  }

  public void setTarsusDepthUnsquashed(Float tarsusDepthUnsquashed) {
    this.tarsusDepthUnsquashed = tarsusDepthUnsquashed;
  }

  public Float getTarsusLength() {
    return tarsusLength;
  }

  public void setTarsusLength(Float tarsusLength) {
    this.tarsusLength = tarsusLength;
  }

  public Float getTarsusWidthSquashed() {
    return tarsusWidthSquashed;
  }

  public void setTarsusWidthSquashed(Float tarsusWidthSquashed) {
    this.tarsusWidthSquashed = tarsusWidthSquashed;
  }

  public Float getTarsusWidthUnsquashed() {
    return tarsusWidthUnsquashed;
  }

  public void setTarsusWidthUnsquashed(Float tarsusWidthUnsquashed) {
    this.tarsusWidthUnsquashed = tarsusWidthUnsquashed;
  }

  public Float getWingLength() {
    return wingLength;
  }

  public void setWingLength(Float wingLength) {
    this.wingLength = wingLength;
  }

}
package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class CaptureDetailEntity {

  @Field
  private String captureMethod;
  @Field
  private Float duration;
  @Field
  private Boolean eyesClosed;
  @Field
  private Boolean headDown;
  @Field
  private String oldHolder;
  @Field
  private Integer oldReaction;
  @Field
  private String oldRecorder1;
  @Field
  private String oldRecorder2;
  @Field
  private String reaction;
  @Field
  private String recoveryID;
  @Field
  private String response;
  @Field
  private Boolean shivered;

  public String getCaptureMethod() {
    return captureMethod;
  }

  public void setCaptureMethod(String captureMethod) {
    this.captureMethod = captureMethod;
  }

  public Float getDuration() {
    return duration;
  }

  public void setDuration(Float duration) {
    this.duration = duration;
  }

  public Boolean getEyesClosed() {
    return eyesClosed;
  }

  public void setEyesClosed(Boolean eyesClosed) {
    this.eyesClosed = eyesClosed;
  }

  public Boolean getHeadDown() {
    return headDown;
  }

  public void setHeadDown(Boolean headDown) {
    this.headDown = headDown;
  }

  public String getOldHolder() {
    return oldHolder;
  }

  public void setOldHolder(String oldHolder) {
    this.oldHolder = oldHolder;
  }

  public Integer getOldReaction() {
    return oldReaction;
  }

  public void setOldReaction(Integer oldReaction) {
    this.oldReaction = oldReaction;
  }

  public String getOldRecorder1() {
    return oldRecorder1;
  }

  public void setOldRecorder1(String oldRecorder1) {
    this.oldRecorder1 = oldRecorder1;
  }

  public String getOldRecorder2() {
    return oldRecorder2;
  }

  public void setOldRecorder2(String oldRecorder2) {
    this.oldRecorder2 = oldRecorder2;
  }

  public String getReaction() {
    return reaction;
  }

  public void setReaction(String reaction) {
    this.reaction = reaction;
  }

  public String getRecoveryID() {
    return recoveryID;
  }

  public void setRecoveryID(String recoveryID) {
    this.recoveryID = recoveryID;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public Boolean getShivered() {
    return shivered;
  }

  public void setShivered(Boolean shivered) {
    this.shivered = shivered;
  }

}
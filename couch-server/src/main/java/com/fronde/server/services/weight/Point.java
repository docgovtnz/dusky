package com.fronde.server.services.weight;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Point {

  private float x;
  private float y;

  private String recordId;
  private String cropStatus;

  public Point() {
  }

  public Point(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public Point(float x, float y, String recordId, String cropStatus) {
    this(x, y);
    this.recordId = recordId;
    this.cropStatus = cropStatus;
  }

  public float getX() {
    return x;
  }

  public void setX(float x) {
    this.x = x;
  }

  public float getY() {
    return y;
  }

  public void setY(float y) {
    this.y = y;
  }

  public String getRecordId() {
    return recordId;
  }

  public void setRecordId(String recordId) {
    this.recordId = recordId;
  }

  public String getCropStatus() {
    return cropStatus;
  }

  public void setCropStatus(String cropStatus) {
    this.cropStatus = cropStatus;
  }
}

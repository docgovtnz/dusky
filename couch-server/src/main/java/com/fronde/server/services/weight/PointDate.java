package com.fronde.server.services.weight;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PointDate {

  private Date x;
  private float y;

  public PointDate() {
  }

  public PointDate(Date x, float y) {
    this.x = x;
    this.y = y;
  }

  public Date getX() {
    return x;
  }

  public void setX(Date x) {
    this.x = x;
  }

  public float getY() {
    return y;
  }

  public void setY(float y) {
    this.y = y;
  }

}

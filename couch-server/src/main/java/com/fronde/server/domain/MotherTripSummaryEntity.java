package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class MotherTripSummaryEntity {

  @Field
  private Integer totalTimeOff;
  @Field
  private Integer numberOfTimesOff;
  @Field
  private Integer maxTimeOff;
  @Field
  private Date firstTimeOff;

  public Integer getTotalTimeOff() {
    return totalTimeOff;
  }

  public void setTotalTimeOff(Integer totalTimeOff) {
    this.totalTimeOff = totalTimeOff;
  }

  public Integer getNumberOfTimesOff() {
    return numberOfTimesOff;
  }

  public void setNumberOfTimesOff(Integer numberOfTimesOff) {
    this.numberOfTimesOff = numberOfTimesOff;
  }

  public Integer getMaxTimeOff() {
    return maxTimeOff;
  }

  public void setMaxTimeOff(Integer maxTimeOff) {
    this.maxTimeOff = maxTimeOff;
  }

  public Date getFirstTimeOff() {
    return firstTimeOff;
  }

  public void setFirstTimeOff(Date firstTimeOff) {
    this.firstTimeOff = firstTimeOff;
  }

}
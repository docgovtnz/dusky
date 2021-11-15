package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class MedicationEntity {

  @Field
  private String concentrationUnits;
  @Field
  private Float concentrationValue;
  @Field
  private Integer courseLength;
  @Field
  private Integer dayNumber;
  @Field
  private String doseRateUnits;
  @Field
  private Float doseRateValue;
  @Field
  private String drug;
  @Field
  private String recoveryid;
  @Field
  private String route;
  @Field
  private String timesPerDay;

  public String getConcentrationUnits() {
    return concentrationUnits;
  }

  public void setConcentrationUnits(String concentrationUnits) {
    this.concentrationUnits = concentrationUnits;
  }

  public Float getConcentrationValue() {
    return concentrationValue;
  }

  public void setConcentrationValue(Float concentrationValue) {
    this.concentrationValue = concentrationValue;
  }

  public Integer getCourseLength() {
    return courseLength;
  }

  public void setCourseLength(Integer courseLength) {
    this.courseLength = courseLength;
  }

  public Integer getDayNumber() {
    return dayNumber;
  }

  public void setDayNumber(Integer dayNumber) {
    this.dayNumber = dayNumber;
  }

  public String getDoseRateUnits() {
    return doseRateUnits;
  }

  public void setDoseRateUnits(String doseRateUnits) {
    this.doseRateUnits = doseRateUnits;
  }

  public Float getDoseRateValue() {
    return doseRateValue;
  }

  public void setDoseRateValue(Float doseRateValue) {
    this.doseRateValue = doseRateValue;
  }

  public String getDrug() {
    return drug;
  }

  public void setDrug(String drug) {
    this.drug = drug;
  }

  public String getRecoveryid() {
    return recoveryid;
  }

  public void setRecoveryid(String recoveryid) {
    this.recoveryid = recoveryid;
  }

  public String getRoute() {
    return route;
  }

  public void setRoute(String route) {
    this.route = route;
  }

  public String getTimesPerDay() {
    return timesPerDay;
  }

  public void setTimesPerDay(String timesPerDay) {
    this.timesPerDay = timesPerDay;
  }

}
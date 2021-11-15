package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.List;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class CheckmateEntity {

  @Field
  private Integer battery1;
  @Field
  private Integer battery2;
  @Field
  private Integer last24hourActivity;
  @Field
  private Integer last24hourActivity1;
  @Field
  private Integer last24hourActivity2;
  @Field
  private String dataCaptureType;
  @Field
  private String pulseRate;
  @Field
  private String recoveryid;
  @Field
  private List<CheckmateDataEntity> checkmateDataList;

  public Integer getBattery1() {
    return battery1;
  }

  public void setBattery1(Integer battery1) {
    this.battery1 = battery1;
  }

  public Integer getBattery2() {
    return battery2;
  }

  public void setBattery2(Integer battery2) {
    this.battery2 = battery2;
  }

  public Integer getLast24hourActivity() {
    return last24hourActivity;
  }

  public void setLast24hourActivity(Integer last24hourActivity) {
    this.last24hourActivity = last24hourActivity;
  }

  public Integer getLast24hourActivity1() {
    return last24hourActivity1;
  }

  public void setLast24hourActivity1(Integer last24hourActivity1) {
    this.last24hourActivity1 = last24hourActivity1;
  }

  public Integer getLast24hourActivity2() {
    return last24hourActivity2;
  }

  public void setLast24hourActivity2(Integer last24hourActivity2) {
    this.last24hourActivity2 = last24hourActivity2;
  }

  public String getDataCaptureType() {
    return dataCaptureType;
  }

  public void setDataCaptureType(String dataCaptureType) {
    this.dataCaptureType = dataCaptureType;
  }

  public String getPulseRate() {
    return pulseRate;
  }

  public void setPulseRate(String pulseRate) {
    this.pulseRate = pulseRate;
  }

  public String getRecoveryid() {
    return recoveryid;
  }

  public void setRecoveryid(String recoveryid) {
    this.recoveryid = recoveryid;
  }

  public List<CheckmateDataEntity> getCheckmateDataList() {
    return checkmateDataList;
  }

  public void setCheckmateDataList(List<CheckmateDataEntity> checkmateDataList) {
    this.checkmateDataList = checkmateDataList;
  }

}
package com.fronde.server.services.snarkimport.reader;

public class SnarkFileEggTimerRecord extends SnarkFileRecord {

  private int id;
  private int incubateFlag;
  private int days;
  private int activityYesterday;
  private int activityLongTerm;

  public SnarkFileEggTimerRecord(int id, int incubateFlag, int days, int activityYesterday,
      int activityLongTerm) {
    this.recordType = RECORD_TYPE_EGG_TIMER;
    this.id = id;
    this.incubateFlag = incubateFlag;
    this.days = days;
    this.activityYesterday = activityYesterday;
    this.activityLongTerm = activityLongTerm;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getIncubateFlag() {
    return incubateFlag;
  }

  public void setIncubateFlag(int incubateFlag) {
    this.incubateFlag = incubateFlag;
  }

  public int getDays() {
    return days;
  }

  public void setDays(int days) {
    this.days = days;
  }

  public int getActivityYesterday() {
    return activityYesterday;
  }

  public void setActivityYesterday(int activityYesterday) {
    this.activityYesterday = activityYesterday;
  }

  public int getActivityLongTerm() {
    return activityLongTerm;
  }

  public void setActivityLongTerm(int activityLongTerm) {
    this.activityLongTerm = activityLongTerm;
  }

  @Override
  public String toString() {
    return "SnarkFileEggTimerRecord{" +
        "id=" + id +
        ", incubateFlag=" + incubateFlag +
        ", days=" + days +
        ", activityYesterday=" + activityYesterday +
        ", activityLongTerm=" + activityLongTerm +
        '}';
  }

}

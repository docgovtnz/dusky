package com.fronde.server.services.checksheetreport;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fronde.server.domain.criteria.AbstractCriteria;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public class ChecksheetReportCriteria extends AbstractCriteria {

  private String island;
  private String sex;
  // assume dates in all criteria objects are dates and not date times
  @DateTimeFormat(iso = ISO.DATE_TIME)
  private Date fromDate;
  // assume dates in all criteria objects are dates and not date times
  @DateTimeFormat(iso = ISO.DATE_TIME)
  private Date toDate;
  private Integer numDays;

  public String getIsland() {
    return island;
  }

  public void setIsland(String island) {
    this.island = island;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public Date getFromDate() {
    return fromDate;
  }

  public void setFromDate(Date fromDate) {
    this.fromDate = fromDate;
  }

  public Date getToDate() {
    return toDate;
  }

  public void setToDate(Date toDate) {
    this.toDate = toDate;
  }

  public Integer getNumDays() {
    return numDays;
  }

  public void setNumDays(Integer numDays) {
    this.numDays = numDays;
  }

}

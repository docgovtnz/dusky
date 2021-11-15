package com.fronde.server.services.matingreport;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fronde.server.domain.criteria.AbstractCriteria;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public class MatingReportCriteria extends AbstractCriteria {

  private String island;
  // assume dates in all criteria objects are dates and not date times
  @DateTimeFormat(iso = ISO.DATE_TIME)
  private Date fromDate;
  // assume dates in all criteria objects are dates and not date times
  @DateTimeFormat(iso = ISO.DATE_TIME)
  private Date toDate;
  private String birdID;
  private Integer minimumQuality;

  public String getIsland() {
    return island;
  }

  public void setIsland(String island) {
    this.island = island;
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

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public Integer getMinimumQuality() {
    return minimumQuality;
  }

  public void setMinimumQuality(Integer minimumQuality) {
    this.minimumQuality = minimumQuality;
  }
}

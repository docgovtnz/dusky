package com.fronde.server.services.bird;

import java.util.Date;

public class BirdLocationCriteria {

  private String island;
  private Date queryDate;
  private Date queryToDate;

  public BirdLocationCriteria() {
  }

  public String getIsland() {
    return island;
  }

  public void setIsland(String island) {
    this.island = island;
  }

  public Date getQueryDate() {
    return queryDate;
  }

  public void setQueryDate(Date queryDate) {
    this.queryDate = queryDate;
  }

  public Date getQueryToDate() { return queryToDate; }

  public void setQueryToDate(Date queryToDate) { this.queryToDate = queryToDate; }
}

package com.fronde.server.services.noranet;

import java.util.List;

public class NoraNetSearchUndetectedDTO {

  private String birdID;
  private String birdName;
  private List<Integer> uhfIdList;

  public NoraNetSearchUndetectedDTO() {
  }

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public String getBirdName() {
    return birdName;
  }

  public void setBirdName(String birdName) {
    this.birdName = birdName;
  }

  public List<Integer> getUhfIdList() { return uhfIdList; }

  public void setUhfIdList(List<Integer> uhfIdList) { this.uhfIdList = uhfIdList; }

}

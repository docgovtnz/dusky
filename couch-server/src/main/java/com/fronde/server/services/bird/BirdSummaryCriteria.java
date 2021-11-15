package com.fronde.server.services.bird;

import java.util.List;

public class BirdSummaryCriteria {

  private List<String> birdIDList;

  public BirdSummaryCriteria() {
  }

  public List<String> getBirdIDList() {
    return birdIDList;
  }

  public void setBirdIDList(List<String> birdIDList) {
    this.birdIDList = birdIDList;
  }
}

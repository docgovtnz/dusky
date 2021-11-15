package com.fronde.server.services.sample;

import java.util.ArrayList;
import java.util.List;

public class RanksRequest {

  private String sampleType;
  private List<ResultDTO> results = new ArrayList<>();

  public String getSampleType() {
    return sampleType;
  }

  public void setSampleType(String sampleType) {
    this.sampleType = sampleType;
  }

  public List<ResultDTO> getResults() {
    return results;
  }

  public void setResults(List<ResultDTO> results) {
    this.results = results;
  }

}

package com.fronde.server.services.checksheetreport;

import java.util.HashMap;
import java.util.Map;

public class ChecksheetRecord {

  private String birdID;
  private String birdName;
  private Map<String, String> interactionsByDate = new HashMap<>();

  public ChecksheetRecord() {
  }

  public ChecksheetRecord(String birdID, String birdName) {
    this.birdID = birdID;
    this.birdName = birdName;
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

  public Map<String, String> getInteractionsByDate() {
    return interactionsByDate;
  }

  public void setInteractionsByDate(Map<String, String> interactionsByDate) {
    this.interactionsByDate = interactionsByDate;
  }
}

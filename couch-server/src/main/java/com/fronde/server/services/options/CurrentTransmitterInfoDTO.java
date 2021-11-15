package com.fronde.server.services.options;

import java.util.Date;

public class CurrentTransmitterInfoDTO {

  private String docId;
  private Date dateTime;

  public String getDocId() {
    return docId;
  }

  public void setDocId(String docId) {
    this.docId = docId;
  }

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }
}

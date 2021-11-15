package com.fronde.server.services.checksheetreport.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class ChecksheetElement {

  @XmlAttribute
  private String recordType;

  @XmlAttribute
  private String reason;

  @XmlAttribute
  private String checksheetSymbol;


  public String getRecordType() {
    return recordType;
  }

  public void setRecordType(String recordType) {
    this.recordType = recordType;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public String getChecksheetSymbol() {
    return checksheetSymbol;
  }

  public void setChecksheetSymbol(String checksheetSymbol) {
    this.checksheetSymbol = checksheetSymbol;
  }

}

package com.fronde.server.services.checksheetreport;

import java.util.List;

public class ChecksheetReport {

  private String[] dateColumns;

  public List<ChecksheetRecord> getRecords() {
    return records;
  }

  public void setRecords(List<ChecksheetRecord> records) {
    this.records = records;
  }

  private List<ChecksheetRecord> records;

  public String[] getDateColumns() {
    return dateColumns;
  }

  public void setDateColumns(String[] dateColumns) {
    this.dateColumns = dateColumns;
  }
}

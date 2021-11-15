package com.fronde.server.services.noraneterror;

import java.util.Date;

public class NoraNetErrorReportDTO {

  private String id;
  private Date dateProcessed;
  private String fileName;
  private String fileData;
  private String message;
  private boolean dataImported;
  private boolean actioned;

  public NoraNetErrorReportDTO() {
  }

  public String getId() { return id; }

  public void setId(String id) { this.id = id; }

  public Date getDateProcessed() { return dateProcessed; }

  public void setDateProcessed(Date dateProcessed) { this.dateProcessed = dateProcessed; }

  public String getFileName() { return fileName; }

  public void setFileName(String fileName) { this.fileName = fileName; }

  public String getFileData() { return fileData; }

  public void setFileData(String fileData) { this.fileData = fileData; }

  public String getMessage() { return message; }

  public void setMessage(String message) { this.message = message; }

  public boolean isDataImported() { return dataImported; }

  public void setDataImported(boolean dataImported) { this.dataImported = dataImported; }

  public boolean isActioned() { return actioned; }

  public void setActioned(boolean actioned) { this.actioned = actioned; }
}

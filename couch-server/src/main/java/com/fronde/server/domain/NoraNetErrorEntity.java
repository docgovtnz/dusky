package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;
import java.util.Date;

@Document
public class NoraNetErrorEntity extends BaseEntity {

  @Field
  private String fileName;
  @Field
  private String fileData;
  @Field
  private Date dateProcessed;
  @Field
  private String message;
  @Field
  private Boolean dataImported;
  @Field
  private Boolean actioned;

  public String getFileName() { return fileName; }

  public void setFileName(String fileName) { this.fileName = fileName; }

  public String getFileData() { return fileData; }

  public void setFileData(String fileData) { this.fileData = fileData; }

  public Date getDateProcessed() { return dateProcessed; }

  public void setDateProcessed(Date dateProcessed) { this.dateProcessed = dateProcessed; }

  public String getMessage() { return message; }

  public void setMessage(String message) { this.message = message; }

  public Boolean getDataImported() { return dataImported; }

  public void setDataImported(Boolean dataImported) { this.dataImported = dataImported; }

  public Boolean getActioned() { return actioned; }

  public void setActioned(Boolean actioned) { this.actioned = actioned; }

}

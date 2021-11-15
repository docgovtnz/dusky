package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;
import java.util.Date;

/**
 *
 */
public abstract class BaseEntity {

  @Id
  private String id;
  @Field
  private String docType;
  @Field
  private Date modifiedTime;
  @Field
  private String modifiedByPersonId;
  @Field
  private String revision;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDocType() {
    return docType;
  }

  public void setDocType(String docType) {
    this.docType = docType;
  }

  public Date getModifiedTime() {
    return modifiedTime;
  }

  public void setModifiedTime(Date modifiedTime) {
    this.modifiedTime = modifiedTime;
  }

  public String getModifiedByPersonId() {
    return modifiedByPersonId;
  }

  public void setModifiedByPersonId(String modifiedByPersonId) {
    this.modifiedByPersonId = modifiedByPersonId;
  }

  public String getRevision() {
    return revision;
  }

  public void setRevision(String revision) {
    this.revision = revision;
  }
}

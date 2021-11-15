package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;
import com.fronde.server.services.common.ObjectIdFactory;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.mapping.Document;

/**
 *
 */
@Document
public class RevisionList {

  @Id
  private String id;
  @Field
  private String docType;
  @Field
  private String revision;

  @Field
  private String entityId;
  @Field
  private String entityDocType;

  @Field
  private List<BaseEntity> revisions = new ArrayList<>();

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  public RevisionList() {
    // Empty constructor needed by spring-couchbase-data
  }

  public RevisionList(BaseEntity entity) {
    if (entity.getId() == null) {
      throw new NullPointerException(
          "entityId must not be null for " + entity.getClass().getName());
    }

    if (entity.getDocType() == null) {
      throw new NullPointerException("docType must not be null for " + entity.getClass().getName());
    }

    this.entityId = entity.getId();
    this.entityDocType = entity.getDocType();

    this.id = objectIdFactory.create();
    this.docType = "RevisionList";
    this.revisions.add(entity);
  }

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

  public String getRevision() {
    return revision;
  }

  public void setRevision(String revision) {
    this.revision = revision;
  }

  public String getEntityId() {
    return entityId;
  }

  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }

  public String getEntityDocType() {
    return entityDocType;
  }

  public void setEntityDocType(String entityDocType) {
    this.entityDocType = entityDocType;
  }

  public List<BaseEntity> getRevisions() {
    return revisions;
  }

  public void setRevisions(List<BaseEntity> revisions) {
    this.revisions = revisions;
  }
}

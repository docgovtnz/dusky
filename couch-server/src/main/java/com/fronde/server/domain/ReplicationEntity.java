package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;

public class ReplicationEntity extends BaseEntity {

  public static final String UNIQUE_ID = "REPLICATION_ENTITY";

  @Field
  private String bucketIdentifier;

  public String getBucketIdentifier() {
    return bucketIdentifier;
  }

  public void setBucketIdentifier(String bucketIdentifier) {
    this.bucketIdentifier = bucketIdentifier;
  }
}

package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class NoraNetBirdEntity {

  @Field
  private String birdID;

  public String getBirdID() { return birdID; }

  public void setBirdID(String birdID) { this.birdID = birdID; }

}


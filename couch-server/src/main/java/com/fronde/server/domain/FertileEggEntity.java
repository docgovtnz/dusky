package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class FertileEggEntity extends BaseEntity {

  @Field
  private String birdID;
  @Field
  private Boolean definiteFather;
  @Field
  private String father;

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public Boolean getDefiniteFather() {
    return definiteFather;
  }

  public void setDefiniteFather(Boolean definiteFather) {
    this.definiteFather = definiteFather;
  }

  public String getFather() {
    return father;
  }

  public void setFather(String father) {
    this.father = father;
  }

}
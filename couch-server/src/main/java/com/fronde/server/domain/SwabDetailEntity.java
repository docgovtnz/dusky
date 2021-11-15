package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class SwabDetailEntity {

  @Field
  private String swabSite;
  @Field
  private Integer quantity;

  public String getSwabSite() {
    return swabSite;
  }

  public void setSwabSite(String swabSite) {
    this.swabSite = swabSite;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

}
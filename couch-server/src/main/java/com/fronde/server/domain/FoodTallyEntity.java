package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class FoodTallyEntity {

  @Field
  private String name;
  @Field
  private Integer in;
  @Field
  private Integer out;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getIn() {
    return in;
  }

  public void setIn(Integer in) {
    this.in = in;
  }

  public Integer getOut() {
    return out;
  }

  public void setOut(Integer out) {
    this.out = out;
  }

}
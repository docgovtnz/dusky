package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class HarnessChangeEntity {

  @Field
  private Integer newHarnessLength;
  @Field
  private Integer oldHarnessLengthRight;
  @Field
  private Integer oldHarnessLengthLeft;
  @Field
  private String txtranstype;

  public Integer getNewHarnessLength() {
    return newHarnessLength;
  }

  public void setNewHarnessLength(Integer newHarnessLength) {
    this.newHarnessLength = newHarnessLength;
  }

  public Integer getOldHarnessLengthRight() {
    return oldHarnessLengthRight;
  }

  public void setOldHarnessLengthRight(Integer oldHarnessLengthRight) {
    this.oldHarnessLengthRight = oldHarnessLengthRight;
  }

  public Integer getOldHarnessLengthLeft() {
    return oldHarnessLengthLeft;
  }

  public void setOldHarnessLengthLeft(Integer oldHarnessLengthLeft) {
    this.oldHarnessLengthLeft = oldHarnessLengthLeft;
  }

  public String getTxtranstype() {
    return txtranstype;
  }

  public void setTxtranstype(String txtranstype) {
    this.txtranstype = txtranstype;
  }

}
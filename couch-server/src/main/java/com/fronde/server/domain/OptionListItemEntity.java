package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;
import java.util.List;

@Document
public class OptionListItemEntity {

  @Field
  private String text;
  @Field
  private List<String> optionListDisplayList;

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public List<String> getOptionListDisplayList() {
    return optionListDisplayList;
  }

  public void setOptionListDisplayList(List<String> optionListDisplayList) {
    this.optionListDisplayList = optionListDisplayList;
  }
}
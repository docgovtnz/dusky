package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.List;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class OptionListEntity extends BaseEntity {

  @Field
  private String name;
  @Field
  private List<OptionListItemEntity> optionListItemList;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<OptionListItemEntity> getOptionListItemList() {
    return optionListItemList;
  }

  public void setOptionListItemList(List<OptionListItemEntity> optionListItemList) {
    this.optionListItemList = optionListItemList;
  }

}
package com.fronde.server.services.optionlist;

import com.fronde.server.domain.criteria.AbstractCriteria;

public class OptionListCriteria extends AbstractCriteria {

  private String optionListName;
  private String optionListText;

  public String getOptionListName() { return optionListName; }

  public void setOptionListName(String optionListName) { this.optionListName = optionListName; }

  public String getOptionListText() { return optionListText; }

  public void setOptionListText(String optionListText) { this.optionListText = optionListText; }
}

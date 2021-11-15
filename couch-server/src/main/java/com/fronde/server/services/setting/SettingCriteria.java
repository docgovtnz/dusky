package com.fronde.server.services.setting;

import com.fronde.server.domain.criteria.AbstractCriteria;

public class SettingCriteria extends AbstractCriteria {

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}

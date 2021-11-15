package com.fronde.server.services.txmortality;

import com.fronde.server.domain.criteria.AbstractCriteria;

public class TxMortalityCriteria extends AbstractCriteria {

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}

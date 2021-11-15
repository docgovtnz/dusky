package com.fronde.server.services.island;

import com.fronde.server.domain.criteria.AbstractCriteria;

public class IslandCriteria extends AbstractCriteria {

  private String name;
  private Integer islandId;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getIslandId() {return islandId; }

  public void setIslandId(Integer islandId) { this.islandId = islandId; }
}

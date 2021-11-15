package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.IslandEntity;
import com.fronde.server.domain.LocationEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.services.island.IslandService;
import com.fronde.server.services.validation.ValidationAppContext;

public abstract class AbstractIslandRule extends AbstractRule {

  protected IslandEntity getIslandWithBounds(Object object) {
    IslandEntity island = getIsland(object);
    if (island == null || island.getLowerEasting() == null || island.getUpperEasting() == null
        || island.getLowerNorthing() == null || island.getUpperNorthing() == null) {
      return null;
    } else {
      return island;
    }

  }

  protected IslandEntity getIsland(Object object) {
    String islandName;
    if (object instanceof RecordEntity) {
      islandName = ((RecordEntity) object).getIsland();
    } else if (object instanceof LocationEntity) {
      islandName = ((LocationEntity) object).getIsland();
    } else {
      throw new RuntimeException(
          "Not able to use this Rule on an instance of " + object.getClass().getName()
              + " at this stage yet.");
    }

    return getIslandByName(islandName);
  }

  protected IslandEntity getIslandByName(String islandName) {
    IslandService islandService = ValidationAppContext.get().getBean(IslandService.class);
    IslandEntity island = islandService.findByName(islandName);
    return island;
  }
}

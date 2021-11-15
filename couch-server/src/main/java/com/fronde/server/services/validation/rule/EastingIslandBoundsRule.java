package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.IslandEntity;
import com.fronde.server.services.validation.ValidationResultFactory;

public class EastingIslandBoundsRule extends AbstractIslandRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {

    IslandEntity island = getIsland(object);
    if (island == null) {
      resultFactory.addResult("IslandNotFound");
    } else if (island.getLowerEasting() == null || island.getUpperEasting() == null
        || island.getLowerNorthing() == null || island.getUpperNorthing() == null) {
      resultFactory.addResult("IslandBoundsUndefined").with("island", island.getName());
    }
  }
}

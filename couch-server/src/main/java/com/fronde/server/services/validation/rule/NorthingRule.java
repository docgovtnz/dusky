package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.IslandEntity;
import com.fronde.server.services.validation.ValidationResultFactory;

public class NorthingRule extends AbstractIslandRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    IslandEntity island = getIslandWithBounds(object);

    if (island != null && propertyValue != null) {
      Float northing = (Float) propertyValue;
      if (northing < island.getLowerNorthing() || northing > island.getUpperNorthing()) {
        resultFactory.addResult("NotInRange");
      }
    }
  }
}

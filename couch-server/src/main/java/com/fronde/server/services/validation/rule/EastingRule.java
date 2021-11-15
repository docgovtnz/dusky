package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.IslandEntity;
import com.fronde.server.services.validation.ValidationResultFactory;

public class EastingRule extends AbstractIslandRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    IslandEntity island = getIslandWithBounds(object);

    if (island != null && propertyValue != null) {
      Float easting = (Float) propertyValue;
      if (easting < island.getLowerEasting() || easting > island.getUpperEasting()) {
        resultFactory.addResult("NotInRange");
      }
    }
  }
}

package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.IslandEntity;
import com.fronde.server.services.island.IslandService;
import com.fronde.server.services.validation.ValidationAppContext;
import com.fronde.server.services.validation.ValidationResultFactory;

public class IslandMustExistRule extends AbstractRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    // if island is null, let it be caught by the required rule for island instead of adding additional validation errors
    if (propertyValue != null) {
      // check island exists in Dusky
      IslandService islandService = ValidationAppContext.get().getBean(IslandService.class);
      IslandEntity islandEntity = islandService.findByName(propertyValue.toString());
      if (islandEntity == null) {
        resultFactory.addResult("IslandMustExist");
      }
    }
  }

}

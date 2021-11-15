package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.IslandEntity;
import com.fronde.server.services.island.IslandService;
import com.fronde.server.services.validation.ValidationAppContext;
import com.fronde.server.services.validation.ValidationResultFactory;

public class IslandUniqueNameRule extends AbstractRule<String> {

  @Override
  public void validate(Object object, String propertyValue,
      ValidationResultFactory resultFactory) {
    IslandEntity island = (IslandEntity) object;

    // if we are validating a new island, we will still have a IslandEntity object, just that island.id will be null

    if (!isUniqueIslandName(island.getId(), propertyValue)) {
      resultFactory.addResult("IslandNameNotUnique");
    }
  }

  protected boolean isUniqueIslandName(String metaID, String islandName) {
    IslandService service = ValidationAppContext.get().getBean(IslandService.class);
    return service.isUniqueIslandName(metaID, islandName);
  }

}

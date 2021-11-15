package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.IslandEntity;
import com.fronde.server.services.island.IslandService;
import com.fronde.server.services.validation.ValidationAppContext;
import com.fronde.server.services.validation.ValidationResultFactory;

public class IslandUniqueIdRule extends AbstractRule<Integer> {

  @Override
  public void validate(Object object, Integer propertyValue,
      ValidationResultFactory resultFactory) {
    IslandEntity island = (IslandEntity) object;

    // if we are validating a new island, we will still have a IslandEntity object, just that island.id will be null

    if (!isUniqueIslandId(island.getId(), propertyValue)) {
      resultFactory.addResult("IslandIdNotUnique");
    }
  }

  protected boolean isUniqueIslandId(String metaID, Integer islandId) {
    IslandService service = ValidationAppContext.get().getBean(IslandService.class);
    return service.isUniqueIslandId(metaID, islandId);
  }

}

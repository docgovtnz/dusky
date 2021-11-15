package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.services.bird.BirdService;
import com.fronde.server.services.validation.ValidationAppContext;
import com.fronde.server.services.validation.ValidationResultFactory;
import org.springframework.util.StringUtils;

public class BirdUniqueHouseIDRule extends AbstractRule<String> {

  @Override
  public void validate(Object object, String propertyValue, ValidationResultFactory resultFactory) {
    BirdEntity r = (BirdEntity) object;

    // if we are validating a new bird, we will still have a BirdEntity object, just that r.id will be null

    // we don't care when the property value is empty (should be caught by a mandatory rule if required)

    if (!StringUtils.isEmpty(propertyValue) && !isUniqueHouseID(r.getId(), propertyValue)) {
      resultFactory.addResult("BirdHouseIDNotUnique");
    }
  }

  protected boolean isUniqueHouseID(String birdID, String houseID) {
    BirdService service = ValidationAppContext.get().getBean(BirdService.class);
    return service.isUniqueHouseID(birdID, houseID);
  }

}

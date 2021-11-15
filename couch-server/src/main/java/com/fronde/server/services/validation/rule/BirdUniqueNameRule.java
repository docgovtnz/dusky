package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.services.bird.BirdService;
import com.fronde.server.services.validation.ValidationAppContext;
import com.fronde.server.services.validation.ValidationResultFactory;
import org.springframework.util.StringUtils;

public class BirdUniqueNameRule extends AbstractRule<String> {

  @Override
  public void validate(Object object, String propertyValue, ValidationResultFactory resultFactory) {
    BirdEntity r = (BirdEntity) object;

    // if we are validating a new bird, we will still have a BirdEntity object, just that r.id will be null

    // we don't care when the property value is empty (should be caught by a mandatory rule if required)

    if (!StringUtils.isEmpty(propertyValue) && !isUniqueName(r.getId(), propertyValue)) {
      resultFactory.addResult("BirdNameNotUnique");
    }
  }

  protected boolean isUniqueName(String birdID, String birdName) {
    BirdService service = ValidationAppContext.get().getBean(BirdService.class);
    return service.isUniqueName(birdID, birdName);
  }

}

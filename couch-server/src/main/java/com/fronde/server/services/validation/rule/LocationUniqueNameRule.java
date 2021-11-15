package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.LocationEntity;
import com.fronde.server.services.location.LocationService;
import com.fronde.server.services.validation.ValidationAppContext;
import com.fronde.server.services.validation.ValidationResultFactory;
import org.springframework.util.StringUtils;

public class LocationUniqueNameRule extends AbstractRule<String> {

  @Override
  public void validate(Object object, String propertyValue, ValidationResultFactory resultFactory) {
    LocationEntity r = (LocationEntity) object;

    // if we are validating a new location, we will still have a LocationEntity object, just that r.id will be null

    // we don't care when the property value is empty (should be caught by a mandatory rule if required)

    if (!StringUtils.isEmpty(propertyValue) && !isUniqueName(r.getId(), propertyValue)) {
      resultFactory.addResult("LocationNameNotUnique");
    }
  }

  protected boolean isUniqueName(String locationID, String locationName) {
    LocationService service = ValidationAppContext.get().getBean(LocationService.class);
    return service.isUniqueName(locationID, locationName);
  }

}

package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.LocationEntity;
import com.fronde.server.domain.NestDetailsEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import org.springframework.util.StringUtils;

public class NestTypeMandatoryClutchRule extends AbstractRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {

    LocationEntity location = (LocationEntity) object;
    String locationType = location.getLocationType();

    if (locationType != null && locationType.equals("Nest")) {

      NestDetailsEntity nestDetails = location.getNestDetails();
      if (nestDetails != null) {
        if (StringUtils.isEmpty(nestDetails.getClutch())) {
          resultFactory.addResult("ClutchIsMandatoryForNest").with("locationType", locationType);
        }
      }
    }
  }
}

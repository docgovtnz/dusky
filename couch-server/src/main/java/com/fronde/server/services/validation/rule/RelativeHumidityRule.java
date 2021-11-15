package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.NestObservationEntity;
import com.fronde.server.services.validation.ValidationResultFactory;

public class RelativeHumidityRule extends AbstractRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    NestObservationEntity nestEntity = (NestObservationEntity) object;
    if (nestEntity.getNestChamber() != null
        && nestEntity.getNestChamber().getRelativeHumidity() != null) {
      Integer humidity = nestEntity.getNestChamber().getRelativeHumidity();
        if (humidity < 0 || humidity > 100) {
          resultFactory.addResult("RelativeHumidityOutsideRange");
        }
    }
  }

}

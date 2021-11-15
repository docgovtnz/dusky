package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.LocationEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.services.validation.ValidationResultFactory;

public class EastingAndNorthingRule extends AbstractRule {


  protected Float getEasting(Object object) {
    Float easting;
    if (object instanceof RecordEntity) {
      easting = ((RecordEntity) object).getEasting();
    } else if (object instanceof LocationEntity) {
      easting = ((LocationEntity) object).getEasting();
    } else {
      throw new RuntimeException(
          "Not able to use this Rule on an instance of " + object.getClass().getName()
              + " at this stage yet.");
    }

    return easting;
  }

  protected Float getNorthing(Object object) {
    Float northing;
    if (object instanceof RecordEntity) {
      northing = ((RecordEntity) object).getNorthing();
    } else if (object instanceof LocationEntity) {
      northing = ((LocationEntity) object).getNorthing();
    } else {
      throw new RuntimeException(
          "Not able to use this Rule on an instance of " + object.getClass().getName()
              + " at this stage yet.");
    }

    return northing;
  }

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    Float easting = getEasting(object);
    Float northing = getNorthing(object);

    if (easting != null && northing == null) {
      resultFactory.addResult("IfOneThenTheOther");
    }

    if (easting == null && northing != null) {
      resultFactory.addResult("IfOneThenTheOther");
    }
  }
}

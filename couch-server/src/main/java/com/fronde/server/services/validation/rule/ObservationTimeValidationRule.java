package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.NestObservationEntity;
import com.fronde.server.services.validation.ValidationResultFactory;

public class ObservationTimeValidationRule extends AbstractRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    NestObservationEntity nestObservation = (NestObservationEntity) object;

    if (nestObservation.getObservationTimes() != null) {
      // Inspection time is required if finish time is set.
      if (nestObservation.getObservationTimes().getFinishObservationTime() != null) {
        validateRequired(resultFactory, nestObservation.getObservationTimes().getInspectionTime(),
            "Inspection Time");
      }

      // Finish time >= Inspection time
      if ((nestObservation.getObservationTimes().getFinishObservationTime() != null) &&
          (nestObservation.getObservationTimes().getInspectionTime() != null)) {
        if (nestObservation.getObservationTimes().getFinishObservationTime()
            .before(nestObservation.getObservationTimes().getInspectionTime())) {
          resultFactory.addResult("FinishTimeMustBeOnAfterInspectionTime");
        }
      }

      // Inspection time >= Start time
      if (nestObservation.getObservationTimes().getInspectionTime() != null) {
        if (nestObservation.getObservationTimes().getInspectionTime()
            .before(nestObservation.getDateTime())) {
          resultFactory.addResult("InspectionTimeMustBeOnAfterObservationTime");
        }
      }


    }

  }
}

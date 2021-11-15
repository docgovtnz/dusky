package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.MotherTripEntity;
import com.fronde.server.domain.NestObservationEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import java.util.ArrayList;
import java.util.List;

public class MotherTripListValidationRule extends AbstractRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    NestObservationEntity nest = (NestObservationEntity) object;

    if (nest.getMotherTripList() != null) {

      List<String> requiredMotherLeftRows = new ArrayList<>();
      List<String> requiredMotherBackRows = new ArrayList<>();
      List<String> motherLeftMustBeAfterNestObservationRows = new ArrayList<>();
      List<String> motherBackMustBeAfterMotherLeftRows = new ArrayList<>();

      // Check the items.
      for (int i = 0; i < nest.getMotherTripList().size(); i++) {
        int row = i + 1;

        MotherTripEntity motherTrip = nest.getMotherTripList().get(i);

        if (motherTrip.getMotherLeft() == null) {
          requiredMotherLeftRows.add("" + row);
        }

        if (motherTrip.getMotherBack() == null) {
          requiredMotherBackRows.add("" + row);
        }

        // Validate the dates make sense.
        if (motherTrip.getMotherLeft() != null && motherTrip.getMotherBack() != null) {

          // Left date must be on or after the observation date.
          if (motherTrip.getMotherLeft().before(nest.getDateTime())) {
            motherLeftMustBeAfterNestObservationRows.add("" + row);
          }
          // Left date must be on or after the observation date.
          if (motherTrip.getMotherBack().before(motherTrip.getMotherLeft())) {
            motherBackMustBeAfterMotherLeftRows.add("" + row);
          }
        }
      }

      if (!requiredMotherLeftRows.isEmpty()) {
        resultFactory.addResult("requiredForRow")
            .with("field", "Mother Left")
            .with("rows", requiredMotherLeftRows);
      }

      if (!requiredMotherBackRows.isEmpty()) {
        resultFactory.addResult("requiredForRow")
            .with("field", "Mother Back")
            .with("rows", requiredMotherBackRows);
      }

      if (!motherLeftMustBeAfterNestObservationRows.isEmpty()) {
        resultFactory.addResult("motherLeftMustBeAfterNestObservation")
            .with("rows", motherLeftMustBeAfterNestObservationRows);
      }

      if (!motherBackMustBeAfterMotherLeftRows.isEmpty()) {
        resultFactory.addResult("motherBackMustBeAfterMotherLeft")
            .with("rows", motherBackMustBeAfterMotherLeftRows);
      }
    }
  }
}


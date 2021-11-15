package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.NestEggEntity;
import com.fronde.server.domain.NestObservationEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import java.util.ArrayList;
import java.util.List;

public class NestEggValidationRule extends AbstractRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    NestObservationEntity nest = (NestObservationEntity) object;

    List<NestEggEntity> eggs = nest.getNestEggList();
    if (eggs != null && eggs.size() > 0) {
      List<String> requiredActivityRows = new ArrayList<>();
      List<String> requiredWeightMethodRows = new ArrayList<>();
      List<String> eggWeightOutsideRangeRows = new ArrayList<>();
      List<String> heartRateOutsideRangeRows = new ArrayList<>();

      for (int i = 0; i < eggs.size(); i++) {
        NestEggEntity egg = eggs.get(i);
        int row = i + 1;

        // Check for an active row (i.e. one we can't just ignore).
        if (anyNotNullOrNotEmpty(
            egg.getComments(),
            egg.getEmbryoMoving(),
            egg.getCandlingAgeEstimateInDays(),
            egg.getTemperature(),
            egg.getHeartRate(),
            egg.getWeightInGrams(),
            egg.getWidthInMms(),
            egg.getLengthInMms())) {

          // We need the Activity set.
          if (egg.getActivity() == null) {
            requiredActivityRows.add("" + row);
          }

          // If weight is set, we need the weigh method.
          if ((egg.getWeightInGrams() != null) && (nest.getWeighMethod() == null)) {
            requiredWeightMethodRows.add("" + row);
          }

          // Egg weights between 0 and 100g
          if (egg.getWeightInGrams() != null && (egg.getWeightInGrams() < 0
              || egg.getWeightInGrams() > 100)) {
            eggWeightOutsideRangeRows.add("" + row);
          }

          // Heart rate greater than or equal to 0
          if (egg.getHeartRate() != null && egg.getHeartRate() < 0) {
            heartRateOutsideRangeRows.add("" + row);
          }
        }
      }

      if (!requiredActivityRows.isEmpty()) {
        resultFactory.addResult("requiredForRow")
            .with("field", "Activity")
            .with("rows", requiredActivityRows);
      }

      if (!requiredWeightMethodRows.isEmpty()) {
        resultFactory.addResult("requiredForRow")
            .with("field", "Weigh Method")
            .with("rows", requiredWeightMethodRows);
      }

      if (!eggWeightOutsideRangeRows.isEmpty()) {
        resultFactory.addResult("EggWeightOutsideRange").with("rows", eggWeightOutsideRangeRows);
      }

      if (!heartRateOutsideRangeRows.isEmpty()) {
        resultFactory.addResult("HeartRateOutsideRange").with("rows", heartRateOutsideRangeRows);
      }
    }
  }

  public static boolean isInteger(String string) {
    if (string == null) {
      return false;
    }
    try {
      Integer i = Integer.parseInt(string);
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }

}

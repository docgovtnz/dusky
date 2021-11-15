package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.NestChickEntity;
import com.fronde.server.domain.NestObservationEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import java.util.ArrayList;
import java.util.List;

public class NestChickValidationRule extends AbstractRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    NestObservationEntity nest = (NestObservationEntity) object;

    List<NestChickEntity> chicks = nest.getNestChickList();
    if (chicks != null && chicks.size() > 0) {
      List<String> requiredActivityRows = new ArrayList<>();
      List<String> requiredWeightMethodRows = new ArrayList<>();
      List<String> chickWeightOutsideRangeRows = new ArrayList<>();

      for (int i = 0; i < chicks.size(); i++) {
        NestChickEntity chick = chicks.get(i);
        int row = i + 1;

        // Check for an active row (i.e. one we can't just ignore).
        if (anyNotNullOrNotEmpty(
            chick.getComments(),
            chick.getRespiratoryRate(),
            chick.getCropStatus(),
            chick.getWeightInGrams())) {

          // We need the Activity set.
          if (chick.getActivity() == null) {
            requiredActivityRows.add("" + row);
          }

          // If weight is set, we need the weigh method.
          if ((chick.getWeightInGrams() != null) && (nest.getWeighMethod() == null)) {
            requiredWeightMethodRows.add("" + row);
          }
          // Chick weights (g) between 0 and 5000g
          if (chick.getWeightInGrams() != null && (chick.getWeightInGrams() < 0
              || chick.getWeightInGrams() > 5000)) {
            chickWeightOutsideRangeRows.add("" + row);
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

      if (!chickWeightOutsideRangeRows.isEmpty()) {
        resultFactory.addResult("ChickWeightOutsideRange")
            .with("rows", chickWeightOutsideRangeRows);
      }
    }
  }
}

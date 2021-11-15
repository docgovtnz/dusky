package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.SampleEntity;
import com.fronde.server.domain.SpermMeasureEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpermMeasureValidationRule extends AbstractRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    SampleEntity nest = (SampleEntity) object;

    List<SpermMeasureEntity> mps = nest.getSpermMeasureList();
    if (mps != null && mps.size() > 0) {

      // check for uniqueness of spermMeasures
      Map<String, String> firstEncountered = new HashMap<>();
      Map<String, List<String>> nonUnique = new HashMap<>();

      for (int i = 0; i < mps.size(); i++) {
        SpermMeasureEntity mp = mps.get(i);
        String measure = mp.getSpermMeasure();
        String row = Integer.toString(i + 1);
        if (firstEncountered.containsKey(measure)) {
          List<String> rows = nonUnique.get(measure);
          if (rows == null) {
            rows = new ArrayList<>();
            nonUnique.put(measure, rows);
            rows.add(firstEncountered.get(measure));
          }
          rows.add(row);
        } else {
          firstEncountered.put(measure, row);
        }
      }

      for (Map.Entry<String, List<String>> entry : nonUnique.entrySet()) {
        resultFactory
            .addResult("RepeatedSpermMeasures")
            .with("spermMeasure", entry.getKey())
            .with("rows", String.join(",", entry.getValue()));
      }
    }
  }
}

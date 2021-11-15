package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.MicrobiologyAndParasitologyTestEntity;
import com.fronde.server.domain.SampleEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MicrobiologyAndParasitologyTestValidationRule extends AbstractRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    SampleEntity nest = (SampleEntity) object;

    List<MicrobiologyAndParasitologyTestEntity> mps = nest.getMicrobiologyAndParasitologyTestList();
    if (mps != null && mps.size() > 0) {

      // check for uniqueness of tests
      Map<String, String> firstEncountered = new HashMap<>();
      Map<String, List<String>> nonUnique = new HashMap<>();

      for (int i = 0; i < mps.size(); i++) {
        MicrobiologyAndParasitologyTestEntity mp = mps.get(i);
        String test = mp.getTest();
        String row = Integer.toString(i + 1);
        if (firstEncountered.containsKey(test)) {
          List<String> rows = nonUnique.get(test);
          if (rows == null) {
            rows = new ArrayList<>();
            nonUnique.put(test, rows);
            rows.add(firstEncountered.get(test));
          }
          rows.add(row);
        } else {
          firstEncountered.put(test, row);
        }
      }

      for (Map.Entry<String, List<String>> entry : nonUnique.entrySet()) {
        resultFactory
            .addResult("RepeatedTests")
            .with("test", entry.getKey())
            .with("rows", String.join(",", entry.getValue()));
      }
    }
  }
}

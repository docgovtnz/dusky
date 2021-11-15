package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.ChemistryAssayEntity;
import com.fronde.server.domain.SampleEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChemistryAssayValidationRule extends AbstractRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    SampleEntity nest = (SampleEntity) object;

    List<ChemistryAssayEntity> cas = nest.getChemistryAssayList();
    if (cas != null && cas.size() > 0) {

      // check for uniqueness of assays
      Map<String, String> firstEncountered = new HashMap<>();
      Map<String, List<String>> nonUnique = new HashMap<>();

      for (int i = 0; i < cas.size(); i++) {
        ChemistryAssayEntity ca = cas.get(i);
        String assay = ca.getChemistryAssay();
        String row = Integer.toString(i + 1);
        if (firstEncountered.containsKey(assay)) {
          List<String> rows = nonUnique.get(assay);
          if (rows == null) {
            rows = new ArrayList<>();
            nonUnique.put(assay, rows);
            rows.add(firstEncountered.get(assay));
          }
          rows.add(row);
        } else {
          firstEncountered.put(assay, row);
        }
      }

      for (Map.Entry<String, List<String>> entry : nonUnique.entrySet()) {
        resultFactory
            .addResult("RepeatedChemistryAssays")
            .with("chemistryAssay", entry.getKey())
            .with("rows", String.join(",", entry.getValue()));
      }
    }
  }
}

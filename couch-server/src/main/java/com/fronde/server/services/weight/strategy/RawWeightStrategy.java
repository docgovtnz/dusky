package com.fronde.server.services.weight.strategy;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.services.weight.WeightSummaryDTO;
import java.util.LinkedList;
import java.util.List;

public class RawWeightStrategy extends WeightStrategy {

  public static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;

  @Override
  public List<WeightSummaryDTO> process(BirdEntity bird, List<RecordEntity> records) {
    List<WeightSummaryDTO> weightList = new LinkedList<>();

    records.forEach(record -> {
      if (record.getWeight() != null && record.getWeight().getWeight() != null) {
        weightList.add(createWeightSummary(bird, record, record.getWeight()));
      }
    });
    calculateWeightChanges(weightList);

    return weightList;
  }


}

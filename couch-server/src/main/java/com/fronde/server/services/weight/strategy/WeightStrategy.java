package com.fronde.server.services.weight.strategy;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.domain.WeightEntity;
import com.fronde.server.services.weight.WeightSummaryDTO;
import java.util.Date;
import java.util.List;

public abstract class WeightStrategy {

  public static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;

  public abstract List<WeightSummaryDTO> process(BirdEntity bird, List<RecordEntity> records);

  public void calculateWeightChanges(List<WeightSummaryDTO> weightList) {
    // Calculate the weight changes.
    for (int i = 1; i < weightList.size(); i++) {
      WeightSummaryDTO current = weightList.get(i);
      WeightSummaryDTO previous = weightList.get(i - 1);

      long dateDiffMillis = (current.getDateTime().getTime() - previous.getDateTime().getTime());
      float dateDiffDays = dateDiffMillis / (float) MILLIS_PER_DAY;
      if (dateDiffDays > 0) {
        float dailyWeightChange = (current.getWeight() - previous.getWeight()) / dateDiffDays;
        float dailyWeightChangePercentage = dailyWeightChange / previous.getWeight();
        current.setDailyWeightChange(dailyWeightChangePercentage * 100);
      } else {
        current.setDailyWeightChange(null);
      }
    }
  }

  public WeightSummaryDTO createWeightSummary(BirdEntity bird, RecordEntity recordEntity,
      WeightEntity weightEntity) {
    WeightSummaryDTO dto = new WeightSummaryDTO();

    // Determine the bird age.
    Date hatchDate = bird.getDateHatched();
    if (hatchDate != null) {
      dto.setAgeDays(
          (recordEntity.getDateTime().getTime() - hatchDate.getTime()) / (float) MILLIS_PER_DAY);
    }

    dto.setRecordId(recordEntity.getId());
    dto.setDateTime(recordEntity.getDateTime());
    dto.setMethod(weightEntity.getMethod());
    dto.setWeight(weightEntity.getWeight());
    dto.setCropStatus(
        recordEntity.getChickHealth() != null ? recordEntity.getChickHealth().getCropStatus()
            : null);

    return dto;
  }

}

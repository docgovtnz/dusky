package com.fronde.server.services.weight.strategy;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.services.weight.WeightSummaryDTO;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MinInWindowWeightStrategy extends WeightStrategy {

  public static final String WEIGHT_DELTA_ROLLING_MIN_WINDOW_HRS = "WEIGHT_DELTA_ROLLING_MIN_WINDOW_HRS";

  public static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
  public static final long MILLIS_PER_HOUR = 60 * 60 * 1000;
  public static final long WINDOW_SIZE_MILLIS = 4 * 60 * 60 * 1000;

  private final long windowSizeMillis;

  public MinInWindowWeightStrategy(int windowSizeHours) {
    this.windowSizeMillis = windowSizeHours * MILLIS_PER_HOUR;
  }

  @Override
  public List<WeightSummaryDTO> process(BirdEntity bird, List<RecordEntity> records) {
    TreeMap<Date, WeightSummaryDTO> weightMap = new TreeMap<>();

    records.forEach(record -> {
      if (record.getWeight() != null && record.getWeight().getWeight() != null) {
        weightMap.put(record.getDateTime(), createWeightSummary(bird, record, record.getWeight()));
      }
    });

    Date start = weightMap.firstKey();
    while (start != null) {
      // Get the window across which we want to look for the minimum.
      Date windowEnd = new Date(start.getTime() + windowSizeMillis);
      SortedMap<Date, WeightSummaryDTO> window = weightMap.subMap(start, true, windowEnd, false);

      if (window.size() == 1) {
        // Only one item in the window, we will keep this and move to processing the next entry.
        start = weightMap.higherKey(start);
      } else {
        // Multiple items in the window. We want to keep the minimum record only. The others can be discarded.

        // Find the minimum.
        WeightSummaryDTO min = window.values().stream()
            .min(Comparator.comparing(WeightSummaryDTO::getWeight)).get();

        // Get a list of the items we want to discard.
        List<WeightSummaryDTO> toDiscard = window.values().stream().filter(v -> v != min)
            .collect(Collectors.toList());

        // Discard the items we don't want.
        toDiscard.forEach(item -> window.remove(item.getDateTime(), item));

        // Resume processing considering the item we kept as the start of the next window.
        start = min.getDateTime();
      }
    }

    List<WeightSummaryDTO> weightList = weightMap.values().stream().collect(Collectors.toList());
    calculateWeightChanges(weightList);

    return weightList;
  }


  public Optional<WeightSummaryDTO> findMinWeight(SortedMap<Date, WeightSummaryDTO> submap) {
    return submap.values().stream().min(Comparator.comparing(WeightSummaryDTO::getWeight));
  }

}

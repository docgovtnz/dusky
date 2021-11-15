package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.IslandEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import java.time.Year;
import java.util.Optional;

public class YearRangeRule extends AbstractRule<Integer> {

  public static final int CURRENT_YEAR = Year.now().getValue();
  public static final int YEAR_RANGE_MIN = Year.of(1001).getValue();
  private static final String KEY = "YEAR_RANGE_RULE";

  @Override
  public void validate(Object object, Integer propertyValue,
      ValidationResultFactory resultFactory) {
    Optional.ofNullable(propertyValue).ifPresent(year -> {
      if (year < YEAR_RANGE_MIN || year > CURRENT_YEAR) {
        resultFactory.addResult(KEY).with("field", getFieldName(object));
      }
    });
  }

  private String getFieldName(Object entity) {
    final Class<?> className = entity.getClass();
    if (BirdEntity.class.equals(className)) {
      return "Lay Year";
    } else if (IslandEntity.class.equals(className)) {
      return "Magnetic Declination As Of Year";
    } else if (RecordEntity.class.equals(className)) {
      return "Magnetic Declination As Of Year";
    } else {
      return "Year";
    }
  }
}

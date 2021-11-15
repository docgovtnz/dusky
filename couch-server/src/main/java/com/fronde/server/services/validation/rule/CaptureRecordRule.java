package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.RecordEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import org.springframework.lang.Nullable;

public class CaptureRecordRule extends AbstractRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    RecordEntity record = (RecordEntity) object;
    if ("Capture".equals(record.getRecordType())) {
      if (record.getWeight() != null) {
        Float weight = record.getWeight().getWeight();
        if (weight == null || weight < 0 || weight > 5) {
          resultFactory.addResult("WeightOutsideRange");
        }
      }
      if (record.getEggHealth() != null && record.getEggHealth().getHeartRate() != null) {
        if (record.getEggHealth().getHeartRate() < 0) {
          resultFactory.addResult("HeartRateOutsideRange");
        }
      }
    }
  }
}

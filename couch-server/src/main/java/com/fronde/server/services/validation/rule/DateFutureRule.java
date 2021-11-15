package com.fronde.server.services.validation.rule;

import com.fronde.server.services.validation.ValidationResultFactory;
import java.util.Date;

public class DateFutureRule extends AbstractRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    if (propertyValue != null) {
      if (propertyValue instanceof Date) {
        Date dateValue = (Date) propertyValue;
        if (!dateValue.after(new Date())) {
          resultFactory.addResult("DateFuture");
        }
      } else {
        throw new RuntimeException(
            "This rule class is expecting a Date but was given " + propertyValue.getClass()
                .getName());
      }
    }
  }
}


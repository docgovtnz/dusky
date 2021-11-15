package com.fronde.server.services.validation.rule;

import com.fronde.server.services.validation.ValidationResultFactory;

public class EmailRule extends AbstractRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    if ((propertyValue != null) && !propertyValue.toString().isEmpty()) {
      String email = String.valueOf(propertyValue);
      if (!email.contains("@")) {
        resultFactory.addResult("InvalidEmail");
      }
    }
  }
}

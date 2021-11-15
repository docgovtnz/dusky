package com.fronde.server.services.validation.rule;

import com.fronde.server.services.validation.ValidationResultFactory;
import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class RequiredRule extends AbstractRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    if (propertyValue == null || propertyValue.toString().isEmpty() || (
        propertyValue instanceof Collection && ((Collection) propertyValue).isEmpty())) {
      resultFactory.addResult("Required");
    }
  }
}

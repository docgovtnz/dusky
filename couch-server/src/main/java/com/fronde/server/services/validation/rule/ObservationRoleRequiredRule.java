package com.fronde.server.services.validation.rule;

import com.fronde.server.services.validation.ValidationResultFactory;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class ObservationRoleRequiredRule extends AbstractRule<List<String>> {

  @Override
  public void validate(Object object, List<String> propertyValue,
      ValidationResultFactory resultFactory) {
    if (propertyValue == null || propertyValue.isEmpty()) {
      resultFactory.addResult("ObservationRoleRequired");
    }
  }

}

package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.ObserverEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class ObserverRequiredRule extends AbstractRule<List<ObserverEntity>> {

  @Override
  public void validate(Object object, List<ObserverEntity> propertyValue,
      ValidationResultFactory resultFactory) {
    if (propertyValue == null || propertyValue.isEmpty()) {
      resultFactory.addResult("ObserverRequired");
    }
  }

}

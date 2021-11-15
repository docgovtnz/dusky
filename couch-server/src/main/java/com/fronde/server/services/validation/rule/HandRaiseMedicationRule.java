package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.HandRaiseEntity;
import com.fronde.server.domain.MedicationEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import com.hazelcast.util.CollectionUtil;
import java.util.List;
import java.util.Optional;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class HandRaiseMedicationRule extends AbstractRule<Object> {


  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    RecordEntity r = (RecordEntity) object;

    // If there are medications/drugs listed, then the medication value must be true
    // Note that the inverse is NOT true, i.e. if medications value is true, don't required drugs
    HandRaiseEntity handRaise = r.getHandRaise();
    List<MedicationEntity> medicationList = r.getMedicationList();
    if (handRaise != null && CollectionUtil.isNotEmpty(medicationList)) {
      boolean medication = Optional.ofNullable(handRaise.getMedication()).orElse(false);
      if (!medication) {
        resultFactory.addResult("MedicationNotSetWhenDrugs");
      }
    }
  }
}

package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.RecordEntity;
import com.fronde.server.domain.TransferDetailEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class TransferRecordToLocationIDRule extends AbstractRule<String> {

  @Override
  public void validate(Object object, String propertyValue, ValidationResultFactory resultFactory) {
    RecordEntity r = (RecordEntity) object;
    TransferDetailEntity td = r.getTransferDetail();
    if (td != null && propertyValue != null && td.getTransferFromLocationID() != null) {
      // if either the transfer to and transfer from is null, let it be caught by other validation checks
      if (propertyValue.equals(td.getTransferFromLocationID())) {
        resultFactory.addResult("TransferToLocationIDMustDiffer");
      }
    }
  }

}

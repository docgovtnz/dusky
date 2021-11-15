package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.RecordEntity;
import com.fronde.server.domain.TransferDetailEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class TransferRecordLocationIDRule extends AbstractRule<String> {

  @Override
  public void validate(Object object, String propertyValue, ValidationResultFactory resultFactory) {
    RecordEntity r = (RecordEntity) object;
    if ("Transfer".equals(r.getRecordType())) {
      // there is no mandatory requirement on location id so we must check null also
      TransferDetailEntity td = r.getTransferDetail();
      if (td == null || td.getTransferToLocationID() == null) {
        if (r.getLocationID() != null) {
          resultFactory.addResult("LocationIDsMustMatch");
        } else {
          // both are null and therefore the same
        }
      } else if (!td.getTransferToLocationID().equals(r.getLocationID())) {
        resultFactory.addResult("LocationIDsMustMatch");
      }
    }
  }

}

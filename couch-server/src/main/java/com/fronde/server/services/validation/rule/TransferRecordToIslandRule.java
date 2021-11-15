package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.RecordEntity;
import com.fronde.server.domain.TransferDetailEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class TransferRecordToIslandRule extends AbstractRule<String> {

  @Override
  public void validate(Object object, String propertyValue, ValidationResultFactory resultFactory) {
    RecordEntity r = (RecordEntity) object;
    TransferDetailEntity td = r.getTransferDetail();
    if (td != null && propertyValue != null && td.getTransferFromIsland() != null) {
      // if either the transfer to and transfer from is null, let it be caught by the required rule for these fields

      // islands only need to differ if the locations don't differ
      boolean locationsDiffer = true;
      if (td.getTransferFromLocationID() == null && td.getTransferToLocationID() == null) {
        locationsDiffer = false;
      } else if (td.getTransferFromLocationID() != null && td.getTransferToLocationID() != null) {
        if (td.getTransferFromLocationID().equals(td.getTransferToLocationID())) {
          locationsDiffer = false;
        }
      }
      if (!locationsDiffer && propertyValue.equals(td.getTransferFromIsland())) {
        resultFactory.addResult("TransferToIslandMustDiffer");
      }
    }
  }

}

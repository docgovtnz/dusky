package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.RecordEntity;
import com.fronde.server.domain.TransferDetailEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class TransferRecordIslandRule extends AbstractRule<String> {

  @Override
  public void validate(Object object, String propertyValue, ValidationResultFactory resultFactory) {
    RecordEntity r = (RecordEntity) object;
    if ("Transfer".equals(r.getRecordType())) {
      // if island is null, let it be caught by the required rule for island instead of adding additional validation errors
      if (propertyValue != null) {
        TransferDetailEntity td = r.getTransferDetail();
        if (td == null || td.getTransferToIsland() == null || !td.getTransferToIsland()
            .equals(r.getIsland())) {
          resultFactory.addResult("IslandsMustMatch");
        }
      }
    }
  }

}

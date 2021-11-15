package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.TransmitterEntity;
import com.fronde.server.services.transmitter.TransmitterService;
import com.fronde.server.services.validation.ValidationAppContext;
import com.fronde.server.services.validation.ValidationResultFactory;

public class TransmitterUniqueTxIdRule extends AbstractRule<String> {

  @Override
  public void validate(Object object, String propertyValue, ValidationResultFactory resultFactory) {
    TransmitterEntity r = (TransmitterEntity) object;

    // if we are validating a new transmitter, we will still have a TransmitterEntity object, just that r.id will be null

    if (!isUniqueTxId(r.getId(), propertyValue)) {
      resultFactory.addResult("TransmitterTxIdNotUnique");
    }
  }

  protected boolean isUniqueTxId(String transmitterID, String txId) {
    TransmitterService service = ValidationAppContext.get().getBean(TransmitterService.class);
    return service.isUniqueTxId(transmitterID, txId);
  }

}

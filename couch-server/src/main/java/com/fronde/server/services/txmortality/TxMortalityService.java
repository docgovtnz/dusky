package com.fronde.server.services.txmortality;

import com.fronde.server.domain.TxMortalityEntity;
import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.services.transmitter.TransmitterService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxMortalityService extends TxMortalityBaseService {

  @Autowired
  private TransmitterService transmitterService;

  @Override
  public DeleteByIdCheckDTO deleteByIdCheck(String docId) {
    DeleteByIdCheckDTO check = new DeleteByIdCheckDTO();
    check.setId(docId);
    // we can't delete something that doesn't exist
    check.setDeleteOk(docId != null && !repository.hasReferences(docId));
    return check;
  }

  public List<TxMortalityEntity> getAllTxMortalityOptions() {
    return repository.getAllTxMortalityOptions();
  }

  public List<String> getTxMortalityTypes() {
    return repository.getTxMortalityTypes();
  }

  public boolean isUniqueName(String metaID, String name) {
    boolean exists = repository.existsByNameExcluding(name, metaID);
    return !exists;
  }

}


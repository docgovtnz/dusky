package com.fronde.server.services.txmortality;

import com.fronde.server.domain.TxMortalityEntity;
import com.fronde.server.domain.response.PagedResponse;
import java.util.List;

public interface TxMortalityRepositoryCustom {

  PagedResponse<TxMortalityEntity> findByCriteria(TxMortalityCriteria criteria);

  List<TxMortalityEntity> getAllTxMortalityOptions();

  List<String> getTxMortalityTypes();

  boolean hasReferences(String txMortalityId);

  boolean existsByNameExcluding(String name, String excludingMetaId);

}

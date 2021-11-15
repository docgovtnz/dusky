package com.fronde.server.services.txmortality;

import com.fronde.server.domain.TxMortalityEntity;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
@N1qlPrimaryIndexed
public interface TxMortalityRepository extends
    PagingAndSortingRepository<TxMortalityEntity, String>, TxMortalityRepositoryCustom {

}

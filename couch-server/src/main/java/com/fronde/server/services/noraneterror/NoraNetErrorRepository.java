package com.fronde.server.services.noraneterror;

import com.fronde.server.domain.NoraNetErrorEntity;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
@N1qlPrimaryIndexed
public interface NoraNetErrorRepository extends
    PagingAndSortingRepository<NoraNetErrorEntity, String>, NoraNetErrorRepositoryCustom {

}

package com.fronde.server.services.record;

import com.fronde.server.domain.RecordEntity;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
@N1qlPrimaryIndexed
public interface RecordRepository extends PagingAndSortingRepository<RecordEntity, String>,
    RecordRepositoryCustom {

}

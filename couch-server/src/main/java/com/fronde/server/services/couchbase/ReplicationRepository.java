package com.fronde.server.services.couchbase;

import com.fronde.server.domain.ReplicationEntity;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
@N1qlPrimaryIndexed
public interface ReplicationRepository extends
    PagingAndSortingRepository<ReplicationEntity, String>, ReplicationRepositoryCustom {

}

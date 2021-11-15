package com.fronde.server.services.bird;

import com.fronde.server.domain.BirdEntity;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
@N1qlPrimaryIndexed
public interface BirdRepository extends PagingAndSortingRepository<BirdEntity, String>,
    BirdRepositoryCustom {

}

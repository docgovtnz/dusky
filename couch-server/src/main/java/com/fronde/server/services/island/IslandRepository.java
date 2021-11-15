package com.fronde.server.services.island;

import com.fronde.server.domain.IslandEntity;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
@N1qlPrimaryIndexed
public interface IslandRepository extends PagingAndSortingRepository<IslandEntity, String>,
    IslandRepositoryCustom {

}

package com.fronde.server.services.feedout;

import com.fronde.server.domain.FeedOutEntity;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
@N1qlPrimaryIndexed
public interface FeedOutRepository extends PagingAndSortingRepository<FeedOutEntity, String>,
    FeedOutRepositoryCustom {

}

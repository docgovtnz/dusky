package com.fronde.server.services.location;

import com.fronde.server.domain.LocationEntity;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
@N1qlPrimaryIndexed
public interface LocationRepository extends PagingAndSortingRepository<LocationEntity, String>,
    LocationRepositoryCustom {

}

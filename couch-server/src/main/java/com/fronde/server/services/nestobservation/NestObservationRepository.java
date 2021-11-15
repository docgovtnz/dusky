package com.fronde.server.services.nestobservation;

import com.fronde.server.domain.NestObservationEntity;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
@N1qlPrimaryIndexed
public interface NestObservationRepository extends
    PagingAndSortingRepository<NestObservationEntity, String>, NestObservationRepositoryCustom {

}

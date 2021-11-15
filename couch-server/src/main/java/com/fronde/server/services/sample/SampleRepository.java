package com.fronde.server.services.sample;

import com.fronde.server.domain.SampleEntity;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
@N1qlPrimaryIndexed
public interface SampleRepository extends PagingAndSortingRepository<SampleEntity, String>,
    SampleRepositoryCustom {

}

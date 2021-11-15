package com.fronde.server.services.snarkactivity;

import com.fronde.server.domain.SnarkActivityEntity;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
@N1qlPrimaryIndexed
public interface SnarkActivityRepository extends
    PagingAndSortingRepository<SnarkActivityEntity, String>, SnarkActivityRepositoryCustom {

}

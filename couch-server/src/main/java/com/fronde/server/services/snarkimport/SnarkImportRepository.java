package com.fronde.server.services.snarkimport;

import com.fronde.server.domain.SnarkImportEntity;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
@N1qlPrimaryIndexed
public interface SnarkImportRepository extends
    PagingAndSortingRepository<SnarkImportEntity, String>, SnarkImportRepositoryCustom {

}

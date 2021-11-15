package com.fronde.server.services.revision;

import com.fronde.server.domain.RevisionList;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@N1qlPrimaryIndexed
public interface RevisionRepository extends PagingAndSortingRepository<RevisionList, String>,
    RevisionRepositoryCustom {

}

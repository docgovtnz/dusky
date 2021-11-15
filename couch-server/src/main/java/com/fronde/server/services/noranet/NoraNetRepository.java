package com.fronde.server.services.noranet;

import com.fronde.server.domain.NoraNetEntity;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @date 14/07/2021
 */

@Component
@N1qlPrimaryIndexed
public interface NoraNetRepository extends
    PagingAndSortingRepository<NoraNetEntity, String>, NoraNetRepositoryCustom {

}

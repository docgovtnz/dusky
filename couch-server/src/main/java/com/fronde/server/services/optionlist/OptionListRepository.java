package com.fronde.server.services.optionlist;

import com.fronde.server.domain.OptionListEntity;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
@N1qlPrimaryIndexed
public interface OptionListRepository extends PagingAndSortingRepository<OptionListEntity, String>,
    OptionListRepositoryCustom {

}

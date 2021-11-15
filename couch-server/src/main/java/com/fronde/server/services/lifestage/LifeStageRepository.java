package com.fronde.server.services.lifestage;

import com.fronde.server.domain.LifeStageEntity;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
@N1qlPrimaryIndexed
public interface LifeStageRepository extends PagingAndSortingRepository<LifeStageEntity, String>,
    LifeStageRepositoryCustom {

}

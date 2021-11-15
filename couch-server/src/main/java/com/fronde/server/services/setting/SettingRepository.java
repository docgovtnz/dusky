package com.fronde.server.services.setting;

import com.fronde.server.domain.SettingEntity;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
@N1qlPrimaryIndexed
public interface SettingRepository extends PagingAndSortingRepository<SettingEntity, String>,
    SettingRepositoryCustom {

}

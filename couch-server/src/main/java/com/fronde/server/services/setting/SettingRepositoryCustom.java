package com.fronde.server.services.setting;

import com.fronde.server.domain.SettingEntity;
import com.fronde.server.domain.response.PagedResponse;

public interface SettingRepositoryCustom {

  PagedResponse<SettingEntity> findByCriteria(SettingCriteria criteria);

}

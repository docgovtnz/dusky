package com.fronde.server.services.lifestage;

import com.fronde.server.domain.LifeStageEntity;
import com.fronde.server.domain.response.PagedResponse;

public interface LifeStageRepositoryCustom {

  PagedResponse<LifeStageEntity> findByCriteria(LifeStageCriteria criteria);

}

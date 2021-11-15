package com.fronde.server.services.lifestage;

import com.fronde.server.domain.LifeStageEntity;
import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.utils.ServiceUtils;
import org.springframework.stereotype.Component;


@Component
public class LifeStageService extends LifeStageBaseService {

  @Override
  public DeleteByIdCheckDTO deleteByIdCheck(String docId) {
    // TODO what is correct here?
    return null;
  }

  public void saveWithThrow(LifeStageEntity entity) {
    ServiceUtils.throwIfRequired(save(entity), "LifeStage", entity.getId());
  }

}


package com.fronde.server.services.noraneterror;

import com.fronde.server.domain.NoraNetErrorEntity;
import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.domain.response.Response;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @version 1.0
 */

@Component
public class NoraNetErrorService extends NoraNetErrorBaseService {

  @Override
  public Response<NoraNetErrorEntity> save(NoraNetErrorEntity entity) {
    return super.save(entity);
  }

  @Override
  public DeleteByIdCheckDTO deleteByIdCheck(String docId) {
    return null;
  }

  public Response<List<NoraNetErrorReportDTO>> findSearchDTOByCriteria(NoraNetErrorCriteria criteria) {
    return repository.findSearchDTOByCriteria(criteria);
  }

}

package com.fronde.server.services.noraneterror;

import com.fronde.server.domain.NoraNetErrorEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.domain.response.Response;
import java.util.List;

public interface NoraNetErrorRepositoryCustom {

  PagedResponse<NoraNetErrorEntity> findByCriteria(NoraNetErrorCriteria criteria);

  Response<List<NoraNetErrorReportDTO>> findSearchDTOByCriteria(NoraNetErrorCriteria criteria);
}

package com.fronde.server.services.common;

import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.id.IdSearchCriteria;
import com.fronde.server.services.id.IdSearchDTO;
import org.springframework.stereotype.Component;

@Component
public interface CommonRepository {

  PagedResponse<IdSearchDTO> findIdSearchByCriteria(IdSearchCriteria criteria);

}

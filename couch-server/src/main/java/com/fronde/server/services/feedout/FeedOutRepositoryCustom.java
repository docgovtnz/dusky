package com.fronde.server.services.feedout;

import com.fronde.server.domain.FeedOutEntity;
import com.fronde.server.domain.response.PagedResponse;

public interface FeedOutRepositoryCustom {

  PagedResponse<FeedOutEntity> findByCriteria(FeedOutCriteria criteria);

  PagedResponse<FeedOutSearchDTO> findSearchDTOByCriteria(FeedOutCriteria criteria);

}

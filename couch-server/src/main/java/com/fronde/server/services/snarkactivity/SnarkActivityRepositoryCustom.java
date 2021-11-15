package com.fronde.server.services.snarkactivity;

import com.fronde.server.domain.SnarkActivityEntity;
import com.fronde.server.domain.response.PagedResponse;

public interface SnarkActivityRepositoryCustom {

  PagedResponse<SnarkActivityEntity> findByCriteria(SnarkActivityCriteria criteria);

  PagedResponse<SnarkActivitySearchDTO> findSearchDTOByCriteria(SnarkActivityCriteria criteria);

  boolean hasReferences(String transmitterID);

}

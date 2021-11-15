package com.fronde.server.services.nestobservation;

import com.fronde.server.domain.NestObservationEntity;
import com.fronde.server.domain.response.PagedResponse;

public interface NestObservationRepositoryCustom {

  PagedResponse<NestObservationEntity> findByCriteria(NestObservationCriteria criteria);

  PagedResponse<NestObservationSearchDTO> findSearchDTOByCriteria(NestObservationCriteria criteria);

  boolean hasRelatedNestObservation(String recordID);

  NestObservationEntity findByRecordID(String recordID);
}

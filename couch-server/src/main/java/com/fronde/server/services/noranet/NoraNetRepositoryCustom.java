package com.fronde.server.services.noranet;

import com.fronde.server.domain.NoraNetDetectionEntity;
import com.fronde.server.domain.NoraNetEntity;
import com.fronde.server.domain.response.PagedResponse;
import java.util.List;

/**
 * @version 1.0
 * @date 14/07/2021
 */

public interface NoraNetRepositoryCustom {

  PagedResponse<NoraNetEntity> findByCriteria(NoraNetCriteria criteria);

  PagedResponse<NoraNetSearchDTO> findSearchDTOByCriteria(NoraNetCriteria criteria);

  List<NoraNetSearchUndetectedDTO> findUndetectedDTOByCriteria(NoraNetCriteria criteria);

  PagedResponse<NoraNetSearchStationDTO> findStationDTOByCriteria(NoraNetCriteria criteria);

  PagedResponse<NoraNetSearchSnarkDTO> findSnarkDTOByCriteria(NoraNetCriteria criteria);

  NoraNetDetectionEntity findDetectedById(String docId, Integer uhfId);

  List<String> findStations();
}

package com.fronde.server.services.location;

import com.fronde.server.domain.LocationEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.options.LocationSummaryDTO;
import java.util.List;

public interface LocationRepositoryCustom {

  PagedResponse<LocationEntity> findByCriteria(LocationCriteria criteria);

  List<LocationSummaryDTO> findLocationSummaries();

  List<String> findLocationNames();

  PagedResponse<LocationSearchDTO> findSearchDTOByCriteria(LocationCriteria criteria);

  boolean hasReferences(String locationID);

  PagedResponse<String> getCurrentEggs(String locationID);

  PagedResponse<String> getCurrentChicks(String locationID);

  Integer getNextClutchOrder(String locationID);

  String getClutch(String locationID);

  /**
   * Returns whether the location with the specific name exists, excluding if it is the specified
   * location (using excludingLocationID)
   *
   * @param locationName
   * @param excludingLocationID
   * @return
   */
  boolean existsByNameExcluding(String locationName, String excludingLocationID);

}

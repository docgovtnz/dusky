package com.fronde.server.services.island;

import com.fronde.server.domain.IslandEntity;
import com.fronde.server.domain.response.PagedResponse;
import java.util.List;

public interface IslandRepositoryCustom {

  PagedResponse<IslandEntity> findByCriteria(IslandCriteria criteria);

  IslandEntity findOneByExactName(String islandName);

  List<String> findIslandNames();

  /**
   * Returns whether an island with the specific id exists, excluding if it is the specified id
   * (using excludingMetaId)
   *
   * @param islandId
   * @param excludingMetaId
   * @return
   */
  boolean existsByIslandIdExcluding(Integer islandId, String excludingMetaId);

  /**
   * Returns true if the specified island name is unique across all islands. islandID can be null in
   * the case of this being a new island.
   *
   * @param islandName
   * @param excludingMetaId
   * @return
   */
  boolean existsByIslandNameExcluding(String islandName, String excludingMetaId);

}

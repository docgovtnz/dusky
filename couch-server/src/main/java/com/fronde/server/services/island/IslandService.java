package com.fronde.server.services.island;

import com.fronde.server.domain.IslandEntity;
import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.domain.response.PagedResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class IslandService extends IslandBaseService {

  public List<IslandEntity> findAll() {
    IslandCriteria criteria = new IslandCriteria();
    criteria.setPageSize(999);
    PagedResponse<IslandEntity> pagedResponse = repository.findByCriteria(criteria);
    return pagedResponse.getResults();
  }

  public IslandEntity findByName(String islandName) {
    IslandEntity entity = repository.findOneByExactName(islandName);
    return entity;
  }

  @Override
  public DeleteByIdCheckDTO deleteByIdCheck(String docId) {
    DeleteByIdCheckDTO checkDTO = new DeleteByIdCheckDTO();
    checkDTO.setId(docId);
    // you can always bring an island back again
    // we can't delete something that doesn't exist
    checkDTO.setDeleteOk(docId != null);
    return checkDTO;
  }

  /**
   * Returns true if the specified islId is unique across all islands. islandID can be null in
   * the case of this being a new island.
   *
   * @param metaID
   * @param islandId
   * @return
   */
  public boolean isUniqueIslandId(String metaID, Integer islandId) {
    boolean exists = repository.existsByIslandIdExcluding(islandId, metaID);
    return !exists;
  }

  /**
   * Returns true if the specified island name is unique across all islands. islandID can be null in
   * the case of this being a new island.
   *
   * @param metaID
   * @param islandName
   * @return
   */
  public boolean isUniqueIslandName(String metaID, String islandName) {
    boolean exists = repository.existsByIslandNameExcluding(islandName, metaID);
    return !exists;
  }


}

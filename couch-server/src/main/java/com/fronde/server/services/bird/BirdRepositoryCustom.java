package com.fronde.server.services.bird;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.LifeStageEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.options.BirdSummaryDTO;
import java.util.List;

public interface BirdRepositoryCustom {

  PagedResponse<BirdEntity> findByCriteria(BirdCriteria criteria);

  PagedResponse<BirdSearchDTO> findSearchDTOByCriteria(BirdCriteria criteria);

  List<BirdSummaryDTO> findBirdSummaries(BirdSummaryCriteria criteria);

  List<String> findBirdNames();

  List<BirdLocationDTO> findBirdsAliveAtDate(BirdLocationCriteria criteria);

  List<BirdLocationDTO> findBirdsAliveBetweenDates(BirdLocationCriteria criteria);

  BirdEntity findBirdIDByTransmitter(String island, int channel, String sex);

  List<BirdEntity> findBirdsByTransmitter(String island, int uhfId, String sex);

  boolean hasReferences(String birdID);

  LifeStageEntity findLatestLifeStageByBirdID(String birdID);

  String getName(String birdID);

  /**
   * Returns whether the bird with the specific name exists, excluding if it is the specified bird
   * (using excludingBirdID)
   *
   * @param birdName
   * @param excludingBirdID
   * @return
   */
  boolean existsByNameExcluding(String birdName, String excludingBirdID);

  /**
   * Returns whether the bird with the specific house ID exists, excluding if it is the specified
   * bird (using excludingBirdID)
   *
   * @param houseID
   * @param excludingBirdID
   * @return
   */
  boolean existsByHouseIDExcluding(String houseID, String excludingBirdID);
}

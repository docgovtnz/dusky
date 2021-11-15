package com.fronde.server.services.record;

import com.fronde.server.domain.RecordEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.options.BandsAndChipsDTO;
import com.fronde.server.services.options.CurrentTransmitterInfoDTO;
import com.fronde.server.services.options.TransmitterBirdHistoryDTO2;
import java.util.Date;
import java.util.List;

public interface RecordRepositoryCustom {

  PagedResponse<RecordEntity> findByCriteria(RecordCriteria criteria);

  PagedResponse<RecordSearchDTO> findSearchDTOByCriteria(RecordCriteria criteria);

  List<String> findRecordTypes();

  List<String> findRecordActivities();

  List<String> findRecordReasons();

  BandsAndChipsDTO findIdInfoByBirdId(String birdId);

  List<BandsAndChipsDTO> findBandHistoryByBirdId(String birdId);

  List<BandsAndChipsDTO> findChipHistoryByBirdId(String birdId);

  CurrentTransmitterInfoDTO findCurrentTransmitterInfoByBirdId(String birdId);

  DatedMeasureDetailDTO getCurrentMeasureDetailByBirdID(String birdID);

  List<DatedMeasureDetailDTO> getMeasureDetailHistoryByBirdID(String birdID);

  List<TransmitterBirdHistoryDTO2> findTransmitterBirdHistory(String txID);

  RecordEntity findLatestRecordWithPartByBirdID(String birdID, String part);

  RecordEntity findLatestRecordWithPartByBirdIDExcluding(String birdID, String transmitterChange,
      String excluding);

  List<RecordEntity> findWithPartByBirdID(String birdID, String transmitterChange);

  List<BirdTransmitterHistoryDTO> findTransmitterHistoryDTOByBirdID(String birdID);

  List<TransmitterBirdHistoryDTO> findTransmitterBirdHistoryDTOByTxDocId(String txDocId);

  List<RecordEntity> findWeightRecordsByBirdID(String birdID);

  List<RecordEntity> findEggWeights(String birdID, Date layDate, Date hatchDate);

  RecordEntity findRecordBySampleID(String sampleID);

}

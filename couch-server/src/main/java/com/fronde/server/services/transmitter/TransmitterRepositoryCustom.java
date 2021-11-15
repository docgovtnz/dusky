package com.fronde.server.services.transmitter;

import com.fronde.server.domain.TransmitterEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.options.TransmitterDTO;
import com.fronde.server.services.options.TransmitterSearchDTO;
import java.util.List;

public interface TransmitterRepositoryCustom {

  PagedResponse<TransmitterEntity> findByCriteria(TransmitterCriteria criteria);

  PagedResponse<TransmitterSearchDTO> findSearchDtoByCriteria(TransmitterCriteria criteria);

  List<String> findTransmitters(boolean fullList);

  TransmitterDTO findTransmitterDTOByTxId(String txId);

  boolean hasReferences(String transmitterID);

  List<String> findTxIds();

  TransmitterEntity findCurrentTransmitterByBirdID(String birdID);

  /**
   * Returns whether the transmitter with the specific id exists, excluding if it is the specified
   * transmitter (using excludingTransmitterID)
   *
   * @param txId
   * @param excludingTransmitterID
   * @return
   */
  boolean existsByTxIdExcluding(String txId, String excludingTransmitterID);

}

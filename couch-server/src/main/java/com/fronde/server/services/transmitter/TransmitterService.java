package com.fronde.server.services.transmitter;

import com.fronde.server.domain.RecordEntity;
import com.fronde.server.domain.TransmitterEntity;
import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.options.TransmitterDTO;
import com.fronde.server.services.options.TransmitterSearchDTO;
import com.fronde.server.services.record.RecordService;
import com.fronde.server.services.record.TransmitterBirdHistoryDTO;
import com.fronde.server.utils.ServiceUtils;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransmitterService extends TransmitterBaseService {

  @Autowired
  private RecordService recordService;

  @Override
  public DeleteByIdCheckDTO deleteByIdCheck(String docId) {
    DeleteByIdCheckDTO dto = new DeleteByIdCheckDTO();
    dto.setId(docId);
    // we can't delete something that doesn't exist
    dto.setDeleteOk(docId != null && !repository.hasReferences(docId));
    return dto;
  }

  public List<String> findTransmitters(boolean fullList) {
    return repository.findTransmitters(fullList);
  }

  public TransmitterDTO findTransmitterDTOByTxId(String txid) {
    return repository.findTransmitterDTOByTxId(txid);
  }

  public PagedResponse<TransmitterSearchDTO> findSearchDTOByCriteria(TransmitterCriteria criteria) {
    return repository.findSearchDtoByCriteria(criteria);
  }

  @Override
  public Response<TransmitterEntity> save(TransmitterEntity entity) {
    //If creating a new transmitter set status to 'New'
    if (entity.getStatus() == null) {
      entity.setStatus("New");
    }
    Response<TransmitterEntity> response = super.save(entity);
    return response;
  }

  public void saveWithThrow(TransmitterEntity entity) {
    ServiceUtils.throwIfRequired(save(entity), "Transmitter", entity.getId());
  }

  public Date getDeployedDateTime(String id) {
    // get the date of the last record
    TransmitterEntity t = this.findById(id);
    if (Arrays.asList(new String[]{"Deployed old", "Deployed new"}).contains(t.getStatus())) {
      if (t.getLastRecordId() != null) {
        RecordEntity r = recordService.findById(t.getLastRecordId());
        if (r != null) {
          return r.getDateTime();
        }
      }
    }
    return null;
  }

  public Date calculateExpiryDate(String id, Integer lifeExpectancy) {
    return calculateExpiryDate(getDeployedDateTime(id), lifeExpectancy);
  }

  public Date calculateExpiryDate(String id, Date deployedDateTime) {
    return calculateExpiryDate(deployedDateTime, getLifeExpectancy(id));
  }

  public Date getExpiryDate(String id) {
    return calculateExpiryDate(getDeployedDateTime(id), getLifeExpectancy(id));
  }

  private Date calculateExpiryDate(Date deployedDateTime, Integer lifeExpectancy) {
    if (deployedDateTime != null && lifeExpectancy != null) {
      return Date.from(deployedDateTime.toInstant().atZone(ZoneId.of("Pacific/Auckland"))
          .truncatedTo(ChronoUnit.DAYS).plus(lifeExpectancy, ChronoUnit.WEEKS).toInstant());
    } else {
      return null;
    }
  }

  private Integer getLifeExpectancy(String id) {
    TransmitterEntity t = this.findById(id);
    return t.getLifeExpectancy();
  }

  public void export(TransmitterCriteria criteria, HttpServletResponse response) {
    List<String> header = Arrays
        .asList("Tx Id", "Status", "Island", "Channel", "UHF Id", "Mort Type", "Bird",
            "Life Expectancy", "Rigging");
    List<String> props = Arrays
        .asList("txId", "status", "island", "channel", "uhfId", "mortType", "birdName",
            "lifeExpectancy", "rigging");
    criteria.setPageSize(exportUtils.getMaxRows());
    criteria.setPageNumber(1);
    PagedResponse<TransmitterSearchDTO> pr = this.findSearchDTOByCriteria(criteria);
    exportUtils.export(response, pr, header, props, "transmitter");
  }

  public List<TransmitterBirdHistoryDTO> getBirdHistory(String id) {
    return recordService.findTransmitterBirdHistoryDTOByTxDocId(id);
  }

  /**
   * Returns true if the specified id is unique for the specified transmitter (via transmitterID).
   * transmitterID can be null in the case of this being a new transmitter.
   *
   * @param transmitterID
   * @param txId
   * @return
   */
  public boolean isUniqueTxId(String transmitterID, String txId) {
    boolean exists = repository.existsByTxIdExcluding(txId, transmitterID);
    return !exists;
  }

}

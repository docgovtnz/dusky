package com.fronde.server.services.transmitter;

import com.fronde.server.domain.TransmitterEntity;
import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.common.ObjectIdFactory;
import com.fronde.server.services.revision.RevisionService;
import com.fronde.server.services.validation.ValidationMessage;
import com.fronde.server.services.validation.ValidationService;
import com.fronde.server.utils.BaseEntityUtils;
import com.fronde.server.utils.CSVExportUtils;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public abstract class TransmitterBaseService {

  @Autowired
  protected TransmitterRepository repository;

  @Autowired
  protected ValidationService validationService;

  @Autowired
  protected RevisionService revisionService;

  @Autowired
  protected CSVExportUtils exportUtils;

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  public TransmitterEntity findById(String docId) {
    // using orElse instead of get so we return null if the entity is not found
    return repository.findById(docId).orElse(null);
  }

  public PagedResponse<TransmitterEntity> search(TransmitterCriteria criteria) {
    return repository.findByCriteria(criteria);
  }

  public Response<TransmitterEntity> save(TransmitterEntity entity) {
    List<ValidationMessage> messages = validationService.validateEntity(entity);
    if (messages.size() == 0) {
      if (entity.getId() == null) {
        entity.setId(objectIdFactory.create());
      }

      if (entity.getDocType() == null) {
        entity.setDocType("Transmitter");
      }

      BaseEntityUtils.setModifiedFields(entity);

      revisionService.createRevision(entity);
      entity = repository.save(entity);
    }

    Response<TransmitterEntity> response = new Response<>(entity);
    response.setMessages(messages);
    return response;
  }

  public void deleteById(String id) {
    DeleteByIdCheckDTO deleteByIdCheckDTO = deleteByIdCheck(id);
    if (deleteByIdCheckDTO != null && deleteByIdCheckDTO.getDeleteOk()) {
      repository.deleteById(id);
    } else {
      throw new RuntimeException("Delete check failed for: " + id);
    }
  }

  public abstract DeleteByIdCheckDTO deleteByIdCheck(String docId);

  public void export(TransmitterCriteria criteria, HttpServletResponse response) {
    List<String> header = Arrays.asList(
        "Tx Id", "Status", "Island", "Channel", "Mort Type", "Bird", "Life Expectancy",
        "Rigging");
    List<String> props = Arrays.asList(
        "txId", "status", "island", "channel", "mortType", "birdId", "lifeExpectancy",
        "rigging");
    criteria.setPageSize(exportUtils.getMaxRows());
    criteria.setPageNumber(1);
    PagedResponse<TransmitterEntity> pr = this.search(criteria);
    exportUtils.export(response, pr, header, props, "transmitter");
  }

}

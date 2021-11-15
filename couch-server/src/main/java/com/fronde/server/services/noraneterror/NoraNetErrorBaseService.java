package com.fronde.server.services.noraneterror;

import com.fronde.server.domain.NoraNetErrorEntity;
import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.common.ObjectIdFactory;
import com.fronde.server.services.revision.RevisionService;
import com.fronde.server.services.validation.ValidationMessage;
import com.fronde.server.services.validation.ValidationService;
import com.fronde.server.utils.BaseEntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @version 1.0
 */

@Component
public abstract class NoraNetErrorBaseService {

  @Autowired
  protected NoraNetErrorRepository repository;
  @Autowired
  protected RevisionService revisionService;
  @Autowired
  protected ObjectIdFactory objectIdFactory;
  @Autowired
  protected ValidationService validationService;

  public NoraNetErrorEntity findById(String docId) {
    // using orElse instead of get so we return null if the entity is not found
    return repository.findById(docId).orElse(null);
  }

  public PagedResponse<NoraNetErrorEntity> search(NoraNetErrorCriteria criteria) {
    return repository.findByCriteria(criteria);
  }

  public Response<NoraNetErrorEntity> save(NoraNetErrorEntity entity) {
    List<ValidationMessage> messages = validationService.validateEntity(entity);
    if (messages.size() == 0) {
      if (entity.getId() == null) {
        entity.setId(objectIdFactory.create());
      }
      if (entity.getDocType() == null) {
        entity.setDocType("NoraNetError");
      }
      BaseEntityUtils.setModifiedFields(entity);
      revisionService.createRevision(entity);
      entity = repository.save(entity);
    }
    Response<NoraNetErrorEntity> response = new Response<>(entity);
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

}

package com.fronde.server.services.noranet;

import com.fronde.server.domain.NoraNetEntity;
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
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @version 1.0
 * @date 14/07/2021
 */

@Component
public abstract class NoraNetBaseService {

  @Autowired
  protected NoraNetRepository repository;

  @Autowired
  protected RevisionService revisionService;

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  @Autowired
  protected ValidationService validationService;

  public NoraNetEntity findById(String docId) {
    // using orElse instead of get so we return null if the entity is not found
    return repository.findById(docId).orElse(null);
  }

  public PagedResponse<NoraNetEntity> search(NoraNetCriteria criteria) {
    return repository.findByCriteria(criteria);
  }

  public Response<NoraNetEntity> save(NoraNetEntity entity) {
    List<ValidationMessage> messages = validationService.validateEntity(entity);
    if (messages.size() == 0) {
      if (entity.getId() == null) {
        entity.setId(objectIdFactory.create());
      }
      if (entity.getDocType() == null) {
        entity.setDocType("NoraNet");
      }
      BaseEntityUtils.setModifiedFields(entity);
      revisionService.createRevision(entity);
      entity = repository.save(entity);
    }
    Response<NoraNetEntity> response = new Response<>(entity);
    response.setMessages(messages);
    return response;
  }

  // for saving incoming NoraNet data from AWS only
  public Response<NoraNetEntity> saveNoraNet(NoraNetEntity entity) {
    if (entity.getId() == null) {
      entity.setId(objectIdFactory.create());
    }
    if (entity.getDocType() == null) {
      entity.setDocType("NoraNet");
    }
    BaseEntityUtils.setModifiedFields(entity);
    revisionService.createRevision(entity);
    entity = repository.save(entity);

    Response<NoraNetEntity> response = new Response<>(entity);
    response.setMessages(null);
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

  public abstract void export(NoraNetCriteria criteria, HttpServletResponse response);

}

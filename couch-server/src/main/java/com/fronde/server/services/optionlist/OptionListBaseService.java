package com.fronde.server.services.optionlist;

import com.fronde.server.domain.OptionListEntity;
import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.common.ObjectIdFactory;
import com.fronde.server.services.revision.RevisionService;
import com.fronde.server.services.validation.ValidationMessage;
import com.fronde.server.services.validation.ValidationService;
import com.fronde.server.utils.BaseEntityUtils;
import com.fronde.server.utils.CSVExportUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public abstract class OptionListBaseService {

  @Autowired
  protected OptionListRepository repository;

  @Autowired
  protected ValidationService validationService;

  @Autowired
  protected RevisionService revisionService;

  @Autowired
  protected CSVExportUtils exportUtils;

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  public OptionListEntity findById(String docId) {
    return repository.findById(docId).get();
  }

  public Response<OptionListEntity> save(OptionListEntity entity) {
    List<ValidationMessage> messages = validationService.validateEntity(entity);
    if (messages.size() == 0) {
      if (entity.getId() == null) {
        entity.setId(objectIdFactory.create());
      }

      if (entity.getDocType() == null) {
        entity.setDocType("OptionList");
      }

      BaseEntityUtils.setModifiedFields(entity);

      revisionService.createRevision(entity);
      entity = repository.save(entity);
    }

    Response<OptionListEntity> response = new Response<>(entity);
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

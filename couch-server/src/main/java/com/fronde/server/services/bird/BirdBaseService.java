package com.fronde.server.services.bird;

import com.fronde.server.domain.BirdEntity;
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
public abstract class BirdBaseService {

  @Autowired
  protected BirdRepository repository;

  @Autowired
  protected ValidationService validationService;

  @Autowired
  protected RevisionService revisionService;

  @Autowired
  protected CSVExportUtils exportUtils;

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  public BirdEntity findById(String docId) {
    // using orElse instead of get so we return null if the entity is not found
    return repository.findById(docId).orElse(null);
  }

  public PagedResponse<BirdEntity> search(BirdCriteria criteria) {
    return repository.findByCriteria(criteria);
  }

  public Response<BirdEntity> save(BirdEntity entity) {
    List<ValidationMessage> messages = validationService.validateEntity(entity);
    if (messages.size() == 0) {
      if (entity.getId() == null) {
        entity.setId(objectIdFactory.create());
      }

      if (entity.getDocType() == null) {
        entity.setDocType("Bird");
      }

      BaseEntityUtils.setModifiedFields(entity);

      revisionService.createRevision(entity);
      entity = repository.save(entity);
    }

    Response<BirdEntity> response = new Response<>(entity);
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

  public void export(BirdCriteria criteria, HttpServletResponse response) {
    List<String> header = Arrays.asList(
        "House Name", "House ID", "Current Island", "Current Location", "Sex", "Alive",
        "Age Class", "Age", "Transmitter group");
    List<String> props = Arrays.asList(
        "birdName", "houseID", "currentIsland", "currentLocation", "sex", "alive",
        "ageClass", "ageInDays", "transmitterGroup");
    criteria.setPageSize(exportUtils.getMaxRows());
    criteria.setPageNumber(1);
    PagedResponse<BirdEntity> pr = this.search(criteria);
    exportUtils.export(response, pr, header, props, "bird");
  }

}

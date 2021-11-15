package com.fronde.server.services.sample;

import com.fronde.server.domain.SampleEntity;
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
public abstract class SampleBaseService {

  @Autowired
  protected SampleRepository repository;

  @Autowired
  protected ValidationService validationService;

  @Autowired
  protected RevisionService revisionService;

  @Autowired
  protected CSVExportUtils exportUtils;

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  public SampleEntity findById(String docId) {
    // using orElse instead of get so we return null if the entity is not found
    return repository.findById(docId).orElse(null);
  }

  public PagedResponse<SampleEntity> search(SampleCriteria criteria) {
    return repository.findByCriteria(criteria);
  }

  public Response<SampleEntity> save(SampleEntity entity) {
    List<ValidationMessage> messages = validationService.validateEntity(entity);
    if (messages.size() == 0) {
      if (entity.getId() == null) {
        entity.setId(objectIdFactory.create());
      }

      if (entity.getDocType() == null) {
        entity.setDocType("Sample");
      }

      BaseEntityUtils.setModifiedFields(entity);

      revisionService.createRevision(entity);
      entity = repository.save(entity);
    }

    Response<SampleEntity> response = new Response<>(entity);
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

  public void export(SampleCriteria criteria, HttpServletResponse response) {
    List<String> header = Arrays.asList(
        "Sample ID", "Bird", "Sample Category", "Sample Type", "Container", "Taken By",
        "Collection Date", "Island", "Location", "Haem", "Chem", "M&P", "Sperm");
    List<String> props = Arrays.asList(
        "sampleName", "birdID", "sampleCategory", "sampleType", "container",
        "sampleTakenBy", "collectionDate", "collectionIsland", "collectionLocationID",
        "hasHaematologyTests", "hasChemistryAssays", "hasMicrobiologyAndParasitologyTests",
        "hasSpermMeasures");
    criteria.setPageSize(exportUtils.getMaxRows());
    criteria.setPageNumber(1);
    PagedResponse<SampleEntity> pr = this.search(criteria);
    exportUtils.export(response, pr, header, props, "sample");
  }

}

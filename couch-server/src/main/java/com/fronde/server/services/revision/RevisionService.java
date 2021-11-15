package com.fronde.server.services.revision;

import com.fronde.server.domain.BaseEntity;
import com.fronde.server.domain.RevisionList;
import com.fronde.server.migration.MigrationUtils;
import com.rits.cloning.Cloner;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class RevisionService {

  @Autowired
  protected RevisionComparator revisionComparator;

  @Autowired
  protected Cloner cloner;

  @Autowired
  protected MigrationUtils migrationUtils;

  @Autowired
  protected RevisionRepository revisionRepository;

  public RevisionList findRevisionList(String entityId) {

    BaseEntityDTO baseEntityDTO = revisionRepository.findBaseEntity(entityId);
    String entityDocType = baseEntityDTO.getDocType();
    Class<? extends BaseEntity> entityClass = findEntityClass(baseEntityDTO);

    String revisionDocType = "Revision" + entityDocType;
    List revisions = revisionRepository.findRevisions(entityId, revisionDocType, entityClass);

    // Whether or not we add the current entity will depend on the data migration strategy. If we can figure out
    // a way of not having to create a revision during data migration then we will need to add the current entity
    //BaseEntity entity = template.findById(entityId, entityClass);
    //revisions.add(entity);

    Collections.sort(revisions, revisionComparator);

    RevisionList revisionList = new RevisionList();
    //revisionList.setDocType("RevisionList");
    revisionList.setEntityId(entityId);
    revisionList.setEntityDocType(entityDocType);
    revisionList.setRevisions(revisions);

    return revisionList;
  }


  private Class findEntityClass(BaseEntityDTO baseEntityDTO) {
    try {
      String entityDocType = baseEntityDTO.getDocType();
      String entityClassname = "com.fronde.server.domain." + entityDocType + "Entity";
      Class entityClass = Class.forName(entityClassname);
      return entityClass;
    } catch (ClassNotFoundException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * This method is the 99% use case for adding a revision to the revisionList
   *
   * @param entity
   */
  public void createRevision(BaseEntity entity) {
    doCreateRevision(entity);
  }

  /**
   * This method is the 99% use case for adding a revision to the revisionList
   * <p>
   * entity must have modified time set
   *
   * @param entity
   */
  private void doCreateRevision(BaseEntity entity) {
    assert entity.getModifiedTime() != null;

    if (entity.getId() == null) {
      throw new NullPointerException(
          "Entity id must not be null for " + entity.getClass().getName());
    }

    String entityId = entity.getId();
    String docType = entity.getDocType();
    Date modifiedTime = entity.getModifiedTime();

    String revisionId = entityId + ":" + modifiedTime.getTime();
    String revisionDocType = "Revision" + docType;

    BaseEntity revision = cloner.deepClone(entity);

    revision.setId(revisionId);
    revision.setDocType(revisionDocType);

    revisionRepository.saveRevision(revision);
  }

}

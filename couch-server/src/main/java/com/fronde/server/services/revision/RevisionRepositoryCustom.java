package com.fronde.server.services.revision;

import com.fronde.server.domain.BaseEntity;
import java.util.List;

/**
 *
 */
public interface RevisionRepositoryCustom {

  BaseEntityDTO findBaseEntity(String entityId);

  List findRevisions(String entityId, String revisionDocType, Class entityClass);

  void saveRevision(BaseEntity entityRevision);
}

package com.fronde.server.services.revision;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.path;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.functions.MetaFunctions.meta;

import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.Expression;
import com.fronde.server.domain.BaseEntity;
import com.fronde.server.utils.QueryUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.repository.query.support.N1qlUtils;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class RevisionRepositoryImpl implements RevisionRepositoryCustom {

  @Autowired
  protected CouchbaseTemplate template;

  @Autowired
  protected QueryUtils queryUtils;

  @Override
  public BaseEntityDTO findBaseEntity(String entityId) {
    Expression bucket = i(template.getCouchbaseBucket().name());
    Expression metaId = path(meta(bucket), "id");

    // no need to filter by syncID because query is a lookup by ID
    Statement statement = select(
        metaId.as("id"),
        i("docType"),
        i("modifiedTime"),
        i("modifiedByPersonId")
    )
        .from(i(template.getCouchbaseBucket().name()))
        .where(metaId.eq(s(entityId)));

    N1qlQuery query = N1qlQuery.simple(statement);
    List<BaseEntityDTO> baseEntityList = template.findByN1QLProjection(query, BaseEntityDTO.class);

    if (baseEntityList != null && baseEntityList.size() == 1) {
      return baseEntityList.get(0);
    } else if (baseEntityList == null || baseEntityList.size() == 0) {
      throw new RuntimeException("Unable to find entity for entityId = " + entityId);
    } else {
      throw new RuntimeException(
          "Found more than one match for entityId = " + entityId + " of " + baseEntityList.size()
              + " items");
    }
  }

  @Override
  public List findRevisions(String entityId, String revisionDocType, Class entityClass) {
    Expression bucket = i(template.getCouchbaseBucket().name());
    Expression metaId = path(meta(bucket), "id");

    String revisionIdMatcher = entityId + ":%";
    Expression e = i("docType").eq(s(revisionDocType));
    e = e.and(metaId.like(s(revisionIdMatcher)));
    e = e.and(queryUtils.notLikeSyncId());

    Statement statement = N1qlUtils
        .createSelectClauseForEntity(template.getCouchbaseBucket().name())
        .from(bucket)
        .where(e);

    N1qlQuery query = N1qlQuery.simple(statement);
    List revisions = template.findByN1QL(query, entityClass);

    return revisions;
  }

  @Override
  public void saveRevision(BaseEntity entityRevision) {
    template.save(entityRevision);
  }
}

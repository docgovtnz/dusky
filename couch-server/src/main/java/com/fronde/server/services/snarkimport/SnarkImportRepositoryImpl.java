package com.fronde.server.services.snarkimport;

import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;

import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.Expression;
import com.fronde.server.domain.SnarkImportEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.utils.QueryUtils;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.repository.query.support.N1qlUtils;
import org.springframework.stereotype.Component;

@Component
public class SnarkImportRepositoryImpl implements SnarkImportRepositoryCustom {

  @Autowired
  protected CouchbaseTemplate template;

  @Autowired
  protected QueryUtils queryUtils;

  private final String wildcard = "%";

  @Override
  public List<SnarkImportEntity> findBySnarkFileHash(String hash) {
    Expression e = i("docType").eq(s("SnarkImport"));
    e = e.and(queryUtils.notLikeSyncId());

    e = e.and(x("snarkFileHash").eq(s(hash)));

    Statement statement = N1qlUtils
        .createSelectClauseForEntity(template.getCouchbaseBucket().name())
        .from(i(template.getCouchbaseBucket().name()))
        .where(e);

    N1qlQuery query = N1qlQuery.simple(statement);
    List<SnarkImportEntity> list = template.findByN1QL(query, SnarkImportEntity.class);
    return list;
  }

  @Override
  public PagedResponse<SnarkImportEntity> findByCriteria(SnarkImportCriteria criteria) {
    Expression e = i("docType").eq(s("SnarkImport"));
    e = e.and(queryUtils.notLikeSyncId());

    PagedResponse<SnarkImportEntity> pagedResponse = null;//queryUtils.query(criteria, e, SnarkImportEntity.class,

    return pagedResponse;
  }

  @Override
  public List<SnarkImportEntity> findBySnarkFile(String snarkFile) {
    // TODO implement
    return Collections.emptyList();
  }

}
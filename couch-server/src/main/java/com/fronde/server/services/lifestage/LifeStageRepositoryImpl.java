package com.fronde.server.services.lifestage;

import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.s;

import com.couchbase.client.java.query.dsl.Expression;
import com.fronde.server.domain.LifeStageEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.utils.QueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LifeStageRepositoryImpl implements LifeStageRepositoryCustom {

  @Autowired
  protected QueryUtils queryUtils;

  private final String wildcard = "%";

  public PagedResponse<LifeStageEntity> findByCriteria(LifeStageCriteria criteria) {
    Expression e = i("docType").eq(s("LifeStage"));
    e = e.and(queryUtils.notLikeSyncId());

    PagedResponse<LifeStageEntity> pagedResponse = queryUtils
        .query(criteria, e, LifeStageEntity.class);

    return pagedResponse;
  }

}
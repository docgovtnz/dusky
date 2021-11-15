package com.fronde.server.services.setting;

import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.functions.StringFunctions.lower;

import com.couchbase.client.java.query.dsl.Expression;
import com.couchbase.client.java.query.dsl.Sort;
import com.fronde.server.domain.SettingEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.utils.QueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SettingRepositoryImpl implements SettingRepositoryCustom {

  @Autowired
  protected QueryUtils queryUtils;

  private final String wildcard = "%";

  public PagedResponse<SettingEntity> findByCriteria(SettingCriteria criteria) {
    Expression e = i("docType").eq(s("Setting"));
    e = e.and(queryUtils.notLikeSyncId());

    if (!StringUtils.isEmpty(criteria.getName())) {
      e = e.and(lower(x("name")).like(lower(s(criteria.getName() + wildcard))));
    }

    PagedResponse<SettingEntity> pagedResponse = queryUtils
        .query(criteria, e, SettingEntity.class, Sort.asc("name"));

    return pagedResponse;
  }

}

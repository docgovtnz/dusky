package com.fronde.server.services.island;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.Select.selectDistinct;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.path;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.functions.MetaFunctions.meta;
import static com.couchbase.client.java.query.dsl.functions.StringFunctions.lower;
import static com.couchbase.client.java.query.dsl.functions.TypeFunctions.toNumber;

import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.Expression;
import com.couchbase.client.java.query.dsl.Sort;
import com.fronde.server.domain.IslandEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.utils.QueryUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.repository.query.support.N1qlUtils;
import org.springframework.stereotype.Component;

@Component
public class IslandRepositoryImpl implements IslandRepositoryCustom {

  @Autowired
  protected CouchbaseTemplate template;

  @Autowired
  protected QueryUtils queryUtils;

  private final String wildcard = "%";

  public PagedResponse<IslandEntity> findByCriteria(IslandCriteria criteria) {
    Expression e = i("docType").eq(s("Island"));
    e = e.and(queryUtils.notLikeSyncId());

    if (criteria.getName() != null) {
      e = e.and(lower(i("name")).like(lower(s(criteria.getName() + wildcard))));
    }
    if (criteria.getIslandId() != null) {
      e = e.and(x("islandId").eq(toNumber(x(criteria.getIslandId()))));
    }

    PagedResponse<IslandEntity> pagedResponse = queryUtils
        .query(criteria, e, IslandEntity.class, Sort.asc("name"));

    return pagedResponse;
  }

  @Override
  public IslandEntity findOneByExactName(String islandName) {
    Expression e = i("docType").eq(s("Island"));
    e = e.and(queryUtils.notLikeSyncId());

    if (islandName != null) {
      e = e.and(lower(i("name")).like(lower(s(islandName))));
    }

    Statement statement = N1qlUtils
        .createSelectClauseForEntity(template.getCouchbaseBucket().name())
        .from(i(template.getCouchbaseBucket().name()))
        .where(e);
    N1qlQuery query = N1qlQuery.simple(statement);
    List<IslandEntity> list = template.findByN1QL(query, IslandEntity.class);

    IslandEntity island;
    if (list.size() == 0) {
      island = null;
    } else if (list.size() == 1) {
      island = list.get(0);
    } else {
      throw new RuntimeException("Found more than one Island for name " + islandName
          + " but only expected a single island");
    }

    return island;
  }

  @Override
  public List<String> findIslandNames() {
    Expression e = i("docType").eq(s("Island"));
    e = e.and(queryUtils.notLikeSyncId());

    Statement statement = selectDistinct(i("name"))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e)
        .orderBy(Sort.asc("name"));

    N1qlQuery query = N1qlQuery.simple(statement);

    List<String> list = new ArrayList<>();
    template.queryN1QL(query).forEach(n1qlQueryRow -> {
      list.add(n1qlQueryRow.value().getString("name"));
    });

    return list;
  }

  @Override
  public boolean existsByIslandIdExcluding(Integer islandId, String excludingMetaId) {
    Expression e = x("docType").eq(s("Island"));
    e = e.and(queryUtils.notLikeSyncId());
    e = e.and(x("islandId").eq(toNumber(x(islandId))));
    e = e.and(path(meta(""), i("id")).ne(s(excludingMetaId)));

    Statement s = select(path(meta(""), i("id")))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e);

    N1qlQuery query = N1qlQuery.simple(s);

    List<N1qlQueryRow> rows = template.queryN1QL(query).allRows();

    return !rows.isEmpty();
  }

  @Override
  public boolean existsByIslandNameExcluding(String islandName, String excludingMetaId) {
    Expression e = x("docType").eq(s("Island"));
    e = e.and(queryUtils.notLikeSyncId());
    e = e.and(lower(i("name")).like(lower(s(islandName))));
    e = e.and(path(meta(""), i("id")).ne(s(excludingMetaId)));

    Statement s = select(path(meta(""), i("id")))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e);

    N1qlQuery query = N1qlQuery.simple(s);

    List<N1qlQueryRow> rows = template.queryN1QL(query).allRows();

    return !rows.isEmpty();
  }

}
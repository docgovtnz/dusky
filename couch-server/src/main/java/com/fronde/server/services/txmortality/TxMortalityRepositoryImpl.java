package com.fronde.server.services.txmortality;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.Select.selectDistinct;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.path;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.functions.AggregateFunctions.count;
import static com.couchbase.client.java.query.dsl.functions.MetaFunctions.meta;
import static com.couchbase.client.java.query.dsl.functions.StringFunctions.lower;

import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.Expression;
import com.couchbase.client.java.query.dsl.Sort;
import com.fronde.server.domain.TxMortalityEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.utils.QueryUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.repository.query.support.N1qlUtils;
import org.springframework.stereotype.Component;

@Component
public class TxMortalityRepositoryImpl implements TxMortalityRepositoryCustom {

  @Autowired
  protected QueryUtils queryUtils;

  @Autowired
  protected CouchbaseTemplate template;

  private final String wildcard = "%";

  public PagedResponse<TxMortalityEntity> findByCriteria(TxMortalityCriteria criteria) {
    Expression e = i("docType").eq(s("TxMortality"));
    e = e.and(queryUtils.notLikeSyncId());

    if (criteria.getName() != null) {
      e = e.and(lower(i("name")).like(lower(s(criteria.getName() + wildcard))));
    }

    PagedResponse<TxMortalityEntity> pagedResponse = queryUtils
        .query(criteria, e, TxMortalityEntity.class, Sort.asc("name"));

    return pagedResponse;
  }

  public List<TxMortalityEntity> getAllTxMortalityOptions() {
    Expression e = i("docType").eq(s("TxMortality"));
    e = e.and(queryUtils.notLikeSyncId());

    Statement statement = N1qlUtils
        .createSelectClauseForEntity(template.getCouchbaseBucket().name())
        .from(i(template.getCouchbaseBucket().name()))
        .where(e);

    N1qlQuery query = N1qlQuery.simple(statement);
    List<TxMortalityEntity> list = template.findByN1QL(query, TxMortalityEntity.class);

    return list;
  }

  public List<String> getTxMortalityTypes() {
    Expression e = i("docType").eq(s("TxMortality"));
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

  public boolean hasReferences(String txMortalityId) {
    Expression bucket = i(template.getCouchbaseBucket().name());
    Statement statement = select(count(path(meta(""), "id")).gt(0).as("hasReferences"))
        .from(bucket)
        .where(i("docType").eq(s("Transmitter")).and(i("txMortalityId").eq(s(txMortalityId))));

    N1qlQuery query = N1qlQuery.simple(statement);

    return template.queryN1QL(query).allRows().get(0).value().getBoolean("hasReferences");
  }

  public boolean existsByNameExcluding(String name, String excludingMetaId){
    Expression e = i("docType").eq(s("TxMortality"));
    e = e.and(queryUtils.notLikeSyncId());
    e = e.and(lower(i("name")).like(lower(s(name))));
    e = e.and(path(meta(""), i("id")).ne(s(excludingMetaId)));

    Statement s = select(path(meta(""), i("id")))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e);

    N1qlQuery query = N1qlQuery.simple(s);

    List<N1qlQueryRow> rows = template.queryN1QL(query).allRows();

    return !rows.isEmpty();
  }
}
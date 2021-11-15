package com.fronde.server.services.person;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.Select.selectDistinct;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.par;
import static com.couchbase.client.java.query.dsl.Expression.path;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.sub;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.functions.AggregateFunctions.count;
import static com.couchbase.client.java.query.dsl.functions.Collections.anyIn;
import static com.couchbase.client.java.query.dsl.functions.MetaFunctions.meta;
import static com.couchbase.client.java.query.dsl.functions.StringFunctions.lower;

import com.couchbase.client.java.query.N1qlParams;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.consistency.ScanConsistency;
import com.couchbase.client.java.query.dsl.Expression;
import com.couchbase.client.java.query.dsl.Sort;
import com.fronde.server.domain.PersonEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.options.PersonSummaryDTO;
import com.fronde.server.utils.QueryUtils;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.repository.query.support.N1qlUtils;
import org.springframework.stereotype.Component;

@Component
public class PersonRepositoryImpl implements PersonRepositoryCustom {

  @Autowired
  protected CouchbaseTemplate template;

  @Autowired
  protected QueryUtils queryUtils;

  private final String wildcard = "%";

  @Override
  public PersonEntity findOneByUserName(String userName) {
    Expression e = i("docType").eq(s("Person"));
    e = e.and(queryUtils.notLikeSyncId());

    e = e.and(x("lower(userName)").eq(s(userName.toLowerCase())));

    Statement statement = N1qlUtils
        .createSelectClauseForEntity(template.getCouchbaseBucket().name())
        .from(i(template.getCouchbaseBucket().name()))
        .where(e);

    N1qlQuery query = N1qlQuery.simple(statement);
    List<PersonEntity> list = template.findByN1QL(query, PersonEntity.class);

    PersonEntity entity = null;
    if (list != null && list.size() > 0) {
      if (list.size() == 1) {
        entity = list.get(0);
      } else {
        throw new RuntimeException(
            "Expected one PersonEntity for userName of " + userName + " but got " + list.size());
      }
    }

    return entity;
  }

  public PagedResponse<PersonEntity> findByCriteria(PersonCriteria criteria) {
    Expression e = x("docType").eq(s("Person"));
    e = e.and(queryUtils.notLikeSyncId());

    if (criteria.getName() != null) {
      e = e.and(lower(i("name")).like(lower(s(criteria.getName() + wildcard))));
    }

    if (criteria.getActiveOnly() != null && criteria.getActiveOnly()) {
      String startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
          .withZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
      e = e.and(par(i("accountExpiry").isNotValued().or(i("accountExpiry").gte(s(startOfDay)))));
    }

    if (criteria.getAccountOnly() != null && criteria.getAccountOnly()) {
      e = e.and(i("hasAccount"));
    }

    if (criteria.getName() != null) {
      e = e.and(lower(i("name")).like(lower(s(criteria.getName() + wildcard))));
    }

    if (criteria.getCurrentCapacity() != null) {
      e = e.and(
          lower(i("currentCapacity")).like(lower(s(criteria.getCurrentCapacity() + wildcard))));
    }

    if (criteria.getPersonRole() != null) {
      e = e.and(lower(i("personRole")).like(lower(s(criteria.getPersonRole() + wildcard))));
    }

    PagedResponse<PersonEntity> pagedResponse = queryUtils.query(criteria, e, PersonEntity.class,
        Sort.asc("name"), Sort.asc("phoneNumber"), Sort.asc("personRole"),
        Sort.asc("accountExpiry"));
    return pagedResponse;
  }

  @Override
  public PagedResponse<PersonSearchDTO> findSearchDTOByCriteria(PersonCriteria criteria) {
    Expression e = x("docType").eq(s("Person"));
    e = e.and(queryUtils.notLikeSyncId());

    if (criteria.getName() != null) {
      e = e.and(lower(i("name")).like(lower(s(criteria.getName() + wildcard))));
    }

    String startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        .withZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    if (criteria.getActiveOnly() != null && criteria.getActiveOnly()) {
      e = e.and(par(i("accountExpiry").isNotValued().or(i("accountExpiry").gte(s(startOfDay)))));
    }

    if (criteria.getAccountOnly() != null && criteria.getAccountOnly()) {
      e = e.and(i("hasAccount"));
    }

    if (criteria.getName() != null) {
      e = e.and(lower(i("name")).like(lower(s(criteria.getName() + wildcard))));
    }

    if (criteria.getCurrentCapacity() != null) {
      e = e.and(
          lower(i("currentCapacity")).like(lower(s(criteria.getCurrentCapacity() + wildcard))));
    }

    if (criteria.getPersonRole() != null) {
      e = e.and(lower(i("personRole")).like(lower(s(criteria.getPersonRole() + wildcard))));
    }

    Statement statement = select(
        path(meta(""), "id").as("personID"),
        i("accountExpiry"),
        i("currentCapacity"),
        i("hasAccount"),
        i("name"),
        i("personRole"),
        i("phoneNumber"),
        i("hasAccount").and(i("accountExpiry").isValued()).and(i("accountExpiry").lt(s(startOfDay)))
            .as("expired"))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e)
        .orderBy(Sort.asc("name"), Sort.asc("phoneNumber"), Sort.asc("personRole"),
            Sort.asc("accountExpiry"))
        .limit(criteria.getPageSize())
        .offset(criteria.getOffset());

    N1qlQuery query = N1qlQuery.simple(statement);

    List<PersonSearchDTO> list = template.findByN1QLProjection(query, PersonSearchDTO.class);

    Statement countStatement = select(count(x("*")).as("count"))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e);

    N1qlQuery countQuery = N1qlQuery.simple(countStatement);

    Integer totalCount = (Integer) template.queryN1QL(countQuery).allRows().get(0).value()
        .get("count");

    PagedResponse pagedResponse = new PagedResponse();
    pagedResponse.setPage(criteria.getPageNumber());
    pagedResponse.setPageSize(criteria.getPageSize());
    pagedResponse.setTotal(totalCount);
    pagedResponse.setResults(list);

    return pagedResponse;
  }

  @Override
  public List<String> findUsers() {
    Expression e = x("docType").eq(s("Person"));
    e = e.and(queryUtils.notLikeSyncId());

    e = e.and(par(x("personRole").isMissing().or(x("personRole").ne(s("Data Replication")))));

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
  public boolean hasReferences(String personID) {
    Expression bucket = i(template.getCouchbaseBucket().name());
    Statement statement = select(count(i("u")).gt(0).as("hasReferences"))
        .from(sub(select(path(meta(""), "id")).from(bucket)
            .where(i("modifiedByPersonId").eq(s(personID)).and(queryUtils.notLikeSyncId()))
            .unionAll()
            .select(path(meta(""), "id")).from(bucket).where(i("docType").eq(s("Record")).and(
                anyIn("o", i("observerList"))
                    .satisfies(path(i("o"), i("personID")).eq(s(personID)))))
        ).as("u"));

    N1qlQuery query = N1qlQuery.simple(statement);

    return template.queryN1QL(query).allRows().get(0).value().getBoolean("hasReferences");
  }

  @Override
  public List<PersonSummaryDTO> findPersonSummaries() {
    Expression bucket = i(template.getCouchbaseBucket().name());
    Expression metaId = path(meta(bucket), "id");
    Expression e = x("docType").eq(s("Person"));
    e = e.and(queryUtils.notLikeSyncId());

    e = e.and(par(x("personRole").isMissing().or(x("personRole").ne(s("Data Replication")))));

    Statement statement = select(
        metaId.as("id"),
        i("name").as("personName")
    )
        .from(bucket)
        .where(e)
        .orderBy(Sort.asc(i("name")));

    N1qlQuery query = N1qlQuery
        .simple(statement, N1qlParams.build().consistency(ScanConsistency.REQUEST_PLUS));
    List<PersonSummaryDTO> summaryList = template
        .findByN1QLProjection(query, PersonSummaryDTO.class);

    return summaryList;
  }

  @Override
  public boolean existsByNameExcluding(String name, String excludingPersonID) {
    Expression e = x("docType").eq(s("Person"));
    e = e.and(queryUtils.notLikeSyncId());
    e = e.and(lower(i("name")).eq(lower(s(name))));
    e = e.and(path(meta(""), i("id")).ne(s(excludingPersonID)));

    Statement s = select(path(meta(""), i("id")))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e);

    N1qlQuery query = N1qlQuery.simple(s);

    List<N1qlQueryRow> rows = template.queryN1QL(query).allRows();
    return !rows.isEmpty();
  }

  @Override
  public boolean existsByUsernameExcluding(String username, String excludingPersonID) {
    Expression e = x("docType").eq(s("Person"));
    e = e.and(queryUtils.notLikeSyncId());
    e = e.and(lower(i("userName")).eq(lower(s(username))));
    e = e.and(path(meta(""), i("id")).ne(s(excludingPersonID)));

    Statement s = select(path(meta(""), i("id")))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e);

    N1qlQuery query = N1qlQuery.simple(s);

    List<N1qlQueryRow> rows = template.queryN1QL(query).allRows();
    return !rows.isEmpty();
  }

}

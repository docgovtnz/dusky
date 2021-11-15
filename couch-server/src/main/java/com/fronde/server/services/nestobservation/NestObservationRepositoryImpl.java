package com.fronde.server.services.nestobservation;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.par;
import static com.couchbase.client.java.query.dsl.Expression.path;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.functions.AggregateFunctions.count;
import static com.couchbase.client.java.query.dsl.functions.Collections.anyIn;
import static com.couchbase.client.java.query.dsl.functions.StringFunctions.lower;

import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.Expression;
import com.fronde.server.domain.NestObservationEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.utils.QueryUtils;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.repository.query.support.N1qlUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class NestObservationRepositoryImpl implements NestObservationRepositoryCustom {

  @Autowired
  protected QueryUtils queryUtils;

  @Autowired
  protected CouchbaseTemplate template;

  private final String wildcard = "%";

  public PagedResponse<NestObservationEntity> findByCriteria(NestObservationCriteria criteria) {
    Expression e = i("docType").eq(s("NestObservation"));
    e = e.and(queryUtils.notLikeSyncId());

    PagedResponse<NestObservationEntity> pagedResponse = null;

    return pagedResponse;
  }


  public PagedResponse<NestObservationSearchDTO> findSearchDTOByCriteria(
      NestObservationCriteria criteria) {
    String bucketName = "`" + template.getCouchbaseBucket().name() + "`";

    Expression e = x("no.docType").eq(s("NestObservation"));
    e = e.and(queryUtils.notLikeSyncId("no"));

    if (criteria.getBirdID() != null) {
      e = e.and(path("no", i("birdID")).eq(s(criteria.getBirdID())));
    }

    if (!StringUtils.isEmpty(criteria.getIsland())) {
      e = e.and(lower(path("l", i("island"))).eq(s(criteria.getIsland().toLowerCase())));
    }

    if (criteria.getLocationID() != null) {
      e = e.and(x("meta(l).id").eq(s(criteria.getLocationID())));
    }

    if (criteria.getFromDate() != null) {
      e = e.and(path("no", i("dateTime")).gte(
          s(criteria.getFromDate().toInstant().atZone(ZoneOffset.UTC)
              .format(DateTimeFormatter.ISO_INSTANT))));
    }

    if (criteria.getToDate() != null) {
      e = e.and(path("no", i("dateTime")).lte(
          s(criteria.getToDate().toInstant().atZone(ZoneOffset.UTC)
              .format(DateTimeFormatter.ISO_INSTANT))));
    }

    if (criteria.getLocationType() != null) {
      e = e
          .and(lower(path("l", i("locationType"))).eq(s(criteria.getLocationType().toLowerCase())));
    }

    if (criteria.getObserverPersonID() != null) {
      e = e.and(anyIn("o", x("no.observerList"))
          .satisfies(x("o.personID").eq(s(criteria.getObserverPersonID()))));
    }

    if (criteria.getChildBirdID() != null) {
      e = e.and(anyIn("child", x("children"))
          .satisfies(x("child.birdID").eq(s(criteria.getChildBirdID()))));
    }

    String fromClause = "  `kakapo-bird` no \n" +
        "  LEFT JOIN " + bucketName + " l ON KEYS no.locationID\n" +
        "  LEFT JOIN " + bucketName + " b ON KEYS no.birdID\n" +
        "  LEFT NEST " + bucketName
        + " eggRecords ON KEYS ARRAY x.recordID FOR x IN no.eggRecordReferenceList END\n" +
        "  LEFT NEST " + bucketName
        + " chickRecords ON KEYS ARRAY x.recordID FOR x IN no.chickRecordReferenceList END\n" +
        "  LEFT NEST " + bucketName
        + " observers ON KEYS ARRAY x.personID FOR x IN no.observerList END\n" +
        "  LET children = ARRAY_CONCAT(IFMISSINGORNULL(ARRAY { \"birdID\": y.birdID, \"birdName\": (SELECT birdName FROM `kakapo-bird` b2 USE KEYS y.birdID)[0].birdName } FOR y IN eggRecords END, []), IFMISSINGORNULL(ARRAY { \"birdID\": y.birdID, \"birdName\": (SELECT birdName FROM `kakapo-bird` b2 USE KEYS y.birdID)[0].birdName } FOR y IN chickRecords END, [])), \n"
        +
        "      observerList = ARRAY { \"personID\": meta(y).id, \"name\": y.name } FOR y IN observers END\n";

    String QUERY = "SELECT \n" +
        "  meta(no).id id,\n" +
        "  meta(l).id locationID,\n" +
        "  l.locationName,\n" +
        "  l.locationType,\n" +
        "  no.`dateTime` as `dateTime`,\n" +
        "  no.birdID,\n" +
        "  b.birdName, \n" +
        "  children,\n" +
        "  l.island,\n" +
        "  observerList as observers\n" +
        "FROM \n" +
        fromClause +
        "WHERE \n" +
        e.toString() +
        "\nORDER BY\n" +
        "  no.dateTime DESC,\n" +
        "  l.locationName ASC\n" +
        "LIMIT " + criteria.getPageSize() + "\n" +
        "OFFSET " + criteria.getOffset();

    N1qlQuery query = N1qlQuery.simple(QUERY);

    List<NestObservationSearchDTO> list = template
        .findByN1QLProjection(query, NestObservationSearchDTO.class);

    Integer totalCount = 0;

    if (!list.isEmpty()) {
      String COUNT_QUERY = "select count(results) as count from (SELECT \n" +
          "  meta(no).id id \n" +
          "FROM \n" +
          fromClause +
          "WHERE \n" +
          e + ") results";

      N1qlQuery countQuery = N1qlQuery.simple(COUNT_QUERY);

      totalCount = template.queryN1QL(countQuery).allRows().get(0).value().getInt("count");
    }

    PagedResponse pagedResponse = new PagedResponse();
    pagedResponse.setPage(criteria.getPageNumber());
    pagedResponse.setPageSize(criteria.getPageSize());
    pagedResponse.setTotal(totalCount);
    pagedResponse.setResults(list);

    return pagedResponse;
  }

  @Override
  public boolean hasRelatedNestObservation(String recordID) {

    Expression whereClause = x("no.docType").eq(s("NestObservation"));
    whereClause = whereClause.and(queryUtils.notLikeSyncId("no"));
    whereClause = whereClause.and(
        par(
            anyIn("ref", i("eggRecordReferenceList"))
                .satisfies(path("ref", i("recordID")).eq(s(recordID)))
                .or(anyIn("ref", i("chickRecordReferenceList"))
                    .satisfies(path("ref", i("recordID")).eq(s(recordID))))
        )
    );

    Statement stmt = select(count(i("no")).as("count"))
        .from(i(template.getCouchbaseBucket().name()).as("no")).where(whereClause);
    System.out.println(stmt);

    N1qlQuery countQuery = N1qlQuery.simple(stmt);
    Integer totalCount = (Integer) template.queryN1QL(countQuery).allRows().get(0).value()
        .get("count");

    return totalCount != null && totalCount.intValue() > 0;
  }

  @Override
  public NestObservationEntity findByRecordID(String recordID) {
    Expression whereClause = x("no.docType").eq(s("NestObservation"));
    whereClause = whereClause.and(queryUtils.notLikeSyncId("no"));
    whereClause = whereClause.and(
        par(
            anyIn("ref", i("eggRecordReferenceList"))
                .satisfies(path("ref", i("recordID")).eq(s(recordID)))
                .or(anyIn("ref", i("chickRecordReferenceList"))
                    .satisfies(path("ref", i("recordID")).eq(s(recordID))))
        )
    );

    Statement stmt = N1qlUtils.createSelectClauseForEntity("no")
        .from(i(template.getCouchbaseBucket().name()).as("no"))
        .where(whereClause);
    System.out.println(stmt);

    N1qlQuery query = N1qlQuery.simple(stmt);

    List<NestObservationEntity> list = template.findByN1QL(query, NestObservationEntity.class);

    if (list.isEmpty()) {
      return null;
    } else {
      return list.get(0);
    }
  }

}
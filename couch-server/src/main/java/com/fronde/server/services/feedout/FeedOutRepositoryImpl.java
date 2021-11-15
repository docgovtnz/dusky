package com.fronde.server.services.feedout;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.Select.selectRaw;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.par;
import static com.couchbase.client.java.query.dsl.Expression.path;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.sub;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.functions.AggregateFunctions.arrayAgg;
import static com.couchbase.client.java.query.dsl.functions.AggregateFunctions.count;
import static com.couchbase.client.java.query.dsl.functions.AggregateFunctions.distinct;
import static com.couchbase.client.java.query.dsl.functions.Collections.anyIn;
import static com.couchbase.client.java.query.dsl.functions.MetaFunctions.meta;
import static com.couchbase.client.java.query.dsl.functions.StringFunctions.lower;

import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.Expression;
import com.couchbase.client.java.query.dsl.Sort;
import com.fronde.server.domain.FeedOutEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.utils.QueryUtils;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeedOutRepositoryImpl implements FeedOutRepositoryCustom {

  @Autowired
  protected CouchbaseTemplate template;

  @Autowired
  protected QueryUtils queryUtils;

  private final String wildcard = "%";

  public PagedResponse<FeedOutEntity> findByCriteria(FeedOutCriteria criteria) {
    Expression e = i("docType").eq(s("FeedOut"));
    e = e.and(queryUtils.notLikeSyncId());

    if (criteria.getIsland() != null) {
      e = e.and(i("locationID").in(queryUtils.subquery(x("meta(sub).id"), "Location",
          lower(x("sub.island")).like(lower(s(criteria.getIsland() + wildcard))))));
    }

    if (criteria.getLocationID() != null) {
      e = e.and(i("locationID").eq(s(criteria.getLocationID())));
    }

    if (criteria.getBirdID() != null) {
      e = e.and(
          anyIn("t", i("targetBirdList")).satisfies(x("t.birdID").eq(s(criteria.getBirdID()))));
    }

    if (criteria.getSex() != null) {
      Expression birdCriteria = x("t.birdID").in(queryUtils.subquery(x("meta(sub).id"), "Bird",
          lower(x("sub.sex")).like(lower(s(criteria.getSex() + wildcard)))));
      e = e.and(anyIn("t", i("targetBirdList")).satisfies(birdCriteria));
    }

    if (criteria.getFromDate() != null) {
      String dateFrom = criteria.getFromDate().toInstant().atZone(ZoneOffset.UTC)
          .format(DateTimeFormatter.ISO_INSTANT);
      //e = e.and(par(i("dateIn").isNotValued().or(i("dateIn").gte(s(dateFrom)))));
      e = e.and(x("IFMISSING(`dateIn`, '9999-12-31')").gte(s(dateFrom)));
    }

    if (criteria.getToDate() != null) {
      String dateTo = criteria.getToDate().toInstant().atZone(ZoneOffset.UTC)
          .format(DateTimeFormatter.ISO_INSTANT);
      e = e.and(par(i("dateOut").isNotValued().or(i("dateOut").lte(s(dateTo)))));
    }

    if (criteria.getFood() != null) {
      e = e.and(anyIn("t", i("foodTallyList"))
          .satisfies(lower(x("t.name")).like(lower(s(criteria.getFood() + wildcard)))));
    }

    PagedResponse<FeedOutEntity> pagedResponse = queryUtils
        .query(criteria, e, FeedOutEntity.class, Sort.desc(i("dateOut")), Sort.desc(i("dateIn")));

    return pagedResponse;
  }

  public PagedResponse<FeedOutSearchDTO> findSearchDTOByCriteria(FeedOutCriteria criteria) {
    Expression bucketName = i(template.getCouchbaseBucket().name());
    Expression e = path("f", i("docType")).eq(s("FeedOut"));
    e = e.and(queryUtils.notLikeSyncId("f"));

    if (criteria.getIsland() != null) {
      e = e.and(path("f", i("locationID")).in(
          sub(selectRaw(
              path(meta("loc"), "id"))
              .from(bucketName.as("loc"))
              .where(
                  path("loc", i("docType")).eq(s("Location"))
                      .and(lower(path("loc", i("island")))
                          .like(lower(s(criteria.getIsland() + wildcard))))
              )
          )
      ));
    }

    if (criteria.getLocationID() != null) {
      e = e.and(path("f", i("locationID")).eq(s(criteria.getLocationID())));
    }

    if (criteria.getBirdID() != null) {
      e = e.and(anyIn("tbird", path("f", i("targetBirdList")))
          .satisfies(path("tbird", i("birdID")).eq(s(criteria.getBirdID()))));
    }

    if (criteria.getSex() != null) {
//            Expression birdCriteria = x("t.birdID").in(queryUtils.subquery(path(meta("s"), "id"), "Bird", lower(path("s", i("sex"))).like(lower(s(criteria.getSex() + wildcard)))));
      Expression birdCriteria = path("tbird", i("birdID")).in(
          sub(selectRaw(
              path(meta("b2"), "id"))
              .from(bucketName.as("b2"))
              .where(
                  path("b2", i("docType")).eq(s("Bird"))
                      .and(lower(path("b2", i("sex"))).like(lower(s(criteria.getSex() + wildcard))))
              )
          )
      );
      e = e.and(anyIn("tbird", path("f", i("targetBirdList"))).satisfies(birdCriteria));
    }

    if (criteria.getFromDate() != null) {
      String dateFrom = criteria.getFromDate().toInstant().atZone(ZoneOffset.UTC)
          .format(DateTimeFormatter.ISO_INSTANT);
      //e = e.and(par(path("f", i("dateIn")).isNotValued().or(path("f", i("dateIn")).gte(s(dateFrom)))));
      e = e.and(x("IFMISSING(f.`dateIn`, '9999-12-31')").gte(s(dateFrom)));
    }

    if (criteria.getToDate() != null) {
      String dateTo = criteria.getToDate().toInstant().atZone(ZoneOffset.UTC)
          .format(DateTimeFormatter.ISO_INSTANT);
      e = e.and(
          par(path("f", i("dateOut")).isNotValued().or(path("f", i("dateOut")).lte(s(dateTo)))));
    }

    if (criteria.getFood() != null) {
      e = e.and(anyIn("ftally", path("f", i("foodTallyList"))).satisfies(
          lower(path("ftally", i("name"))).like(lower(s(criteria.getFood() + wildcard)))));
    }

    Statement statement = select(
        path(meta("f"), "id").as("feedOutID"),
        path("f", i("dateOut")),
        path("f", i("dateIn")),
        path("f", i("locationID")),
        path("l", i("locationName")),
        arrayAgg("object_add(ft, \"consumed\", " + par(
            x(path("ft", i("out")) + "-" + path("ft", i("in")))) + ")").as("foodTallyList"),
        arrayAgg(distinct("object_add(tb, \"birdName\", " + path("b", i("birdName")) + ")"))
            .as("targetBirdList"))
        .from(bucketName).as("f")
        .leftJoin(bucketName).as("l").onKeys(path("f", i("locationID")))
        .unnest(path("f", i("foodTallyList"))).as("ft")
        .unnest(path("f", i("targetBirdList"))).as("tb")
        .leftJoin(bucketName).as("b").onKeys(path("tb", i("birdID")))
        .where(e)
        .groupBy("f", "l")
        .orderBy(Sort.desc(path("f", "dateOut")), Sort.desc(path("f", "dateIn")))
        .limit(criteria.getPageSize())
        .offset(criteria.getOffset());

    N1qlQuery query = N1qlQuery.simple(statement);

    List<FeedOutSearchDTO> list = template.findByN1QLProjection(query, FeedOutSearchDTO.class);

    Statement countStatement = select(count(i("f")).as("count"))
        .from(i(template.getCouchbaseBucket().name()).as("f"))
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

}
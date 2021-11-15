package com.fronde.server.services.location;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.Select.selectDistinct;
import static com.couchbase.client.java.query.dsl.Alias.alias;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.par;
import static com.couchbase.client.java.query.dsl.Expression.path;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.sub;
import static com.couchbase.client.java.query.dsl.Expression.x;

import static com.couchbase.client.java.query.dsl.functions.AggregateFunctions.count;
import static com.couchbase.client.java.query.dsl.functions.AggregateFunctions.max;
import static com.couchbase.client.java.query.dsl.functions.Case.caseSearch;
import static com.couchbase.client.java.query.dsl.functions.MetaFunctions.meta;
import static com.couchbase.client.java.query.dsl.functions.StringFunctions.lower;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.query.N1qlParams;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.consistency.ScanConsistency;
import com.couchbase.client.java.query.dsl.Expression;
import com.couchbase.client.java.query.dsl.Sort;
import com.fronde.server.domain.LocationEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.options.LocationSummaryDTO;
import com.fronde.server.utils.QueryUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Component;

@Component
public class LocationRepositoryImpl implements LocationRepositoryCustom {

  @Autowired
  protected CouchbaseTemplate template;

  @Autowired
  protected QueryUtils queryUtils;

  private final String wildcard = "%";
  private Expression par;

  public PagedResponse<LocationEntity> findByCriteria(LocationCriteria criteria) {
    Expression e = x("docType").eq(s("Location"));
    e = e.and(queryUtils.notLikeSyncId());

    if (criteria.getIsland() != null) {
      e = e.and(lower(i("island")).like(lower(s(criteria.getIsland() + wildcard))));
    }

    if (criteria.getLocationType() != null) {
      e = e.and(lower(i("locationType")).like(lower(s(criteria.getLocationType() + wildcard))));
    }

    if (criteria.getActiveOnly() != null && criteria.getActiveOnly()) {
      e = e.and(i("active"));
    }

    if (criteria.getLocationIDs() != null && criteria.getLocationIDs().size() > 0) {
      e = e.and(path(meta(""), "id").in(JsonArray.from(criteria.getLocationIDs())));
    }

        /*
        if (criteria.getLocationNames() != null && criteria.getLocationNames().size() > 0) {
            e = e.and( like("locationName", criteria.getLocationNames()));
        }
        */

    if (criteria.getBirdID() != null) {
      e = e.and(i("birdID").eq(s(criteria.getBirdID())));
    }

    PagedResponse<LocationEntity> pagedResponse = queryUtils
        .query(criteria, e, LocationEntity.class,
            Sort.asc("locationName"), Sort.asc("island"));
    return pagedResponse;
  }

  public PagedResponse<LocationSearchDTO> findSearchDTOByCriteria(LocationCriteria criteria) {
    Expression e = path("l", x("docType").eq(s("Location")));
    e = e.and(queryUtils.notLikeSyncId("l"));
    e = e.and(queryUtils.notLikeSyncId("b"));

    if (criteria.getIsland() != null) {
      e = e.and(lower(path("l", i("island"))).like(lower(s(criteria.getIsland() + wildcard))));
    }

    if (criteria.getLocationType() != null) {
      e = e.and(lower(path("l", i("locationType")))
          .like(lower(s(criteria.getLocationType() + wildcard))));
    }

    if (criteria.getActiveOnly() != null && criteria.getActiveOnly()) {
      e = e.and(path("l", i("active")));
    }

    if (criteria.getLocationIDs() != null && criteria.getLocationIDs().size() > 0) {
      e = e.and(path(meta(""), "id").in(JsonArray.from(criteria.getLocationIDs())));
    }

    if (criteria.getBirdID() != null) {
      e = e.and(path("l", i("birdID")).eq(s(criteria.getBirdID())));
    }

        /*
        if (criteria.getLocationNames() != null && criteria.getLocationNames().size() > 0) {
            e = e.and( like("locationName", criteria.getLocationNames()));
        }
        */

    Statement statement = select(
        path(meta("l"), "id").as("locationID"),
        path("l", i("locationName")),
        path("l", i("island")),
        path("l", i("locationType")),
        path("l", i("easting")),
        path("l", i("northing")),
        path("l", i("birdID")),
        path("b", i("birdName")),
        path("l", i("active")))
        .from(i(template.getCouchbaseBucket().name()).as("l"))
        .leftJoin(i(template.getCouchbaseBucket().name()).as("b")).onKeys(path("l", i("birdID")))
        .where(e)
        .orderBy(Sort.asc(path("l", "locationName")), Sort.asc(path("l", "island")))
        .limit(criteria.getPageSize())
        .offset(criteria.getOffset());

    N1qlQuery query = N1qlQuery.simple(statement);

    List<LocationSearchDTO> list = template.findByN1QLProjection(query, LocationSearchDTO.class);

    Statement countStatement = select(count(x("*")).as("count"))
        .from(i(template.getCouchbaseBucket().name()).as("l"))
        .leftJoin(i(template.getCouchbaseBucket().name()).as("b")).onKeys(path("l", i("birdID")))
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
  public List<LocationSummaryDTO> findLocationSummaries() {
    Expression bucket = i(template.getCouchbaseBucket().name());
    Expression metaId = path(meta(bucket), "id");
    Expression e = x("docType").eq(s("Location"));
    e = e.and(queryUtils.notLikeSyncId());

    Statement statement = select(
        metaId.as("id"),
        i("locationName"),
        caseSearch().when(i("active")).then(s("ACTIVE")).elseReturn(s("INACTIVE")).as("state"),
        i("island")
    )
        .from(i(template.getCouchbaseBucket().name()))
        .where(e)
        .orderBy(Sort.asc(i("state")), Sort.asc(i("locationName")));

    N1qlQuery query = N1qlQuery
        .simple(statement, N1qlParams.build().consistency(ScanConsistency.REQUEST_PLUS));
    List<LocationSummaryDTO> summaryList = template
        .findByN1QLProjection(query, LocationSummaryDTO.class);

    return summaryList;
  }

  @Override
  public List<String> findLocationNames() {
    Expression e = x("docType").eq(s("Location"));
    e = e.and(queryUtils.notLikeSyncId());

    Statement statement = selectDistinct(i("locationName"))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e)
        .orderBy(Sort.asc("locationName"));

    N1qlQuery query = N1qlQuery
        .simple(statement, N1qlParams.build().consistency(ScanConsistency.REQUEST_PLUS));

    List<String> list = new ArrayList<>();
    template.queryN1QL(query).forEach(n1qlQueryRow -> {
      list.add(n1qlQueryRow.value().getString("locationName"));
    });

    return list;
  }

  public boolean hasReferences(String locationID) {
    Expression bucket = i(template.getCouchbaseBucket().name());
    Statement statement = select(count(path(meta(""), "id")).gt(0).as("hasReferences"))
        .from(bucket)
        .where(
            par(
                i("docType").eq(s("Record")).or(i("docType").eq(s("FeedOut")))
                    .or(i("docType").eq(s("SnarkImport"))).or(i("docType").eq(s("SnarkActivity")))
            )
                .and(queryUtils.notLikeSyncId())
                .and(i("locationID").eq(s(locationID))));

    N1qlQuery query = N1qlQuery.simple(statement);

    return template.queryN1QL(query).allRows().get(0).value().getBoolean("hasReferences");
  }

  @Override
  public PagedResponse<String> getCurrentEggs(String locationID) {
    Expression bucket = i(template.getCouchbaseBucket().name());
    Statement s = select("x.birdID")
        .from(sub(select(path(meta("b"), i("id")).as("birdID"), path("b", i("birdName")),
            x("latestLs[1]").as("ageClass"))
            .from(bucket).as("b")
            .join(bucket.as("ls") + " ON KEY " + path("ls", i("birdID")) + " FOR " + "b")
            .where(path("b", i("docType")).eq(s("Bird"))
                .and(path("ls", i("docType")).eq(s("LifeStage")))
                .and(path("b", i("currentLocationID")).eq(s(locationID)))
                .and(
                    par(par(path("b", i("viable")).isValued()
                        .and(path("b", i("viable")).eq(s("infert"))))
                        .or(par(path("b", i("alive").isValued().and(path("b", i("alive")).not())))))
                        .not()))
            .groupBy("b")
            .letting(alias("latestLs", max("[ls.dateTime, ls.ageClass]"))))).as("x")
        .where(path("x", i("ageClass")).eq(s("Egg")))
        .orderBy(Sort.asc(path("x", i("birdName"))));

    N1qlQuery query = N1qlQuery.simple(s);

    List<String> list = new ArrayList<>();

    template.queryN1QL(query).forEach(n1qlQueryRow -> {
      list.add(n1qlQueryRow.value().getString("birdID"));
    });

    PagedResponse pr = new PagedResponse();
    pr.setResults(list);
    pr.setTotal(list.size());
    pr.setPageSize(list.size());
    pr.setPage(1);

    return pr;
  }

  @Override
  public PagedResponse<String> getCurrentChicks(String locationID) {
    Expression bucket = i(template.getCouchbaseBucket().name());
    Statement s = select("x.birdID")
        .from(sub(select(path(meta("b"), i("id")).as("birdID"), path("b", i("birdName")),
            x("latestLs[1]").as("ageClass"))
            .from(bucket).as("b")
            .join(bucket.as("ls") + " ON KEY " + path("ls", i("birdID")) + " FOR " + "b")
            .where(path("b", i("docType")).eq(s("Bird"))
                .and(path("ls", i("docType")).eq(s("LifeStage")))
                .and(path("b", i("currentLocationID")).eq(s(locationID)))
                .and(path("b", i("alive")))
                .and(par(path("b",
                    i("dateFledged").isMissing().or(path("b", i("dateFledged").isNull())))))
            )
            .groupBy("b")
            .letting(alias("latestLs", max("[ls.dateTime, ls.ageClass]"))))).as("x")
        .where(path("x", i("ageClass")).eq(s("Chick")))
        .orderBy(Sort.asc(path("x", i("birdName"))));

    N1qlQuery query = N1qlQuery.simple(s);

    List<String> list = new ArrayList<>();

    template.queryN1QL(query).forEach(n1qlQueryRow -> {
      list.add(n1qlQueryRow.value().getString("birdID"));
    });

    PagedResponse pr = new PagedResponse();
    pr.setResults(list);
    pr.setTotal(list.size());
    pr.setPageSize(list.size());
    pr.setPage(1);

    return pr;
  }

  @Override
  public Integer getNextClutchOrder(String locationID) {
    Expression bucket = i(template.getCouchbaseBucket().name());
    Bucket bkt = template.getCouchbaseBucket();
    // historic eggs won't have layLocations set. Check if any layLocations are missing and if so
    // return a next clutch order based on the old scheme -- number of eggs at this location + 1
    Statement counts =
        select(count("b.layLocationID").as("layLocationCount"), count("b.currentLocationID").as("currentLocationCount"))
            .from(bucket).as("b")
        .where(
            x("b.docType").eq(s("Bird"))
                .and(queryUtils.notLikeSyncId("b"))
                .and(par(x("b.currentLocationID").eq(s(locationID))
                    .or("b.layLocationID").eq(s(locationID)))));
    N1qlQueryRow row = template.queryN1QL(N1qlQuery.simple(counts)).allRows().get(0);
    int recsWithCurrentLocation = row.value().getInt("currentLocationCount");
    int recsWithLayLocation = row.value().getInt("layLocationCount");
    if (recsWithLayLocation != recsWithCurrentLocation) {
      return recsWithCurrentLocation + 1;
    }
    else{
      return recsWithLayLocation + 1;
    }
  }


  @Override
  public String getClutch(String locationID) {
    Expression bucket = i(template.getCouchbaseBucket().name());

    Statement s = select(path(i("nestDetails"), i("clutch")))
        .from(bucket).useKeys(s(locationID))
        .where(
            i("docType").eq(s("Location"))
                .and(queryUtils.notLikeSyncId())
        );

    N1qlQuery query = N1qlQuery.simple(s);

    List<N1qlQueryRow> rows = template.queryN1QL(query).allRows();
    if (rows.isEmpty()) {
      return null;
    } else {
      return rows.get(0).value().getString("clutch");
    }
  }

  public Expression like(String key, List<String> options) {
    Expression e = lower(x(key)).like(lower(s(options.get(0) + wildcard)));

    for (int i = 1; i < options.size(); i++) {
      e = e.or(lower(x(key)).like(lower(s(options.get(i) + wildcard))));
    }

    return par(e);
  }

  @Override
  public boolean existsByNameExcluding(String locationName, String excludingLocationID) {
    Expression e = x("docType").eq(s("Location"));
    e = e.and(queryUtils.notLikeSyncId());
    e = e.and(lower(i("locationName")).eq(lower(s(locationName))));
    e = e.and(path(meta(""), i("id")).ne(s(excludingLocationID)));

    Statement s = select(path(meta(""), i("id")))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e);

    N1qlQuery query = N1qlQuery.simple(s);

    List<N1qlQueryRow> rows = template.queryN1QL(query).allRows();
    return !rows.isEmpty();
  }

}

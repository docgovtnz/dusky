package com.fronde.server.services.snarkactivity;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.par;
import static com.couchbase.client.java.query.dsl.Expression.path;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.functions.AggregateFunctions.count;
import static com.couchbase.client.java.query.dsl.functions.ArrayFunctions.arrayDistinct;
import static com.couchbase.client.java.query.dsl.functions.Collections.anyIn;
import static com.couchbase.client.java.query.dsl.functions.MetaFunctions.meta;
import static com.couchbase.client.java.query.dsl.functions.StringFunctions.lower;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.Expression;
import com.couchbase.client.java.query.dsl.Sort;
import com.fronde.server.domain.SnarkActivityEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.utils.QueryUtils;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Component;

@Component
public class SnarkActivityRepositoryImpl implements SnarkActivityRepositoryCustom {

  @Autowired
  protected QueryUtils queryUtils;

  @Autowired
  protected CouchbaseTemplate template;

  private final String wildcard = "%";

  @Override
  public PagedResponse<SnarkActivityEntity> findByCriteria(SnarkActivityCriteria criteria) {
    return null;
  }

  public PagedResponse<SnarkActivitySearchDTO> findSearchDTOByCriteria(
      SnarkActivityCriteria criteria) {
    Expression e = i("docType").eq(s("SnarkActivity"));
    e = e.and(queryUtils.notLikeSyncId());

    if (criteria.getBirdID() != null) {
      // any sr in snarkRecordList satisfies sr.birdid = "f429d87d-8d87-4960-a62a-e2b8695410ff" end
      e = e.and(
          anyIn("sr", i("snarkRecordList")).satisfies(x("sr.birdID").eq(s(criteria.getBirdID()))));
    }

    Expression activityTypeOr = x("false");
    if (criteria.getIncludeTrackAndBowl() == Boolean.TRUE) {
      activityTypeOr = activityTypeOr.or(i("activityType").eq(s("Track and Bowl")));
    }
    if (criteria.getIncludeHopper() == Boolean.TRUE) {
      activityTypeOr = activityTypeOr.or(i("activityType").eq(s("Hopper")));
    }
    if (criteria.getIncludeNest() == Boolean.TRUE) {
      activityTypeOr = activityTypeOr.or(i("activityType").eq(s("Nest")));
    }
    if (criteria.getIncludeRoost() == Boolean.TRUE) {
      activityTypeOr = activityTypeOr.or(i("activityType").eq(s("Roost")));
    }
    e = e.and(par(activityTypeOr));

    if (criteria.getIsland() != null) {
      // sa.locationID in (select raw meta(l).id from `kakapo-bird` l where l.docType = 'Location' and l.island = 'Hauturu')
      // for performance reasons take advantage of the fact that there are relatively few locations
      // the database executes the query faster if we put ids into the
      Statement locIdsStatement = select(x("array_agg(meta(l).id)").as("ids"))
          .from(i(template.getCouchbaseBucket().name()).as("l")).where(
              x("l.docType").eq(s("Location"))
                  .and(lower(x("l.island")).like(lower(s(criteria.getIsland() + "%")))));
      JsonArray result = (JsonArray) template.queryN1QL(N1qlQuery.simple(locIdsStatement)).allRows()
          .get(0).value().get("ids");
      if (result != null) {
        e = e.and(i("locationID").in(result));
      } else {
        e = e.and(i("locationID").in((JsonArray.from(Collections.emptyList()))));
      }
    }
    if (criteria.getLocationID() != null) {
      e = e.and(i("locationID").eq(s(criteria.getLocationID())));
    }

    if (criteria.getFromDate() != null) {
      e = e.and(i("date").gte(s(criteria.getFromDate().toInstant().atZone(ZoneOffset.UTC)
          .format(DateTimeFormatter.ISO_INSTANT))));
    }

    if (criteria.getToDate() != null) {
      e = e.and(i("date").lt(s(criteria.getToDate().toInstant().atZone(ZoneOffset.UTC)
          .format(DateTimeFormatter.ISO_INSTANT))));
    }

    // get the ids of the matching documents first as it seems to be faster to break it up in this way
    Statement idsStatement = select(x("meta().id").as(i("id")))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e)
        .orderBy(Sort.desc("date"))
        .limit(criteria.getPageSize())
        .offset(criteria.getOffset());

    N1qlQuery idsQuery = N1qlQuery.simple(idsStatement);

    List<String> ids = new ArrayList<>();

    template.queryN1QL(idsQuery).forEach(result -> {
      ids.add(result.value().getString("id"));
    });

    List<SnarkActivitySearchDTO> results = null;
    if (ids.isEmpty()) {
      results = Collections.emptyList();
    } else {
      // Use the ids to get the results to populate the search dto.
      // Exempt from sync filter because where clause only picking from list of IDs above.
      Statement statement = select(
          x("meta(sa).id").as("snarkActivityId"),
          x("sa.activityType"),
          x("sa.date"),
          x("sa.locationID"),
          x("l.locationName"),
          x("sa.trackAndBowlActivity.boom"),
          x("sa.trackAndBowlActivity.ching"),
          x("sa.trackAndBowlActivity.sticks"),
          x("sa.trackAndBowlActivity.skraak"),
          x("sa.trackAndBowlActivity.trackActivity"),
          x("sa.trackAndBowlActivity.grubbing"),
          x("sa.trackAndBowlActivity.matingSign"),
          arrayDistinct(
              x("array {'id': meta(b2).id, 'name': b2.birdName } for b2 in array_agg(b1) END"))
              .as(i("birds"))
      ).from(i(template.getCouchbaseBucket().name())).as("sa")
          .leftUnnest(i("snarkRecordList").as("sr"))
          .leftJoin(i(template.getCouchbaseBucket().name())).as("b1").onKeys("sr.birdID")
          .leftJoin(i(template.getCouchbaseBucket().name())).as("l").onKeys("sa.locationID")
          .where(x("meta(sa).id").in(JsonArray.from(ids)))
          .groupBy(i("sa"), i("l"))
          .orderBy(Sort.desc("sa.date"));

      N1qlQuery query = N1qlQuery.simple(statement);

      results = template.findByN1QLProjection(query, SnarkActivitySearchDTO.class);
    }

    PagedResponse<SnarkActivitySearchDTO> pagedResponse = new PagedResponse<>(); //queryUtils.query(criteria, e, SnarkActivityEntity.class, Sort.desc("dateTime"));

    Integer totalCount = queryUtils.countRows(e);
    pagedResponse.setResults(results);
    pagedResponse.setTotal(totalCount);
    pagedResponse.setPage(criteria.getPageNumber());
    pagedResponse.setPageSize(criteria.getPageSize());

    return pagedResponse;
  }

  public boolean hasReferences(String transmitterID) {
    Expression bucket = i(template.getCouchbaseBucket().name());
    Statement statement = select(count(path(meta(""), "id")).gt(0).as("hasReferences"))
        .from(bucket).where(i("docType").eq(s("Record"))
            .and(path("snarkData", i("snarkActivityID")).eq(s(transmitterID))));

    N1qlQuery query = N1qlQuery.simple(statement);

    return template.queryN1QL(query).allRows().get(0).value().getBoolean("hasReferences");
  }

}
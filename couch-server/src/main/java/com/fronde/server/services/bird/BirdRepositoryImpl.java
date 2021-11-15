package com.fronde.server.services.bird;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.Select.selectDistinct;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.par;
import static com.couchbase.client.java.query.dsl.Expression.path;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.sub;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.functions.AggregateFunctions.count;
import static com.couchbase.client.java.query.dsl.functions.Case.caseSearch;
import static com.couchbase.client.java.query.dsl.functions.Collections.anyIn;
import static com.couchbase.client.java.query.dsl.functions.MetaFunctions.meta;
import static com.couchbase.client.java.query.dsl.functions.StringFunctions.lower;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlParams;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.consistency.ScanConsistency;
import com.couchbase.client.java.query.dsl.Expression;
import com.couchbase.client.java.query.dsl.Sort;
import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.LifeStageEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.options.BirdSummaryDTO;
import com.fronde.server.utils.QueryUtils;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.repository.query.support.N1qlUtils;
import org.springframework.stereotype.Component;

@Component
public class BirdRepositoryImpl implements BirdRepositoryCustom {

  @Autowired
  protected CouchbaseTemplate template;

  @Autowired
  protected QueryUtils queryUtils;

  private final String wildcard = "%";

  public PagedResponse<BirdEntity> findByCriteria(BirdCriteria criteria) {
    throw new UnsupportedOperationException(
        "Not implemented since determination of age class requires more advanced criteria");
  }

  public boolean hasReferences(String birdID) {
    Expression bucket = i(template.getCouchbaseBucket().name());
    Statement statement = select(count(i("u")).gt(0).as("hasReferences"))
        .from(sub(select(path(meta(""), "id")).from(bucket)
            .where(i("docType").eq(s("Record")).and(i("birdID").eq(s(birdID))))
            .unionAll()
            .select(path(meta(""), "id")).from(bucket)
            .where(i("docType").eq(s("Location")).and(i("birdID").eq(s(birdID))))
            .unionAll()
            // we haven't indexed mother, father, or nest mother as there are not that many birds
            .select(path(meta(""), "id")).from(bucket).where(i("docType").eq(s("Bird")).and(
                par(i("mother").eq(s(birdID)).or(i("father").eq(s(birdID)))
                    .or(i("nestMother").eq(s(birdID))))))
            .unionAll()
            .select(path(meta(""), "id")).from(bucket).where(i("docType").eq(s("FeedOut")).and(
                anyIn("tb", i("targetBirdList")).satisfies(path("tb", i("birdID")).eq(s(birdID)))))
            .unionAll()
            // checkmateDataList.birdId is correct and differs to birdID used elsewhere
            .select(path(meta(""), "id")).from(bucket).where(i("docType").eq(s("Record")).and(
                anyIn("cd", path(i("checkmate"), i("checkmateDataList")))
                    .satisfies(path("cd", i("birdId")).eq(s(birdID)))))
            .unionAll()
            .select(path(meta(""), "id")).from(bucket).where(i("docType").eq(s("SnarkActivity"))
                .and(anyIn("sr", i("snarkRecordList"))
                    .satisfies(path("sr", i("birdID")).eq(s(birdID)))))
        ).as("u"));

    N1qlQuery query = N1qlQuery.simple(statement);

    return template.queryN1QL(query).allRows().get(0).value().getBoolean("hasReferences");
  }

  public Expression like(String key, List<String> options) {
    Expression e = lower(x(key)).like(lower(s(options.get(0) + wildcard)));

    for (int i = 1; i < options.size(); i++) {
      e = e.or(lower(x(key)).like(lower(s(options.get(i) + wildcard))));
    }

    return par(e);
  }

  @Override
  public PagedResponse<BirdSearchDTO> findSearchDTOByCriteria(BirdCriteria criteria) {
    // even though we are forced to query using a string
    // we can still specify the where clause using an expression as the to string returns the sql
    // only exception is age class which must be added to the sql string directly
    Expression e = x("b.docType").eq(s("Bird"));
    e = e.and(queryUtils.notLikeSyncId("b"));

//        if(criteria.getBirdName() != null) {
//            e = e.and(par(
//                    lower(x("b.birdName")).like(lower(s(criteria.getBirdName() + wildcard)))
//                            .or(lower(x("b.houseID")).like(lower(s(criteria.getBirdName() + wildcard))))
//                            .or(lower(x("b.gan")).like(lower(s(criteria.getBirdName() + wildcard))))
//                            .or(lower(x("b.eggName")).like(lower(s(criteria.getBirdName() + wildcard))))));
//        }
    if (criteria.getBirdNames() != null && criteria.getBirdNames().size() > 0) {
      e = e.and(par(
          like("b.birdName", criteria.getBirdNames())
              .or(like("b.houseID", criteria.getBirdNames()))
              .or(like("b.gan", criteria.getBirdNames()))
              .or(like("b.eggName", criteria.getBirdNames()))
      ));
    }
    // using b = true works around a limitation in couchbase
    // if we just used false we get error: 'No index available for join term ls'
    Expression aliveOrDead = x("b = true");
    if (Boolean.TRUE.equals(criteria.getShowAlive())) {
      aliveOrDead = aliveOrDead.or(x("b.alive"));
    }
    if (Boolean.TRUE.equals(criteria.getShowDead())) {
      aliveOrDead = aliveOrDead.or(x("b.alive").isMissing().or(x("b.alive").not()));
    }
    e = e.and(par(aliveOrDead));
    if (criteria.getSex() != null) {
      e = e.and(lower(x("b.sex")).like(lower(s(criteria.getSex() + wildcard))));
    }
    if (criteria.getCurrentIsland() != null) {
      e = e.and(lower(x("b.currentIsland")).like(lower(s(criteria.getCurrentIsland() + wildcard))));
    }
    if (criteria.getTransmitterGroup() != null) {
      e = e.and(
          lower(x("b.transmitterGroup")).like(lower(s(criteria.getTransmitterGroup() + wildcard))));
    }
    if (criteria.getCurrentLocationID() != null) {
      e = e.and(x("b.currentLocationID").eq(s(criteria.getCurrentLocationID())));
    }

    String bucketName = template.getCouchbaseBucket().name();

    String sql = "select " +
        "{\"birdID\": meta(b).id, " +
        "b.birdName, " +
        "b.houseID, " +
        "b.currentIsland, " +
        "\"currentLocation\": l.locationName, " +
        "b.sex, " +
        "\"ageClass\": mostRecentStage[1], " +
        "b.age, " +
        "b.alive, " +
        "b.transmitterGroup} as searchDTO, \n" +
        "b.dateLaid, \n" +
        "b.dateHatched, \n" +
        "b.dateFirstFound, \n" +
        "b.estAgeWhen1stFound, \n" +
        "b.viable, \n" +
        "b.demise \n" +
        "from `" + bucketName + "` as b \n" +
        "LEFT JOIN `" + bucketName + "` AS ls ON KEY ls.birdID FOR b \n" +
        "LEFT JOIN `" + bucketName + "` AS l ON KEYS b.currentLocationID \n" +
        "WHERE " + e + "\n" +
        "and ls.docType = 'LifeStage' \n" +
        "group by b, l \n" +
        "letting mostRecentStage = max([ls.modifiedTime, ls.ageClass]) \n" +
        (criteria.getAgeClass() != null ? "HAVING lower(mostRecentStage[1]) like lower(\""
            + criteria.getAgeClass() + "%\")\n " : "") +
        (Optional.ofNullable(criteria.getExcludeEgg()).orElse(false)
            ? "HAVING LOWER(mostRecentStage[1]) != LOWER(\"Egg\")\n" : "") +
        "ORDER BY b.birdName \n" +
        "LIMIT " + criteria.getPageSize() + " \n" +
        "OFFSET " + criteria.getOffset() + " \n";

    N1qlQuery query = N1qlQuery.simple(sql);

    List<BirdSearchPreDTO> preList = template.findByN1QLProjection(query, BirdSearchPreDTO.class);

    List<BirdSearchDTO> list = new ArrayList<>();
    for (BirdSearchPreDTO preDTO : preList) {
      preDTO.populateSearchDTOAgeInDays();
      list.add(preDTO.getSearchDTO());
    }

    Integer totalCount = 0;

    if (!list.isEmpty()) {
      String countSql = "select count(results) as count from (select meta(b).id " +
          "from `" + bucketName + "` as b \n" +
          "LEFT JOIN `" + bucketName + "` AS ls ON KEY ls.birdID FOR b \n" +
          "WHERE " + e + "\n" +
          "and ls.docType = 'LifeStage' \n" +
          "group by b \n" +
          "letting mostRecentStage = max([ls.modifiedTime, ls.ageClass]) \n" +
          (criteria.getAgeClass() != null ? "HAVING lower(mostRecentStage[1]) like lower(\""
              + criteria.getAgeClass() + "%\")\n " : "") +
          (Optional.ofNullable(criteria.getExcludeEgg()).orElse(false)
              ? "HAVING LOWER(mostRecentStage[1]) != LOWER(\"Egg\")\n" : "") +
          ") results";

      N1qlQuery countQuery = N1qlQuery.simple(countSql);

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
  public List<BirdSummaryDTO> findBirdSummaries(BirdSummaryCriteria criteria) {

    Expression bucket = i(template.getCouchbaseBucket().name());
    Expression metaId = path(meta(bucket), "id");
    Expression e = x("docType").eq(s("Bird"));
    e = e.and(queryUtils.notLikeSyncId());

    if (criteria != null) {
      if (criteria.getBirdIDList() != null && criteria.getBirdIDList().size() > 0) {
        e = e.and(like("meta().id", criteria.getBirdIDList()));
      }
    }

    Statement statement = select(
        metaId.as("id"),
        i("birdName"),
        caseSearch().when(i("alive")).then(s("ALIVE")).elseReturn(s("DEAD")).as("state")
    )
        .from(bucket)
        .where(e)
        .orderBy(Sort.asc(i("state")), Sort.asc(i("birdName")));

    N1qlQuery query = N1qlQuery
        .simple(statement, N1qlParams.build().consistency(ScanConsistency.REQUEST_PLUS));
    List<BirdSummaryDTO> summaryList = template.findByN1QLProjection(query, BirdSummaryDTO.class);

    return summaryList;
  }

  @Override
  public List<String> findBirdNames() {

    Expression e = x("docType").eq(s("Bird"));
    e = e.and(queryUtils.notLikeSyncId());

    Statement statement = selectDistinct(i("birdName"))
        .from(sub(select(i("birdName").as("birdName"))
            .from(i(template.getCouchbaseBucket().name()))
            .where(e)
            .union(select(i("houseID").as("birdName"))
                .from(i(template.getCouchbaseBucket().name()))
                .where(i("docType").eq(s("Bird"))))
            .union(select(i("gan").as("birdName"))
                .from(i(template.getCouchbaseBucket().name()))
                .where(i("docType").eq(s("Bird"))))
            .union(select(i("eggName").as("birdName"))
                .from(i(template.getCouchbaseBucket().name()))
                .where(i("docType").eq(s("Bird")))))).as("b")
        .orderBy(Sort.asc("b.birdName"));

    N1qlQuery query = N1qlQuery
        .simple(statement, N1qlParams.build().consistency(ScanConsistency.REQUEST_PLUS));

    List<String> list = new ArrayList<>();
    template.queryN1QL(query).forEach(n1qlQueryRow -> {
      list.add(n1qlQueryRow.value().getString("birdName"));
    });

    return list;
  }

  @Override
  public List<BirdLocationDTO> findBirdsAliveAtDate(BirdLocationCriteria criteria) {
    List<String> latestRecords = findLatestRecordIDForBirds(criteria.getQueryDate());
    String idList = "";
    boolean first = true;
    for (String id : latestRecords) {
      if (!first) {
        idList += ", \n";
      } else {
        first = false;
      }
      idList += "\"" + id + "\"";
    }

    String queryDateStr = criteria.getQueryDate().toInstant().atZone(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_INSTANT);
    String islandQueryStr = criteria.getIsland() != null ? criteria.getIsland() + "%" : "%";

    String sql = "Select {b.birdName, b.sex, x.birdID, x.island, x.dateTime} as dto, \n" +
        "b.alive, \n" +
        "b.dateFirstFound, \n" +
        "b.dateHatched, \n" +
        "b.dateLaid, \n" +
        "b.demise, \n" +
        "b.discoveryDate, \n" +
        "b.estAgeWhen1stFound, \n" +
        "b.viable \n" +
        "From (\n" +
        "SELECT  r.birdID,  r.dateTime, r.island\n" +
        "FROM `kakapo-bird` AS r use keys [ \n" +
        idList + "\n" +
        "]) as x join `kakapo-bird` as b on keys x.birdID\n" +
        "where\n" +
        "    (b.alive = true or b.demise is not missing)\n" +
        "and (b.dateLaid is missing or b.dateLaid < '{queryDateTime}')\n" +
        "and (b.demise is missing or b.demise > '{queryDateTime}')\n" +
        "and (x.island like '{islandQueryStr}')\n" +
        "Order by b.birdName";

    sql = sql.replace("{queryDateTime}", queryDateStr);
    sql = sql.replace("{islandQueryStr}", islandQueryStr);

    N1qlQuery query = N1qlQuery.simple(sql);
    List<BirdLocationPreDTO> preList = template
        .findByN1QLProjection(query, BirdLocationPreDTO.class);
    List<BirdLocationDTO> list = new ArrayList<>();
    for (BirdLocationPreDTO preDto : preList) {
      BirdLocationDTO dto = preDto.getDto();
      dto.setAgeClass(preDto.calcAgeClass(criteria.getQueryDate()));
      list.add(dto);
    }

    return list;
  }

  @Override
  public List<BirdLocationDTO> findBirdsAliveBetweenDates(BirdLocationCriteria criteria) {
    // Used by NoraNet Undetected Kakapo list
    List<String> latestRecords = findLatestRecordIDForBirds(criteria.getQueryToDate());
    StringBuilder idList = new StringBuilder();
    boolean first = true;
    for (String id : latestRecords) {
      if (!first) {
        idList.append(", \n");
      } else {
        first = false;
      }
      idList.append("\"").append(id).append("\"");
    }

    String queryFromDateStr = criteria.getQueryDate().toInstant().atZone(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_INSTANT);
    String queryToDateStr = criteria.getQueryToDate().toInstant().atZone(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_INSTANT);
    String islandQueryStr = criteria.getIsland() != null ? criteria.getIsland() + "%" : "%";

    // find birds deemed alive between the dates, excludes eggs
    String sql = "SELECT DISTINCT b.birdName, x.birdID, x.island\n" +
        "FROM (\n" +
        "SELECT  r.birdID, r.island\n" +
        "FROM `kakapo-bird` AS r USE KEYS [\n" + idList + "]\n" +
        "UNION\n" +
        "SELECT meta(bd).id AS birdID, bd.currentIsland AS island\n" +
        "FROM `kakapo-bird` bd\n" +
        "WHERE bd.docType = 'Bird'\n" +
        "AND (bd.alive = true OR bd.demise IS NOT MISSING)\n" +
        ") AS x\n" +
        "JOIN `kakapo-bird` AS b ON KEYS x.birdID\n" +
        "LEFT JOIN `kakapo-bird` AS ls ON KEY ls.birdID FOR b \n" +
        "WHERE\n" +
        "    (b.alive = true OR b.demise IS NOT MISSING)\n" +
        "AND (b.dateHatched IS MISSING OR b.dateHatched < '{queryToDateTime}')\n" +
        "AND (b.demise IS MISSING OR b.demise > '{queryFromDateTime}')\n" +
        "AND (x.island LIKE '{islandQueryStr}')\n" +
        "AND ls.docType = 'LifeStage'\n" +
        "GROUP BY b,x\n" +
        "LETTING mostRecentStage = max([ls.modifiedTime, ls.ageClass])\n" +
        "HAVING LOWER(mostRecentStage[1]) NOT LIKE LOWER('Egg%')";

    sql = sql.replace("{queryFromDateTime}", queryFromDateStr);
    sql = sql.replace("{queryToDateTime}", queryToDateStr);
    sql = sql.replace("{islandQueryStr}", islandQueryStr);

    N1qlQuery query = N1qlQuery.simple(sql);
    return template.findByN1QLProjection(query, BirdLocationDTO.class);
  }

  private List<String> findLatestRecordIDForBirds(Date asAt) {
    List<Map> birdIDDateTimePairs = findLatestBirdIDDateTimePair(asAt);

    String clause = "";
    boolean first = true;
    for (Map pair : birdIDDateTimePairs) {
      if (!first) {
        clause += "\nOR ";
      } else {
        first = false;
      }
      clause += "(birdID = \"" + pair.get("birdID") + "\" and dateTime = \"" + pair.get("dateTime")
          + "\") ";
    }

    String sql = "select max(meta().id) as id\n" +
        "from `kakapo-bird` USE INDEX (idx_record_datetime_birdID USING GSI)\n" +
        "where docType = 'Record'\n" +
        "and meta().id not like '_sync:rb:%'\n" +
        "and (" + "\n" +
        clause + "\n" +
        ")\n" +
        "group by birdID, dateTime";

    N1qlQuery query = N1qlQuery.simple(sql);

    List<String> list = new ArrayList<>();

    N1qlQueryResult result = template.queryN1QL(query);
    for (N1qlQueryRow row : result.allRows()) {
      list.add((String) row.value().get("id"));
    }

    return list;
  }

  private List<Map> findLatestBirdIDDateTimePair(Date asAt) {
    String queryDateStr = asAt.toInstant().atZone(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_INSTANT);

    String sql = "select birdID, max(dateTime) as dateTime \n" +
        "from `kakapo-bird` r \n" +
        "where r.docType = 'Record' \n" +
        "and meta().id not like '_sync:rb:%' \n" +
        "and dateTime <= $asAt \n" +
        "group by birdID\n" +
        "order by birdID";

    N1qlQuery query = N1qlQuery.parameterized(sql, JsonObject.create().put("asAt", queryDateStr));

    return template.findByN1QLProjection(query, Map.class);
  }

  public List<BirdLocationDTO> findBirdsAliveAtDateOld(BirdLocationCriteria criteria) {

    String queryDateStr = criteria.getQueryDate().toInstant().atZone(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_INSTANT);
    String islandQueryStr = criteria.getIsland() != null ? criteria.getIsland() + "%" : "%";

    String sql = "Select {b.birdName, b.sex, x.birdID, x.island, x.dateTime} as dto, \n" +
        "b.alive, \n" +
        "b.dateFirstFound, \n" +
        "b.dateHatched, \n" +
        "b.dateLaid, \n" +
        "b.demise, \n" +
        "b.discoveryDate, \n" +
        "b.estAgeWhen1stFound, \n" +
        "b.viable \n" +
        "From (\n" +
        "SELECT  r.birdID,  doc[0] AS dateTime, doc[1] AS island\n" +
        "FROM `kakapo-bird` AS r\n" +
        "WHERE \n" +
        "    r.docType = 'Record' \n" +
        "AND meta(r).id NOT LIKE '\\\\_sync%' \n" +
        "AND r.birdID IS NOT NULL\n" +
        "AND r.dateTime < '{queryDateTime}'\n" +
        "GROUP BY r.birdID\n" +
        "LETTING doc = MAX([r.dateTime, r.island])\n" +
        ") as x join `kakapo-bird` as b on keys x.birdID\n" +
        "where\n" +
        "    (b.alive = true or b.demise is not missing)\n" +
        "and (b.dateLaid is missing or b.dateLaid < '{queryDateTime}')\n" +
        "and (b.demise is missing or b.demise > '{queryDateTime}')\n" +
        "and (x.island like '{islandQueryStr}')\n" +
        "Order by b.birdName";

    sql = sql.replace("{queryDateTime}", queryDateStr);
    sql = sql.replace("{islandQueryStr}", islandQueryStr);

    N1qlQuery query = N1qlQuery.simple(sql);
    List<BirdLocationPreDTO> preList = template
        .findByN1QLProjection(query, BirdLocationPreDTO.class);
    List<BirdLocationDTO> list = new ArrayList<>();
    for (BirdLocationPreDTO preDto : preList) {
      BirdLocationDTO dto = preDto.getDto();
      dto.setAgeClass(preDto.calcAgeClass(criteria.getQueryDate()));
      list.add(dto);
    }
    return list;

  }

  public BirdEntity findBirdIDByTransmitter(String island, int channel, String sex) {

    Expression e = x("transmitter.docType").eq(s("Transmitter"));
    e = e.and(queryUtils.notLikeSyncId("transmitter"));

    Expression whereClause = e
        .and("transmitter.channel").eq(channel)
        .and(x("transmitter.status").in(JsonArray.from("Deployed new", "Deployed old")))
        .and(x("bird.currentIsland").eq(s(island)))
        .and(x("bird.alive"));

    if (sex != null) {
      whereClause = whereClause.and(x("bird.sex").eq(s(sex)));
    }

    Statement statement = select(
        x("record.birdID").as("id"))
        .from(i(template.getCouchbaseBucket().name())).as("transmitter")
        .join(i(template.getCouchbaseBucket().name())).as("record")
        .onKeys("transmitter.lastRecordId")
        .join(i(template.getCouchbaseBucket().name())).as("bird").onKeys("record.birdID")
        .where(whereClause);

    N1qlQuery query = N1qlQuery.simple(statement);
    List<BirdEntity> results = template.findByN1QLProjection(query, BirdEntity.class);
    if (results.size() == 1) {
      return results.get(0);
    } else {
      return null;
    }
  }

  @Override
  public List<BirdEntity> findBirdsByTransmitter(String island, int uhfId, String sex) {

    Expression e = x("transmitter.docType").eq(s("Transmitter"));
    e = e.and(queryUtils.notLikeSyncId("transmitter"));

    Expression whereClause = e
        .and("transmitter.uhfId").eq(uhfId)
        .and(x("transmitter.status").in(JsonArray.from("Deployed new", "Deployed old")))
        .and(x("bird.currentIsland").eq(s(island)))
        .and(x("bird.alive"));

    if (sex != null) {
      whereClause = whereClause.and(x("bird.sex").eq(s(sex)));
    }

    Statement statement = select(
        x("record.birdID").as("id"))
        .from(i(template.getCouchbaseBucket().name())).as("transmitter")
        .join(i(template.getCouchbaseBucket().name())).as("record")
        .onKeys("transmitter.lastRecordId")
        .join(i(template.getCouchbaseBucket().name())).as("bird").onKeys("record.birdID")
        .where(whereClause);

    N1qlQuery query = N1qlQuery.simple(statement);
    List<BirdEntity> results = template.findByN1QLProjection(query, BirdEntity.class);
    return results;
  }

  @Override
  public LifeStageEntity findLatestLifeStageByBirdID(String birdID) {
    Expression e = x("docType").eq(s("LifeStage"));
    e = e.and(queryUtils.notLikeSyncId());

    Statement statement = N1qlUtils
        .createSelectClauseForEntity(template.getCouchbaseBucket().name())
        .from(i(template.getCouchbaseBucket().name()))
        .where(e.and(i("birdID").eq(s(birdID)))
            .and(i("ageClass").isValued()))
        .orderBy(Sort.desc(i("modifiedTime")))
        .limit(1);

    //N1qlQuery query = N1qlQuery.simple(statement);
    N1qlQuery query = N1qlQuery
        .simple(statement, N1qlParams.build().consistency(ScanConsistency.REQUEST_PLUS));

    List<LifeStageEntity> list = template.findByN1QL(query, LifeStageEntity.class);

    if (list.isEmpty()) {
      return null;
    } else {
      return list.get(0);
    }
  }

  @Override
  public String getName(String birdID) {
    Expression e = x("docType").eq(s("Bird"));
    e = e.and(queryUtils.notLikeSyncId());

    Statement s = select(i("birdName"))
        .from(i(template.getCouchbaseBucket().name())).useKeys(s(birdID))
        .where(e);

    N1qlQuery query = N1qlQuery.simple(s);

    List<N1qlQueryRow> rows = template.queryN1QL(query).allRows();
    if (rows.isEmpty()) {
      return null;
    } else {
      return rows.get(0).value().getString("birdName");
    }
  }

  @Override
  public boolean existsByNameExcluding(String birdName, String excludingBirdID) {
    Expression e = x("docType").eq(s("Bird"));
    e = e.and(queryUtils.notLikeSyncId());
    e = e.and(lower(i("birdName")).eq(lower(s(birdName))));
    e = e.and(path(meta(""), i("id")).ne(s(excludingBirdID)));

    Statement s = select(path(meta(""), i("id")))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e);

    N1qlQuery query = N1qlQuery.simple(s);

    List<N1qlQueryRow> rows = template.queryN1QL(query).allRows();
    return !rows.isEmpty();
  }

  @Override
  public boolean existsByHouseIDExcluding(String houseID, String excludingBirdID) {
    Expression e = x("docType").eq(s("Bird"));
    e = e.and(queryUtils.notLikeSyncId());
    e = e.and(lower(i("houseID")).eq(lower(s(houseID))));
    e = e.and(path(meta(""), i("id")).ne(s(excludingBirdID)));

    Statement s = select(path(meta(""), i("id")))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e);

    N1qlQuery query = N1qlQuery.simple(s);

    List<N1qlQueryRow> rows = template.queryN1QL(query).allRows();
    return !rows.isEmpty();
  }

}

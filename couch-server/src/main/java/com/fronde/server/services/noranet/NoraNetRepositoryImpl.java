package com.fronde.server.services.noranet;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.Expression;
import com.couchbase.client.java.query.dsl.Sort;
import com.fronde.server.domain.NoraNetDetectionEntity;
import com.fronde.server.domain.NoraNetEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.bird.BirdLocationCriteria;
import com.fronde.server.services.bird.BirdLocationDTO;
import com.fronde.server.services.bird.BirdRepository;
import com.fronde.server.services.noranet.NoraNetSearchDTO.Bird;
import com.fronde.server.services.record.BirdTransmitterHistoryDTO;
import com.fronde.server.services.record.RecordRepository;
import com.fronde.server.utils.QueryUtils;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.Select.selectDistinct;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.par;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.functions.Collections.anyWithin;
import static com.couchbase.client.java.query.dsl.functions.TypeFunctions.toNumber;
import static com.couchbase.client.java.query.dsl.functions.Collections.anyIn;
import static com.couchbase.client.java.query.dsl.functions.StringFunctions.lower;

/**
 * @version 1.0
 * @date 14/07/2021
 */

@Component
public class NoraNetRepositoryImpl implements NoraNetRepositoryCustom {

  protected final CouchbaseTemplate template;

  protected final QueryUtils queryUtils;

  protected final BirdRepository birdRepository;

  protected final RecordRepository recordRepository;

  public NoraNetRepositoryImpl(CouchbaseTemplate template, QueryUtils queryUtils,
      BirdRepository birdRepository,
      RecordRepository recordRepository) {
    this.template = template;
    this.queryUtils = queryUtils;
    this.birdRepository = birdRepository;
    this.recordRepository = recordRepository;
  }

  public PagedResponse<NoraNetEntity> findByCriteria(NoraNetCriteria criteria) {
    Expression e = i("docType").eq(s("NoraNet"));
    e = e.and(queryUtils.notLikeSyncId());

    if (criteria.getIsland() != null) {
      e = e.and(lower(i("island")).like(lower(s(criteria.getIsland()))));
    }
    if (criteria.getStationId() != null) {
      e = e.and(lower(i("stationId")).like(lower(s(criteria.getStationId()))));
    }
    if (criteria.getFileDate() != null) {
      String fileDate = criteria.getFileDate().toInstant().atZone(ZoneOffset.UTC)
          .format(DateTimeFormatter.ISO_INSTANT);
      e = e.and(x("fileDate").eq(s(fileDate)));
    }
    if (criteria.getActivityDate() != null) {
      String activityDate = criteria.getActivityDate().toInstant().atZone(ZoneOffset.UTC)
          .format(DateTimeFormatter.ISO_INSTANT);
      e = e.and(x("activityDate").eq(s(activityDate)));
    }

    return queryUtils
        .query(criteria, e, NoraNetEntity.class, Sort.asc("dataType"));
  }

  @Override
  public PagedResponse<NoraNetSearchDTO> findSearchDTOByCriteria(NoraNetCriteria criteria) {

    Expression bucket = i(template.getCouchbaseBucket().name());
    Expression e = i("docType").eq(s("NoraNet"));
    e = e.and(queryUtils.notLikeSyncId());

    if (criteria.getIsland() == null || criteria.getIsland().equals("")) {
      // must provide an island
      throw new RuntimeException("No Island provided in the request");
    }
    e = e.and(lower(i("island")).like(lower(s(criteria.getIsland()))));

    if (criteria.getFromActivityDate() == null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());
      cal.add(Calendar.DATE, -7);
      criteria.setFromActivityDate(cal.getTime());
    } else {
      Calendar cal = Calendar.getInstance();
      cal.setTime(criteria.getFromActivityDate());
      cal.add(Calendar.HOUR, -12);
      criteria.setFromActivityDate(cal.getTime());
    }

    e = e.and(
        i("activityDate").gte(s(criteria.getFromActivityDate().toInstant().atZone(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT))));

    if (criteria.getToActivityDate() == null) {
      criteria.setToActivityDate(new Date());
    }
    e = e.and(
        i("activityDate").lte(s(criteria.getToActivityDate().toInstant().atZone(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT))));

    if (criteria.getDataType() == null) {
      criteria.setDataType("ALL");
    }

    JsonArray birdArray = JsonArray.empty();
    if (criteria.getBirdIDs() != null && criteria.getBirdIDs().size() > 0) {
      birdArray = JsonArray.from(criteria.getBirdIDs());
      // retrieve any records in detectionList, standardList, eggTimerList, cmShortList, cmLongList
      if (criteria.getDataType().equalsIgnoreCase("ALL")) {
        e = e.and(par(
            anyWithin("dt", i("detectionList")).satisfies(x("dt").in(birdArray))
                .or(anyWithin("st", i("standardList")).satisfies(x("st").in(birdArray)))
                .or(anyWithin("et", i("eggTimerList")).satisfies(x("et").in(birdArray)))
                .or(anyWithin("cms", i("cmShortList")).satisfies(x("cms").in(birdArray)))
                .or(anyWithin("cml", i("cmLongList")).satisfies(x("cml").in(birdArray)))));
      } else if (criteria.getDataType().equalsIgnoreCase("DETECT")) {
        e = e.and(par(anyWithin("dt", i("detectionList")).satisfies(x("dt").in(birdArray))));
      } else if (criteria.getDataType().equalsIgnoreCase("STANDARD")) {
        e = e.and(par(anyWithin("st", i("standardList")).satisfies(x("st").in(birdArray))));
      } else if (criteria.getDataType().equalsIgnoreCase("Egg timer")) {
        e = e.and(par(anyWithin("et", i("eggTimerList")).satisfies(x("et").in(birdArray))));
      } else if (criteria.getDataType().equalsIgnoreCase("CMSHORT")) {
        e = e.and(par(anyWithin("cms", i("cmShortList")).satisfies(x("cms").in(birdArray))));
      } else if (criteria.getDataType().equalsIgnoreCase("CMLONG")) {
        e = e.and(par(anyWithin("cml", i("cmLongList")).satisfies(x("cml").in(birdArray))));
      } else if (criteria.getDataType().equalsIgnoreCase("Checkmate")) {
        e = e.and(par(anyWithin("cms", i("cmShortList")).satisfies(x("cms").in(birdArray))
            .or(anyWithin("cml", i("cmLongList")).satisfies(x("cml").in(birdArray)))));
      }
    }

    if (criteria.getUhfId() != null) {
      if (criteria.getDataType().equalsIgnoreCase("ALL")) {
        // retrieve any records in detectionList, standardList, eggTimerList, cmShortList, cmLongList
        e = e.and(par(
            anyIn("dt", i("detectionList")).satisfies(
                    x("dt.uhfId").eq(toNumber(x(criteria.getUhfId()))))
                .or(anyIn("st", i("standardList")).satisfies(
                    x("st.uhfId").eq(toNumber(x(criteria.getUhfId())))))
                .or(anyIn("et", i("eggTimerList")).satisfies(
                    x("et.uhfId").eq(toNumber(x(criteria.getUhfId())))))
                .or(anyIn("cms", i("cmShortList")).satisfies(
                    x("cms.uhfId").eq(toNumber(x(criteria.getUhfId())))))
                .or(anyIn("cml", i("cmLongList")).satisfies(
                    x("cml.uhfId").eq(toNumber(x(criteria.getUhfId())))))));
      } else if (criteria.getDataType().equalsIgnoreCase("DETECT")) {
        e = e.and(par(anyIn("dt", i("detectionList")).satisfies(
            x("dt.uhfId").eq(toNumber(x(criteria.getUhfId()))))));
      } else if (criteria.getDataType().equalsIgnoreCase("STANDARD")) {
        e = e.and(par(anyIn("st", i("standardList")).satisfies(
            x("st.uhfId").eq(toNumber(x(criteria.getUhfId()))))));
      } else if (criteria.getDataType().equalsIgnoreCase("Egg timer")) {
        e = e.and(par(anyIn("et", i("eggTimerList")).satisfies(
            x("et.uhfId").eq(toNumber(x(criteria.getUhfId()))))));
      } else if (criteria.getDataType().equalsIgnoreCase("CMSHORT")) {
        e = e.and(par(anyIn("cms", i("cmShortList")).satisfies(
            x("cms.uhfId").eq(toNumber(x(criteria.getUhfId()))))));
      } else if (criteria.getDataType().equalsIgnoreCase("CMLONG")) {
        e = e.and(par(anyIn("cml", i("cmLongList")).satisfies(
            x("cml.uhfId").eq(toNumber(x(criteria.getUhfId()))))));
      } else if (criteria.getDataType().equalsIgnoreCase("Checkmate")) {
        e = e.and(par(anyIn("cms", i("cmShortList")).satisfies(
                x("cms.uhfId").eq(toNumber(x(criteria.getUhfId()))))
            .or(anyIn("cml", i("cmLongList")).satisfies(
                x("cml.uhfId").eq(toNumber(x(criteria.getUhfId())))))));
      }
    }

    // get the ids of the matching documents first as it seems to be faster to break it up in this way
    Statement idsStatement;
    // need to select all records that match, order not important
    idsStatement = select(x("meta().id").as(i("id")))
        .from(bucket)
        .where(e);

    N1qlQuery idsQuery = N1qlQuery.simple(idsStatement);

    List<String> selectedIDs = new ArrayList<>();

    template.queryN1QL(idsQuery).forEach(result -> selectedIDs.add(result.value().getString("id")));

    List<NoraNetSearchDTO> results;
    List<NoraNetSearchDTO> resultsCount;

    if (selectedIDs.isEmpty()) {
      results = Collections.emptyList();
      resultsCount = Collections.emptyList();
    } else {
      String bucketName = "`" + template.getCouchbaseBucket().name() + "`";
      // Use the ids to get the results to populate the search dto
      // Exempt from sync filter because where clause only picking from list of IDs above.
      String queryString = "";
      String queryStringDetect = "SELECT " +
          "meta(nn).id AS noraNetId, " +
          "nn.island AS island, " +
          "nn.activityDate AS activityDate, " +
          "nn.stationId AS stationId, " +
          "nn.batteryVolts AS batteryVolts, " +
          "'Detected' AS dataType, " +
          "dt.uhfId AS uhfId, " +
          "(ARRAY_REVERSE(ARRAY_AGG(" +
          "OBJECT_CONCAT({\"birdName\":b.birdName},{\"birdID\":birds.birdID})))) AS birdList, " +
          "dt.category AS category, " +
          "dt.pulseCount AS pulseCount, " +
          "dt.peakTwitch AS peakTwitch, " +
          "dt.activity AS activity " +
          "FROM `kakapo-bird` nn " +
          "UNNEST detectionList AS dt " +
          "LEFT UNNEST dt.birdList as birds " +
          "LEFT JOIN `kakapo-bird` b ON KEYS birds.birdID " +
          "WHERE meta(nn).id IN $selectedIDs " +
          (criteria.getUhfId() != null ? "AND dt.uhfId = $uhfId " : "") +
          (!birdArray.isEmpty() ? "AND (ANY x WITHIN dt.birdList SATISFIES x IN $birdArray END) " : "") +
          "GROUP BY nn, dt ";

      String queryStringStandard = "SELECT " +
          "meta(nn).id AS noraNetId, " +
          "nn.island AS island, " +
          "nn.activityDate AS activityDate, " +
          "nn.stationId AS stationId, " +
          "'Standard' AS dataType, " +
          "st.uhfId AS uhfId, " +
          "(ARRAY_REVERSE(ARRAY_AGG(" +
          "OBJECT_CONCAT({\"birdName\":b.birdName},{\"birdID\":birds.birdID})))) AS birdList, " +
          "st.activity AS activity, " +
          "st.batteryLife AS batteryLife " +
          "FROM `kakapo-bird` nn " +
          "UNNEST standardList AS st " +
          "LEFT UNNEST st.birdList as birds " +
          "LEFT JOIN `kakapo-bird` b ON KEYS birds.birdID " +
          "WHERE meta(nn).id IN $selectedIDs " +
          (criteria.getUhfId() != null ? "AND st.uhfId = $uhfId " : "") +
          (!birdArray.isEmpty() ? "AND (ANY x WITHIN st.birdList SATISFIES x IN $birdArray END) " : "") +
          "GROUP BY nn, st ";

      String queryStringEgg = "SELECT " +
          "meta(nn).id AS noraNetId, " +
          "nn.island AS island, " +
          "nn.activityDate AS activityDate, " +
          "nn.stationId AS stationId, " +
          "'Egg timer' AS dataType, " +
          "et.uhfId AS uhfId, " +
          "(ARRAY_REVERSE(ARRAY_AGG(" +
          "OBJECT_CONCAT({\"birdName\":b.birdName},{\"birdID\":birds.birdID})))) AS birdList, " +
          "et.activity AS activity, " +
          "et.batteryLife AS batteryLife, " +
          "et.incubating AS incubating, " +
          "et.daysSinceChange AS daysSinceChange " +
          "FROM `kakapo-bird` nn " +
          "UNNEST eggTimerList AS et " +
          "LEFT UNNEST et.birdList as birds " +
          "LEFT JOIN `kakapo-bird` b ON KEYS birds.birdID " +
          "WHERE meta(nn).id IN $selectedIDs " +
          (criteria.getUhfId() != null ? "AND et.uhfId = $uhfId " : "") +
          (!birdArray.isEmpty() ? "AND (ANY x WITHIN et.birdList SATISFIES x IN $birdArray END) " : "") +
          "GROUP BY nn, et ";

      String queryStringCmShort = "SELECT " +
          "meta(nn).id AS noraNetId, " +
          "nn.island AS island, " +
          "nn.activityDate AS activityDate, " +
          "nn.stationId AS stationId, " +
          "'Non-Mating Checkmate' AS dataType, " +
          "cms.uhfId AS uhfId, " +
          "(ARRAY_REVERSE(ARRAY_AGG(" +
          "OBJECT_CONCAT({\"birdName\":b.birdName},{\"birdID\":birds.birdID})))) AS birdList, " +
          "cms.activity AS activity, " +
          "cms.batteryLife AS batteryLife, " +
          "cms.matingAge AS matingAge, " +
          "cms.cmHour AS cmHour, " +
          "cms.cmMinute AS cmMinute " +
          "FROM `kakapo-bird` nn " +
          "UNNEST cmShortList AS cms " +
          "LEFT UNNEST cms.birdList as birds " +
          "LEFT JOIN `kakapo-bird` b ON KEYS birds.birdID " +
          "WHERE meta(nn).id IN $selectedIDs " +
          (criteria.getUhfId() != null ? "AND cms.uhfId = $uhfId " : "") +
          (!birdArray.isEmpty() ? "AND (ANY x WITHIN cms.birdList SATISFIES x IN $birdArray END) " : "") +
          "GROUP BY nn, cms ";

      String queryStringCmLong = "SELECT " +
          "meta(nn).id AS noraNetId, " +
          "nn.island AS island, " +
          "nn.activityDate AS activityDate, " +
          "nn.stationId AS stationId, " +
          "'Mating Checkmate' AS dataType, " +
          "cml.uhfId AS uhfId, " +
          "(ARRAY_REVERSE(ARRAY_AGG(" +
          "OBJECT_CONCAT({\"birdName\":b.birdName},{\"birdID\":birds.birdID})))) AS birdList, " +
          "cml.matingAge AS matingAge, " +
          "cml.cmHour AS cmHour, " +
          "cml.cmMinute AS cmMinute, " +
          "cml.lastCmHour AS lastCmHour, " +
          "cml.lastCmMinute AS lastCmMinute, " +
          "cml.cmFemaleList AS cmFemaleList " +
          "FROM `kakapo-bird` nn " +
          "UNNEST cmLongList AS cml " +
          "LEFT UNNEST cml.birdList as birds " +
          "LEFT JOIN `kakapo-bird` b ON KEYS birds.birdID " +
          "WHERE meta(nn).id IN $selectedIDs " +
          (criteria.getUhfId() != null ? "AND cml.uhfId = $uhfId " : "") +
          (!birdArray.isEmpty() ? "AND (ANY x WITHIN cml.birdList SATISFIES x IN $birdArray END) " : "") +
          "GROUP BY nn, cml ";

      // now add the select statements that need to be included
      if (criteria.getDataType().equalsIgnoreCase("ALL") ||
          criteria.getDataType().equalsIgnoreCase("Detected")) {
        queryString = queryStringDetect;
      }
      if (criteria.getDataType().equalsIgnoreCase("ALL") ||
          criteria.getDataType().equalsIgnoreCase("Standard")) {
        if (!queryString.equals("")) {queryString = queryString + "\nUNION ALL ";}
        queryString = queryString + queryStringStandard;
      }
      if (criteria.getDataType().equalsIgnoreCase("ALL") ||
          criteria.getDataType().equalsIgnoreCase("Egg timer")) {
        if (!queryString.equals("")) {queryString = queryString + "\nUNION ALL ";}
        queryString = queryString + queryStringEgg;
      }
      if (criteria.getDataType().equalsIgnoreCase("ALL") ||
          criteria.getDataType().equalsIgnoreCase("Checkmate")) {
        if (!queryString.equals("")) {queryString = queryString + "\nUNION ALL ";}
        queryString = queryString + queryStringCmShort;
        queryString = queryString + "\nUNION ALL ";
        queryString = queryString + queryStringCmLong;
      }

      //build and execute query for total count purposes, no ordering, limit or offset
      N1qlQuery queryCount = N1qlQuery.parameterized(queryString, JsonObject.create()
          .put("selectedIDs", selectedIDs)
          .put("uhfId", criteria.getUhfId())
          .put("birdArray", birdArray));
      resultsCount = template.findByN1QLProjection(queryCount, NoraNetSearchDTO.class);

      // add order by, limit and offset for data to return
      queryString = queryString +
          "\nORDER BY activityDate DESC, birdList[0].birdName, uhfId, stationId, dataType " +
          "LIMIT " + criteria.getPageSize() + " " +
          "OFFSET " + criteria.getOffset();

      N1qlQuery query = N1qlQuery.parameterized(queryString, JsonObject.create()
          .put("selectedIDs", selectedIDs)
          .put("uhfId", criteria.getUhfId())
          .put("birdArray", birdArray));

      results = template.findByN1QLProjection(query, NoraNetSearchDTO.class);
    }

    PagedResponse<NoraNetSearchDTO> pagedResponse = new PagedResponse<>();

    Integer totalCount = resultsCount.size();
    pagedResponse.setPage(criteria.getPageNumber());
    pagedResponse.setPageSize(criteria.getPageSize());
    pagedResponse.setTotal(totalCount);
    pagedResponse.setResults(results);

    return pagedResponse;
  }

  @Override
  public List<NoraNetSearchUndetectedDTO> findUndetectedDTOByCriteria(NoraNetCriteria criteria) {

    // set criteria as relevant for undetected list
    criteria.setUndetectedBirds(true);
    criteria.setBirdIDs(null);
    criteria.setUhfId(null);
    criteria.setDataType("ALL");
    criteria.setPageSize(10000);
    criteria.setPageNumber(1);
    // call the existing search to establish the detected birds
    PagedResponse<NoraNetSearchDTO> detectedResult = findSearchDTOByCriteria(criteria);

    List<NoraNetSearchDTO> birdsDetected = detectedResult.getResults();
    // loop through the results and make a list of all the birdIDs
    List<String> detectedBirdIDs = new ArrayList<>();
    for (NoraNetSearchDTO searchResult : birdsDetected) {
      if (searchResult.getBirdList() != null && searchResult.getBirdList().size() > 0) {
        for (Bird bird : searchResult.getBirdList()) {
          // add unique ids only and skip nulls
          if (bird != null && bird.getBirdID() != null) {
            if (!detectedBirdIDs.contains(bird.getBirdID())) {
              detectedBirdIDs.add(bird.getBirdID());
            }
          }
        }
      }
    }

    BirdLocationCriteria birdLocationCriteria = new BirdLocationCriteria();
    birdLocationCriteria.setIsland(criteria.getIsland());
    birdLocationCriteria.setQueryDate(criteria.getFromActivityDate());
    birdLocationCriteria.setQueryToDate(criteria.getToActivityDate());
    List<BirdLocationDTO> birdResults = birdRepository.findBirdsAliveBetweenDates(birdLocationCriteria);
    List<String> aliveBirdList = new ArrayList<>();
    for (BirdLocationDTO bird : birdResults) {
      if (bird != null && bird.getBirdID() != null) {
        if (!aliveBirdList.contains(bird.getBirdID())) {
          aliveBirdList.add(bird.getBirdID());
        }
      }
    }

    List<NoraNetSearchUndetectedDTO> results;
    if (aliveBirdList.isEmpty()) {
      results = Collections.emptyList();
    } else {
      Expression bucket = i(template.getCouchbaseBucket().name());
      Statement birdsStatement = selectDistinct(
          x("meta(b).id").as("birdID"),
          x("b.birdName").as("birdName")
      ).from(bucket).as("b")
          .where(x("meta(b).id").notIn(JsonArray.from(detectedBirdIDs))
              .and(x("meta(b).id").in(JsonArray.from(aliveBirdList))))
          .orderBy(Sort.asc("birdName"))
          .limit(criteria.getPageSize())
          .offset(criteria.getOffset());

      N1qlQuery query = N1qlQuery.simple(birdsStatement);

      results = template.findByN1QLProjection(query, NoraNetSearchUndetectedDTO.class);
      for (NoraNetSearchUndetectedDTO bird : results) {
        List<Integer> uhfIds = new ArrayList<>();
        if (bird.getBirdID() != null) {
          List<BirdTransmitterHistoryDTO> transmitterList =
              recordRepository.findTransmitterHistoryDTOByBirdID(bird.getBirdID());
          // reverse order of items so dates are in ascending order
          Collections.reverse(transmitterList);
          Integer startUhfId = null;
          for (BirdTransmitterHistoryDTO transmitter : transmitterList) {
            if (transmitter.getUhfId()!=null
                && transmitter.getRecordDateTime().before(criteria.getToActivityDate())) {
              if (transmitter.getRecordDateTime().before(criteria.getFromActivityDate())) {
                // we need to know the UHF ID that was on bird at time of our start date
                // because traversing list in date ascending order we can hold onto the prior uhfId
                if (transmitter.getUhfId()!=null) {
                  startUhfId = transmitter.getUhfId();
                }
              } else {
                if (!uhfIds.contains(transmitter.getUhfId())) {
                  uhfIds.add(transmitter.getUhfId());
                }
              }
            }
          }
          if (startUhfId!= null) {
            // insert the starting uhfId at beginning of list if not already in the list
            if (uhfIds.isEmpty() || !uhfIds.contains(startUhfId)) {
              uhfIds.add(0, startUhfId);
            }
          }
        }
        if (!uhfIds.isEmpty()) {
          bird.setUhfIdList(uhfIds);
        }
      }
    }
    return results;
  }

  public NoraNetDetectionEntity findDetectedById(String docId, Integer uhfId) {
    Expression bucket = i(template.getCouchbaseBucket().name());
    Statement statement = select(
        x("dt.uhfId").as("uhfId"),
        x("dt.birdList").as("birdList"),
        x("dt.category").as("category"),
        x("dt.pulseCount").as("pulseCount"),
        x("dt.peakTwitch").as("peakTwitch"),
        x("dt.activity").as("activity")
    ).from(bucket).as("nn")
        .unnest(x("detectionList").as("dt"))
        .where(x("meta(nn).id").eq(s(docId))
            .and(queryUtils.notLikeSyncId("nn"))
            .and(x("dt.uhfId").eq(toNumber(x(uhfId)))));

    N1qlQuery query = N1qlQuery.simple(statement);

    List<NoraNetDetectionEntity> results = template.findByN1QLProjection(query, NoraNetDetectionEntity.class);

    if (results.isEmpty()) {
      return null;
    } else {
      return results.get(0);
    }

  }

  @Override
  public List<String> findStations() {
    String queryString = "SELECT DISTINCT nn.stationId AS station " +
        "FROM `kakapo-bird` nn " +
        "WHERE nn.docType = 'NoraNet' " +
        "ORDER BY station";
    N1qlQuery query = N1qlQuery.simple(queryString);
    List<String> list = new ArrayList<>();
    template.queryN1QL(query).forEach(n1qlQueryRow -> {
      list.add(n1qlQueryRow.value().getString("station"));
    });
    return list;
  }

  @Override
  public PagedResponse<NoraNetSearchStationDTO> findStationDTOByCriteria(NoraNetCriteria criteria) {
    Expression bucket = i(template.getCouchbaseBucket().name());
    Expression e = i("docType").eq(s("NoraNet"));
    e = e.and(queryUtils.notLikeSyncId());

    if (criteria.getIsland() != null && !criteria.getIsland().equals("")) {
      e = e.and(lower(i("island")).eq(lower(s(criteria.getIsland()))));
    }

    if (criteria.getStationId() != null && !criteria.getStationId().equals("")) {
      e = e.and(lower(i("stationId")).eq(lower(s(criteria.getStationId()))));
    }

    if (criteria.getFromActivityDate() == null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());
      cal.add(Calendar.DATE, -1);
      criteria.setFromActivityDate(cal.getTime());
    } else {
      Calendar cal = Calendar.getInstance();
      cal.setTime(criteria.getFromActivityDate());
      cal.add(Calendar.HOUR, -12);
      criteria.setFromActivityDate(cal.getTime());
    }

    e = e.and(
        i("activityDate").gte(s(criteria.getFromActivityDate().toInstant().atZone(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT))));

    if (criteria.getToActivityDate() != null) {
      e = e.and(
          i("activityDate").lte(s(criteria.getToActivityDate().toInstant().atZone(ZoneOffset.UTC)
              .format(DateTimeFormatter.ISO_INSTANT))));
    }

    if (criteria.getDataType() == null || criteria.getDataType().equals("")) {
      criteria.setDataType("ALL");
    }

    e = e.and(i("detectionList").isValued());

    if (criteria.getUhfId() == null) {
      if (criteria.getDataType().equalsIgnoreCase("DETECT")) {
        e = e.and(par(anyIn("dt", i("detectionList")).satisfies(
            lower("dt.category").eq(lower(s("Detection"))))));
      } else if (criteria.getDataType().equalsIgnoreCase("STANDARD")) {
        e = e.and(par(anyIn("dt", i("detectionList")).satisfies(
            lower("dt.category").eq(lower(s("Standard"))))));
      } else if (criteria.getDataType().equalsIgnoreCase("Egg timer")) {
        e = e.and(par(anyIn("dt", i("detectionList")).satisfies(
            lower("dt.category").like(lower(s("%Egg timer"))))));
      } else if (criteria.getDataType().equalsIgnoreCase("Checkmate")) {
        e = e.and(par(anyIn("dt", i("detectionList")).satisfies(
            lower("dt.category").like(lower(s("%Checkmate"))))));
      } else if (!criteria.getDataType().equalsIgnoreCase("ALL")) {
        e = e.and(par(anyIn("dt", i("detectionList")).satisfies(
            lower("dt.category").like(lower(s(criteria.getDataType()))))));
      }
    } else {
      if (criteria.getDataType().equalsIgnoreCase("ALL")) {
        e = e.and(par(
            anyIn("dt", i("detectionList")).satisfies(
                    x("dt.uhfId").eq(toNumber(x(criteria.getUhfId()))))));
      } else if (criteria.getDataType().equalsIgnoreCase("DETECT")) {
        e = e.and(par(anyIn("dt", i("detectionList")).satisfies(
            x("dt.uhfId").eq(toNumber(x(criteria.getUhfId())))
                .and(lower("dt.category").eq(lower(s("Detection")))))));
      } else if (criteria.getDataType().equalsIgnoreCase("STANDARD")) {
        e = e.and(par(anyIn("dt", i("detectionList")).satisfies(
            x("dt.uhfId").eq(toNumber(x(criteria.getUhfId())))
                .and(lower("dt.category").eq(lower(s("Standard")))))));
      } else if (criteria.getDataType().equalsIgnoreCase("Egg timer")) {
        e = e.and(par(anyIn("dt", i("detectionList")).satisfies(
            x("dt.uhfId").eq(toNumber(x(criteria.getUhfId())))
                .and(lower("dt.category").like(lower(s("%Egg timer")))))));
      } else if (criteria.getDataType().equalsIgnoreCase("Checkmate")) {
        e = e.and(par(anyIn("dt", i("detectionList")).satisfies(
            x("dt.uhfId").eq(toNumber(x(criteria.getUhfId())))
                .and(lower("dt.category").like(lower(s("%Checkmate")))))));
      } else {
        e = e.and(par(anyIn("dt", i("detectionList")).satisfies(
            x("dt.uhfId").eq(toNumber(x(criteria.getUhfId())))
                .and(lower("dt.category").like(lower(s(criteria.getDataType())))))));
      }
    }

    // get the ids of the matching documents first as it seems to be faster to break it up in this way
    Statement idsStatement;
    // need to select all records that match, order not important
    idsStatement = select(x("meta().id").as(i("id")))
        .from(bucket)
        .where(e);

    N1qlQuery idsQuery = N1qlQuery.simple(idsStatement);

    List<String> selectedIDs = new ArrayList<>();

    template.queryN1QL(idsQuery).forEach(result -> selectedIDs.add(result.value().getString("id")));

    List<NoraNetSearchStationDTO> results;
    List<NoraNetSearchStationDTO> resultsCount;

    if (selectedIDs.isEmpty()) {
      results = Collections.emptyList();
      resultsCount = Collections.emptyList();
    } else {
      // Use the ids to get the results to populate the search dto
      // Exempt from sync filter because where clause only picking from list of IDs above.
      String queryString = "SELECT " +
          "meta(nn).id AS noraNetId, " +
          "nn.island AS island, " +
          "nn.stationId AS stationId, " +
          "nn.activityDate AS activityDate, " +
          "nn.batteryVolts AS batteryVolts, " +
          "dt.uhfId AS uhfId, " +
          "(ARRAY_REVERSE(ARRAY_AGG(" +
          "OBJECT_CONCAT({\"birdName\":b.birdName},{\"birdID\":birds.birdID})))) AS birdList, " +
          "dt.pulseCount AS pulseCount, " +
          "dt.category AS detectionType " +
          "FROM `kakapo-bird` nn " +
          "UNNEST detectionList AS dt " +
          "LEFT UNNEST dt.birdList as birds " +
          "LEFT JOIN `kakapo-bird` b ON KEYS birds.birdID " +
          "WHERE meta(nn).id IN $selectedIDs " +
          (criteria.getUhfId() != null ? " AND dt.uhfId = $uhfId " : "") +
          (criteria.getDataType().equalsIgnoreCase("Detect") ? " AND LOWER(dt.category) = LOWER('Detection') " : "") +
          (criteria.getDataType().equalsIgnoreCase("Standard") ? " AND LOWER(dt.category) = LOWER('Standard') " : "") +
          (criteria.getDataType().equalsIgnoreCase("Egg timer") ? " AND LOWER(dt.category) LIKE LOWER('%Egg Timer') " : "") +
          (criteria.getDataType().equalsIgnoreCase("Checkmate") ? " AND LOWER(dt.category) LIKE LOWER('%Checkmate') " : "") +
          "GROUP BY nn, dt ";

      //build and execute query for total count purposes, no ordering, limit or offset
      N1qlQuery queryCount = N1qlQuery.parameterized(queryString, JsonObject.create()
          .put("selectedIDs", selectedIDs)
          .put("uhfId", criteria.getUhfId()));

      resultsCount = template.findByN1QLProjection(queryCount, NoraNetSearchStationDTO.class);

      // add order by, limit and offset for data to return
      queryString = queryString +
          "\nORDER BY island, activityDate DESC, stationId, uhfId " +
          "LIMIT " + criteria.getPageSize() + " " +
          "OFFSET " + criteria.getOffset();

      N1qlQuery query = N1qlQuery.parameterized(queryString, JsonObject.create()
          .put("selectedIDs", selectedIDs)
          .put("uhfId", criteria.getUhfId()));
      results = template.findByN1QLProjection(query, NoraNetSearchStationDTO.class);
    }

    PagedResponse<NoraNetSearchStationDTO> pagedResponse = new PagedResponse<>();

    Integer totalCount = resultsCount.size();
    pagedResponse.setPage(criteria.getPageNumber());
    pagedResponse.setPageSize(criteria.getPageSize());
    pagedResponse.setTotal(totalCount);
    pagedResponse.setResults(results);

    return pagedResponse;
  }

  @Override
  public PagedResponse<NoraNetSearchSnarkDTO> findSnarkDTOByCriteria(NoraNetCriteria criteria) {
    Expression e = x("nn.docType").eq(s("NoraNet"));
    e = e.and(queryUtils.notLikeSyncId("nn"));
    e = e.and(x("nn.weightList").exists());

    if (criteria.getIsland() != null && !criteria.getIsland().equals("")) {
      e = e.and(lower(x("nn.island")).eq(lower(s(criteria.getIsland()))));
    }
    if (criteria.getStationId() != null && !criteria.getStationId().equals("")) {
      e = e.and(lower(x("nn.stationId")).eq(lower(s(criteria.getStationId()))));
    }

    if (criteria.getFromActivityDate() == null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());
      cal.add(Calendar.DATE, -1);
      criteria.setFromActivityDate(cal.getTime());
    } else {
      Calendar cal = Calendar.getInstance();
      cal.setTime(criteria.getFromActivityDate());
      cal.add(Calendar.HOUR, -12);
      criteria.setFromActivityDate(cal.getTime());
    }
    e = e.and(
        x("nn.activityDate").gte(s(criteria.getFromActivityDate().toInstant().atZone(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT))));

    if (criteria.getToActivityDate() != null) {
      e = e.and(
          x("nn.activityDate").lte(s(criteria.getToActivityDate().toInstant().atZone(ZoneOffset.UTC)
              .format(DateTimeFormatter.ISO_INSTANT))));
    }

    String fromClause = " `kakapo-bird` nn";

    String queryString = "SELECT " +
        "meta(nn).id AS noraNetId, " +
        "nn.island AS island, " +
        "nn.stationId AS stationId, " +
        "nn.activityDate AS activityDate, " +
        "ARRAY TO_STRING(item.binCount) ||' weights recorded from ' ||" +
        "(TO_STRING(item.weightBin * 100)) || 'g-' || (TO_STRING((item.weightBin+1)*100)) || 'g' || " +
        "' with max quality=' || TO_STRING(item.maxQuality) " +
        "FOR item IN nn.weightList END AS weightData" +
        "\nFROM " +
        fromClause +
        "\nWHERE " + e.toString() +
        "\nGROUP BY nn " +
        "\nORDER BY nn.island, nn.activityDate DESC, nn.stationId " +
        "\nLIMIT " + criteria.getPageSize() +
        "\nOFFSET " + criteria.getOffset();

    N1qlQuery query = N1qlQuery.simple(queryString);
    List<NoraNetSearchSnarkDTO> resultList = template.findByN1QLProjection(query, NoraNetSearchSnarkDTO.class);

    Integer totalCount = 0;
    if (!resultList.isEmpty()) {
      String countQueryString = "SELECT COUNT(results) as count " +
          " FROM (SELECT meta(nn).id id " +
          "\n      FROM " +
          fromClause +
          "\n      WHERE " +
          e + ") results";
      N1qlQuery countQuery = N1qlQuery.simple(countQueryString);
      totalCount = template.queryN1QL(countQuery).allRows().get(0).value().getInt("count");
    }

    PagedResponse<NoraNetSearchSnarkDTO> pagedResponse = new PagedResponse();
    pagedResponse.setPage(criteria.getPageNumber());
    pagedResponse.setPageSize(criteria.getPageSize());
    pagedResponse.setTotal(totalCount);
    pagedResponse.setResults(resultList);

    return pagedResponse;
  }

}

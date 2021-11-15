package com.fronde.server.services.record;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.Select.selectDistinct;
import static com.couchbase.client.java.query.Select.selectRaw;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.par;
import static com.couchbase.client.java.query.dsl.Expression.path;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.sub;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.functions.Collections.anyIn;
import static com.couchbase.client.java.query.dsl.functions.MetaFunctions.meta;
import static com.couchbase.client.java.query.dsl.functions.StringFunctions.lower;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.Expression;
import com.couchbase.client.java.query.dsl.Sort;
import com.couchbase.client.java.query.dsl.path.GroupByPath;
import com.fronde.server.domain.MeasureDetailEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.options.BandsAndChipsDTO;
import com.fronde.server.services.options.CurrentTransmitterInfoDTO;
import com.fronde.server.services.options.TransmitterBirdHistoryDTO2;
import com.fronde.server.utils.CommonUtils;
import com.fronde.server.utils.QueryUtils;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.repository.query.support.N1qlUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RecordRepositoryImpl implements RecordRepositoryCustom {

  @Autowired
  protected CouchbaseTemplate template;

  @Autowired
  protected QueryUtils queryUtils;

  @Autowired
  protected CommonUtils commonUtils;

  private final String wildcard = "%";

  public PagedResponse<RecordEntity> findByCriteria(RecordCriteria criteria) {
    Expression e = x("r.docType").eq(s("Record"));
    e = e.and(queryUtils.notLikeSyncId("r"));

    e = addCriteriaExpression(criteria, e);

    PagedResponse<RecordEntity> pagedResponse = queryUtils
        .query(criteria, e, RecordEntity.class, Sort.desc("r.dateTime"));
    return pagedResponse;
  }

  @Override
  public PagedResponse<RecordSearchDTO> findSearchDTOByCriteria(RecordCriteria criteria) {
    Expression bucketName = i(template.getCouchbaseBucket().name());
    Expression e = x("r.docType").eq(s("Record"));
    e = e.and(queryUtils.notLikeSyncId("r"));

    e = addCriteriaExpression(criteria, e);

    Statement statement = select(
        x("meta(r).id as recordID"),
        x("meta(b).id as birdID"),
        x("b.birdName"),
        x("r.dateTime"),
        x("r.island"),
        x("r.locationID"),
        x("l.locationName"),
        x("l.easting").as("locationEasting"),
        x("l.northing").as("locationNorthing"),
        x("r.observerList"),
        x("r.recordType"),
        x("r.activity"),
        x("r.reason"),
        x("r.subReason"),
        x("r.easting"),
        x("r.northing"),
        x("r.weight.weight"),
        x("(r.comments is valued ) AS hasComment"),
        x("((r.bloodSampleDetail.bloodSampleList IS VALUED AND r.bloodSampleDetail.bloodSampleList <> []) " +
          "OR (r.otherSampleList IS VALUED AND r.otherSampleList <> []) " +
          "OR (r.swabSampleList IS VALUED AND r.swabSampleList <> []) " +
          "OR (r.spermSampleList IS VALUED AND r.spermSampleList <> []) ) as hasSample")
        )
        .from(bucketName).as("r")
        .leftJoin(bucketName).as("b").onKeys(x("r.birdID"))
        .leftJoin(bucketName).as("l").onKeys(x("r.locationID"))
        .where(e)
        .orderBy(Sort.desc("r.dateTime"))
        .limit(criteria.getPageSize())
        .offset(criteria.getOffset());

    List<RecordSearchDTO> list = new ArrayList<>();
    N1qlQuery query = N1qlQuery.simple(statement);

    System.out.println("findSearchDTOByCriteria(): " + query);

    Set<String> recorderIDSet = new HashSet<>();
    template.queryN1QL(query).forEach(row -> {

      JsonObject jsonObject = row.value();

      String recorderID = null;
      JsonArray observerList = jsonObject.getArray("observerList");
      if (observerList != null) {
        for (Object nextObserverObj : observerList) {
          JsonObject j = (JsonObject) nextObserverObj;
          JsonArray observationRoles = j.getArray("observationRoles");
          if (observationRoles != null && observationRoles.toList().contains("Recorder")) {
            recorderID = j.getString("personID");
            recorderIDSet.add(recorderID);
            break;
          }
        }
      }

      RecordSearchDTO dto = new RecordSearchDTO();
      dto.setRecordID(jsonObject.getString("recordID"));
      dto.setBirdID(jsonObject.getString("birdID"));
      dto.setBirdName(jsonObject.getString("birdName"));
      dto.setDateTime(commonUtils.nullSafeGetDate(jsonObject.getString("dateTime")));
      dto.setIsland(jsonObject.getString("island"));
      dto.setLocationID(jsonObject.getString("locationID"));
      dto.setLocationName(jsonObject.getString("locationName"));
      dto.setLocationEasting(CommonUtils.nullSafeGetFloat(jsonObject.getNumber("locationEasting")));
      dto.setLocationNorthing(
          CommonUtils.nullSafeGetFloat(jsonObject.getNumber("locationNorthing")));
      dto.setRecorder(recorderID);
      dto.setRecordType(jsonObject.getString("recordType"));
      dto.setActivity(jsonObject.getString("activity"));
      dto.setReason(jsonObject.getString("reason"));
      dto.setSubReason(jsonObject.getString("subReason"));
      dto.setEasting(CommonUtils.nullSafeGetFloat(jsonObject.getNumber("easting")));
      dto.setNorthing(CommonUtils.nullSafeGetFloat(jsonObject.getNumber("northing")));
      dto.setWeight(CommonUtils.nullSafeGetFloat(jsonObject.getNumber("weight")));
      dto.setHasComment(jsonObject.getBoolean("hasComment"));
      dto.setHasSample(jsonObject.getBoolean("hasSample"));

      list.add(dto);
    });

    Map<String, String> personNameMap = findPersonNames(recorderIDSet);
    list.forEach(dto -> {
      dto.setRecorder(personNameMap.get(dto.getRecorder()));
    });

    Integer totalCount = queryUtils.countRows(e, "r");
    PagedResponse pagedResponse = new PagedResponse();
    pagedResponse.setPage(criteria.getPageNumber());
    pagedResponse.setPageSize(criteria.getPageSize());
    pagedResponse.setTotal(totalCount);
    pagedResponse.setResults(list);

    return pagedResponse;
  }

  private Expression like(String key, List<String> options) {
    Expression e = lower(x(key)).like(lower(s(options.get(0) + wildcard)));

    for (int i = 1; i < options.size(); i++) {
      e = e.or(lower(x(key)).like(lower(s(options.get(i) + wildcard))));
    }

    return par(e);
  }

  private Expression addCriteriaExpression(RecordCriteria criteria, Expression e) {
    if (criteria.getBirdIDs() != null && criteria.getBirdIDs().size() > 0) {
      e = e.and(x("r.birdID").in(JsonArray.from(criteria.getBirdIDs())));
    }
    if (criteria.getRecordTypes() != null && criteria.getRecordTypes().size() > 0) {
      List<String> rTypes = toLowerArray(criteria.getRecordTypes());
      if (rTypes.get(0).endsWith(wildcard)){
        Expression orE = x("false");
        orE = orE.or(lower(x("r.recordType")).like(lower(s(rTypes.get(0)))));
        rTypes.remove(0);
        if (rTypes.size() > 0){
          orE = orE.or(lower("r.recordType").in(JsonArray.from(rTypes)));
        }
        e = e.and(par(orE));
      }
      else
      {
        e = e.and(lower("r.recordType").in(JsonArray.from(rTypes)));
      }
    }
    if (criteria.getActivity() != null) {
      e = e.and(lower(x("r.activity")).like(lower(s(criteria.getActivity() + wildcard))));
    }
    if (criteria.getIsland() != null) {
      e = e.and(lower(x("r.island")).like(lower(s(criteria.getIsland() + wildcard))));
    }
    if (criteria.getLocationID() != null) {
      e = e.and(x("r.locationID").eq(s(criteria.getLocationID())));
    }
    if (criteria.getObserverPersonID() != null) {
      e = e.and(anyIn("o", x("r.observerList"))
          .satisfies(x("o.personID").eq(s(criteria.getObserverPersonID()))));
    }

    if (criteria.getFromDate() != null) {
      e = e.and(x("r.dateTime").gt(s(criteria.getFromDate().toInstant().atZone(ZoneOffset.UTC)
          .format(DateTimeFormatter.ISO_INSTANT))));
    }

    if (criteria.getToDate() != null) {
      e = e.and(x("r.dateTime").lt(s(criteria.getToDate().toInstant().atZone(ZoneOffset.UTC)
          .format(DateTimeFormatter.ISO_INSTANT))));
    }

    if (criteria.getSignificantEventOnly() != null && criteria.getSignificantEventOnly()) {
      e = e.and(x("r.significantEvent"));
    }

    if (criteria.getReasons() != null  && criteria.getReasons().size() > 0) {
      List<String> rReasons = toLowerArray(criteria.getReasons());
      if (rReasons.get(0).endsWith(wildcard)){
        Expression orE = x("false");
        orE = orE.or(lower(x("r.reason")).like(lower(s(rReasons.get(0)))));
        rReasons.remove(0);
        if (rReasons.size() > 0){
          orE = orE.or(lower("r.reason").in(JsonArray.from(rReasons)));
        }
        e = e.and(par(orE));
      }
      else
      {
        e = e.and(lower("r.reason").in(JsonArray.from(rReasons)));
      }
    }

    if (criteria.getWithWeightOnly() != null && criteria.getWithWeightOnly()) {
      e = e.and(x("r.weight.weight").isNotMissing());
    }

    return e;
  }

  private List<String> toLowerArray(List<String> inArray){
    Object[] inA = inArray.toArray();
    List<String>  outA = new ArrayList<>();
    for(int i = 0; i < inA.length; i++) {
      outA.add(((String)inA[i]).toLowerCase());
    }
    return outA;
  }

  private Map<String, String> findPersonNames(Set<String> personIDSet) {
    Map<String, String> map = new HashMap<>();

    // Exempt from notLikeSyncId() check searching for Ids in array
    Statement statement = select(x("p.name"), x("meta(p).id"))
        .from(i(template.getCouchbaseBucket().name())).as("p")
        .where(
            x("p.docType").eq(s("Person"))
                .and(x("meta(p).id").in(JsonArray.from(personIDSet.toArray())))
        );

    N1qlQuery query = N1qlQuery.simple(statement);
    template.queryN1QL(query).forEach(row -> {
      map.put(row.value().getString("id"), row.value().getString("name"));
    });

    return map;
  }

  @Override
  public List<String> findRecordTypes() {
    Expression e = x("docType").eq(s("Record"));
    e = e.and(queryUtils.notLikeSyncId());

    Statement statement = selectDistinct(i("recordType"))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e)
        .orderBy(Sort.asc("recordType"));

    N1qlQuery query = N1qlQuery.simple(statement);

    List<String> list = new ArrayList<>();
    template.queryN1QL(query).forEach(n1qlQueryRow -> {
      list.add(n1qlQueryRow.value().getString("recordType"));
    });

    return list;
  }

  @Override
  public List<String> findRecordActivities() {
    Expression e = x("docType").eq(s("Record"));
    e = e.and(queryUtils.notLikeSyncId());

    Statement statement = selectDistinct(i("activity"))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e)
        .orderBy(Sort.asc("activity"));

    N1qlQuery query = N1qlQuery.simple(statement);

    List<String> list = new ArrayList<>();
    template.queryN1QL(query).forEach(n1qlQueryRow -> {
      list.add(n1qlQueryRow.value().getString("activity"));
    });

    return list;
  }

  @Override
  public List<String> findRecordReasons() {
    Expression e = x("docType").eq(s("Record"));
    e = e.and(queryUtils.notLikeSyncId());

    Statement statement = selectDistinct(i("reason"))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e)
        .orderBy(Sort.asc("reason"));

    N1qlQuery query = N1qlQuery.simple(statement);

    List<String> list = new ArrayList<>();
    template.queryN1QL(query).forEach(n1qlQueryRow -> {
      list.add(n1qlQueryRow.value().getString("reason"));
    });

    return list;
  }

  @Override
  public BandsAndChipsDTO findIdInfoByBirdId(String birdId) {
    List<BandsAndChipsDTO> list = findBandAndChipsForBirdID(birdId, true, true);

    BandsAndChipsDTO bncResult = new BandsAndChipsDTO();

    for (BandsAndChipsDTO bandc : list) {
      // if we find a microchip then set it
      if (bncResult.getChipDateTime() == null && bandc.getMicrochip() != null) {
        bncResult.setChipDateTime(bandc.getChipDateTime());
        bncResult.setMicrochip(bandc.getMicrochip());
      }
      // if we find a band then set it
      if (bncResult.getBandDateTime() == null && bandc.getNewBandNumber() != null) {
        bncResult.setBandDateTime(bandc.getBandDateTime());
        bncResult.setNewBandNumber(bandc.getNewBandNumber());
      }
      // if we have found both the most recent band and most recent chip then break
      if (bncResult.getBandDateTime() != null && bncResult.getChipDateTime() != null) {
        break;
      }
    }

    return bncResult;
  }

  @Override
  public List<BandsAndChipsDTO> findBandHistoryByBirdId(String birdId) {
    return findBandAndChipsForBirdID(birdId, true, false);
  }

  @Override
  public List<BandsAndChipsDTO> findChipHistoryByBirdId(String birdId) {
    return findBandAndChipsForBirdID(birdId, false, true);
  }

  @Override
  public CurrentTransmitterInfoDTO findCurrentTransmitterInfoByBirdId(String birdId) {
    Expression e = i("docType").eq(s("Record"));
    e = e.and(queryUtils.notLikeSyncId());
    e = e.and(i("birdID").eq(s(birdId)));
    e = e.and(i("transmitterChange").isValued());

    Statement statement = selectDistinct(i("transmitterChange"), i("dateTime"))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e)
        .orderBy(Sort.asc("dateTime"));

    N1qlQuery query = N1qlQuery.simple(statement);

    CurrentTransmitterInfoDTO tceResult = new CurrentTransmitterInfoDTO();

    template.queryN1QL(query).forEach(result -> {
      String dateTimeStr = result.value().getString("dateTime");
      if (dateTimeStr != null) {
        Date dateTime = commonUtils.nullSafeGetDate(dateTimeStr);
        JsonObject txIdObj = result.value().getObject("transmitterChange");
        if (txIdObj != null) {
          String transmitterId = txIdObj.getString("txTo");
          tceResult.setDocId(transmitterId);
          if (tceResult.getDateTime() == null || dateTime.after(tceResult.getDateTime())) {
            tceResult.setDateTime(dateTime);
          }

        }
      }
    });

    return tceResult;
  }

  private List<String> findIdByBirdID(String birdID) {
    Expression e = x("docType").eq(s("Record"));
    e = e.and(queryUtils.notLikeSyncId());
    e = e.and(i("birdID").eq(s(birdID)));

    Statement statement = select(x("meta().id").as(i("id")))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e);

    N1qlQuery query = N1qlQuery.simple(statement);

    List<String> results = new ArrayList<>();

    template.queryN1QL(query).forEach(result -> {
      results.add(result.value().getString("id"));
    });

    return results;
  }

  @Override
  public DatedMeasureDetailDTO getCurrentMeasureDetailByBirdID(String birdID) {
    List<DatedMeasureDetailDTO> history = getMeasureDetailHistoryByBirdID(birdID);

    DatedMeasureDetailDTO result = new DatedMeasureDetailDTO();
    result.setMeasureDetail(new MeasureDetailEntity());
    for (DatedMeasureDetailDTO dmd : history) {
      MeasureDetailEntity md = dmd.getMeasureDetail();
      if (result.getDateTime() == null) {
        result.setDateTime(dmd.getDateTime());
      }
      if (result.getMeasureDetail().getCulmenAndCere() == null && md.getCulmenAndCere() != null
          && md.getCulmenAndCere() != 0.0f) {
        result.getMeasureDetail().setCulmenAndCere(md.getCulmenAndCere());
      }
      if (result.getMeasureDetail().getCulmenLength() == null && md.getCulmenLength() != null
          && md.getCulmenLength() != 0.0f) {
        result.getMeasureDetail().setCulmenLength(md.getCulmenLength());
      }
      if (result.getMeasureDetail().getCulmenWidth() == null && md.getCulmenWidth() != null
          && md.getCulmenWidth() != 0.0f) {
        result.getMeasureDetail().setCulmenWidth(md.getCulmenWidth());
      }
      if (result.getMeasureDetail().getFemur() == null && md.getFemur() != null
          && md.getFemur() != 0) {
        result.getMeasureDetail().setFemur(md.getFemur());
      }
      if (result.getMeasureDetail().getLeg() == null && md.getLeg() != null && !StringUtils
          .isEmpty(md.getLeg())) {
        result.getMeasureDetail().setLeg(md.getLeg());
      }
      if (result.getMeasureDetail().getLongestToe() == null && md.getLongestToe() != null
          && md.getLongestToe() != 0.0f) {
        result.getMeasureDetail().setLongestToe(md.getLongestToe());
      }
      if (result.getMeasureDetail().getLongToeClaw() == null && md.getLongToeClaw() != null
          && md.getLongToeClaw() != 0.0f) {
        result.getMeasureDetail().setLongToeClaw(md.getLongToeClaw());
      }
      if (result.getMeasureDetail().getRecoveryID() == null && md.getRecoveryID() != null
          && !StringUtils.isEmpty(md.getRecoveryID())) {
        result.getMeasureDetail().setRecoveryID(md.getRecoveryID());
      }
      if (result.getMeasureDetail().getSternumshoulder() == null && md.getSternumshoulder() != null
          && md.getSternumshoulder() != 0) {
        result.getMeasureDetail().setSternumshoulder(md.getSternumshoulder());
      }
      if (result.getMeasureDetail().getTailLength() == null && md.getTailLength() != null
          && md.getTailLength() != 0.0f) {
        result.getMeasureDetail().setTailLength(md.getTailLength());
      }
      if (result.getMeasureDetail().getTarsusDepthSquashed() == null
          && md.getTarsusDepthSquashed() != null && md.getTarsusDepthSquashed() != 0.0f) {
        result.getMeasureDetail().setTarsusDepthSquashed(md.getTarsusDepthSquashed());
      }
      if (result.getMeasureDetail().getTarsusDepthUnsquashed() == null
          && md.getTarsusDepthUnsquashed() != null && md.getTarsusDepthUnsquashed() != 0.0f) {
        result.getMeasureDetail().setTarsusDepthUnsquashed(md.getTarsusDepthUnsquashed());
      }
      if (result.getMeasureDetail().getTarsusLength() == null && md.getTarsusLength() != null
          && md.getTarsusLength() != 0.0f) {
        result.getMeasureDetail().setTarsusLength(md.getTarsusLength());
      }
      if (result.getMeasureDetail().getTarsusWidthSquashed() == null
          && md.getTarsusWidthSquashed() != null && md.getTarsusWidthSquashed() != 0.0f) {
        result.getMeasureDetail().setTarsusWidthSquashed(md.getTarsusWidthSquashed());
      }
      if (result.getMeasureDetail().getTarsusWidthUnsquashed() == null
          && md.getTarsusWidthUnsquashed() != null && md.getTarsusWidthUnsquashed() != 0.0f) {
        result.getMeasureDetail().setTarsusWidthUnsquashed(md.getTarsusWidthUnsquashed());
      }
      if (result.getMeasureDetail().getWingLength() == null && md.getWingLength() != null
          && md.getWingLength() != 0.0f) {
        result.getMeasureDetail().setWingLength(md.getWingLength());
      }
    }

    return result;
  }

  @Override
  public List<DatedMeasureDetailDTO> getMeasureDetailHistoryByBirdID(String birdID) {
    List<String> ids = findIdByBirdID(birdID);
    return getMeasureDetailHistoryByIds(ids);
  }

  private List<DatedMeasureDetailDTO> getMeasureDetailHistoryByIds(List<String> ids) {
    // querying by id only performs where there are actual ids so save time and return an empty list when no ids
    if (ids.isEmpty()) {
      return java.util.Collections.emptyList();
    } else {
      Statement statement = getMeasureDetailHistoryStatement(ids);

      N1qlQuery query = N1qlQuery.simple(statement);

      return template.findByN1QLProjection(query, DatedMeasureDetailDTO.class);
    }
  }

  private Statement getMeasureDetailHistoryStatement(List<String> ids) {
    Statement statement =
        select(i("dateTime"), i("measureDetail"))
            .from(i(template.getCouchbaseBucket().name()))
            .where(
                x("meta().id").in(JsonArray.from(ids))
                    .and(queryUtils.notLikeSyncId())
                    .and(x("object_length(measureDetail)").gt(0)))
            .orderBy(Sort.desc(i("dateTime")));

    return statement;
  }

  private List<BandsAndChipsDTO> findBandAndChipsForBirdID(String birdID, boolean includeBands,
      boolean includeChips) {
    Expression orE = x("false");
    if (includeBands) {
      orE = orE.or(x("bands.newBandNumber").isValued());
    }
    if (includeChips) {
      orE = orE.or(x("chips.microchip").isValued());
    }

    Expression e = i("docType").eq(s("Record"))
        .and(queryUtils.notLikeSyncId())
        .and(i("birdID").eq(s(birdID))
            .and(par(orE)));

    Statement statement = selectDistinct(
        i("dateTime").as("chipDateTime"),
        i("dateTime").as("bandDateTime"),
        x("bands.leg").as("leg"),
        x("bands.newBandNumber").as("newBandNumber"),
        x("chips.microchip").as("microchip"))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e)
        .orderBy(Sort.asc("dateTime"));

    N1qlQuery query = N1qlQuery.simple(statement);

    List<BandsAndChipsDTO> list = template.findByN1QLProjection(query, BandsAndChipsDTO.class);

    return list;
  }

  @Override
  public RecordEntity findLatestRecordWithPartByBirdID(String birdID, String part) {
    return findLatestRecordWithPartByBirdIDExcluding(birdID, part, null);
  }

  @Override
  public RecordEntity findLatestRecordWithPartByBirdIDExcluding(String birdID, String part,
      String excluding) {
    Statement statement = N1qlUtils
        .createSelectClauseForEntity(template.getCouchbaseBucket().name())
        .from(i(template.getCouchbaseBucket().name()))
        .where(
            i("docType").eq(s("Record"))
                .and(queryUtils.notLikeSyncId())
                .and(i(part).isValued()
                    .and(i("birdID").eq(s(birdID))))
                .and((excluding != null ? path(meta(""), "id").ne(s(excluding)) : x("true"))))
        .orderBy(Sort.desc("dateTime"))
        .limit(1);

    N1qlQuery query = N1qlQuery.simple(statement);
    List<RecordEntity> list = template.findByN1QL(query, RecordEntity.class);

    if (list.isEmpty()) {
      return null;
    } else {
      return list.get(0);
    }
  }

  @Override
  public List<RecordEntity> findWithPartByBirdID(String birdID, String part) {

    Statement statement = N1qlUtils
        .createSelectClauseForEntity(template.getCouchbaseBucket().name())
        .from(i(template.getCouchbaseBucket().name()))
        .where(i("docType").eq(s("Record"))
            .and(queryUtils.notLikeSyncId())
            .and(i(part).isValued()
                .and(i("birdID").eq(s(birdID)))))
        .orderBy(Sort.desc("dateTime"));

    N1qlQuery query = N1qlQuery.simple(statement);
    List<RecordEntity> list = template.findByN1QL(query, RecordEntity.class);

    return list;
  }

  @Override
  public List<BirdTransmitterHistoryDTO> findTransmitterHistoryDTOByBirdID(String birdID) {
    Expression bucket = i(template.getCouchbaseBucket().name());

    Expression e =
        path("r", i("docType")).eq(s("Record"))
            .and(queryUtils.notLikeSyncId("r"))
            .and(path("r", i("transmitterChange").isValued()))
            .and(path("r", i("birdID")).eq(s(birdID)));

    Statement statement = select(
        path("r", i("transmitterChange"), i("txTo")).as("txDocId"),
        path("r", i("dateTime")).as("recordDateTime"),
        path("r", i("island")).as("recordIsland"),
        path("t", i("channel")),
        path("txm", i("name").as("mortType")),
        path("t", i("software")),
        path("r", i("transmitterChange"), i("newStatus")).as("newStatus"),
        path("r", i("transmitterChange"), i("newTxFineTune")).as("txFineTune"),
        path("t", i("txId")),
        path("t", i("uhfId")))
        .from(bucket.as("r"))
        .join(bucket.as("t")).onKeys(path("r", i("transmitterChange"), i("txTo")))
        .join(bucket.as("txm")).onKeys(path("t", i("txMortalityId")))
        .where(e)
        .orderBy(Sort.desc(path("r", i("dateTime"))));

    N1qlQuery query = N1qlQuery.simple(statement);

    return template.findByN1QLProjection(query, BirdTransmitterHistoryDTO.class);
  }

  @Override
  public List<TransmitterBirdHistoryDTO> findTransmitterBirdHistoryDTOByTxDocId(String txDocId) {
    Expression bucket = i(template.getCouchbaseBucket().name());

    Expression e1 =
        path("r", i("docType")).eq(s("Record"))
            .and(queryUtils.notLikeSyncId("r"))
            .and(path("b", i("birdName").ne(s("Unknown"))))
            .and(path("r", i("transmitterChange"), i("txTo")).eq(s(txDocId)));

    GroupByPath s1 = selectRaw("{ \n" +
        path("r", i("birdID")) + ", \n" +
        path("b", i("birdName")) + ", \n" +
        path("r", i("dateTime")) + ", \n" +
        s("deployedDateTime") + ": " + path("r", i("dateTime")) + ", \n" +
        path("r", i("transmitterChange"), i("newTxFineTune")) + " }")
        .from(bucket).as("r")
        .join(bucket).as("b").onKeys(path("r", i("birdID")))
        .where(e1);

    Expression e2 =
        path("r", i("docType")).eq(s("Record"))
            .and(queryUtils.notLikeSyncId("r"))
            .and(path("b", i("birdName").ne(s("Unknown"))))
            .and(path("r", i("transmitterChange"), i("txFrom")).eq(s(txDocId)));

    GroupByPath s2 = selectRaw("{ \n" +
        path("r", i("birdID")) + ", \n" +
        path("b", i("birdName")) + ", \n" +
        path("r", i("dateTime")) + ", \n" +
        s("removedDateTime") + ": " + path("r", i("dateTime")) + "}")
        .from(bucket).as("r")
        .join(bucket).as("b").onKeys(path("r", i("birdID")))
        .where(e2);

    Statement s3 = select(i("birdID"), i("birdName"), i("deployedDateTime"), i("removedDateTime"),
        i("newTxFineTune"))
        .from(sub((s1.unionAll(s2)))).as("dh")
        // ordering by deployedDateTime asc means removed items will appear in list second (NULL is lower than a value)
        .orderBy(Sort.desc(i("dateTime")), Sort.desc(i("birdID")), Sort.asc(i("deployedDateTime")));

    N1qlQuery query = N1qlQuery.simple(s3);

    List<TransmitterBirdHistoryDTO> result = template
        .findByN1QLProjection(query, TransmitterBirdHistoryDTO.class);

    int i = 0;
    while (i < result.size()) {
      TransmitterBirdHistoryDTO current = result.get(i);
      boolean hasNext = i + 1 < result.size();
      if (current.getRemovedDateTime() != null && current.getDeployedDateTime() == null
          && hasNext) {
        TransmitterBirdHistoryDTO next = result.get(i + 1);
        if (next.getRemovedDateTime() == null && next.getBirdID().equals(current.getBirdID())) {
          next.setRemovedDateTime(current.getRemovedDateTime());
          result.remove(i);
        }
      }
      i++;
    }

    return result;
  }

  @Override
  public List<TransmitterBirdHistoryDTO2> findTransmitterBirdHistory(String txID) {
    List<TransmitterBirdHistoryDTO2> added = getAddedTxList(txID);
    List<TransmitterBirdHistoryDTO2> removed = getRemovedTxList(txID);

    added.forEach(addedItem -> removed.forEach(removedItem -> {
      //If birdID's match.
      if (addedItem.getBirdId().equalsIgnoreCase(removedItem.getBirdId())) {
        //If removed date is after the added date.
        if (!removedItem.getRemovedDate().before(addedItem.getInstalledDate())) {
          addedItem.setRemovedDate(removedItem.getRemovedDate());
        }
      }
    }));

    added.sort(Comparator.comparing(TransmitterBirdHistoryDTO2::getInstalledDate).reversed());

    return added;
  }

  private List<TransmitterBirdHistoryDTO2> getAddedTxList(String txID) {
    Expression e1 = x("r.docType").eq(s("Record"))
        .and(queryUtils.notLikeSyncId("r"))
        .and(x("r.transmitterChange.txTo").eq(s(txID)))
        .and(x("b.birdName").ne(s("Unknown")));

    Statement added = select(x("r.birdID").as("addedTo"), x("b.birdName").as("addedBirdName"),
        x("r.dateTime").as("addedDate"), x("r.transmitterChange.newTxFineTune").as("tune"))
        .from(i(template.getCouchbaseBucket().name()).as("r"))
        .join(i(template.getCouchbaseBucket().name()).as("b"))
        .onKeys(x("r.birdID"))
        .where(e1)
        .orderBy(Sort.desc(s("addedDate")));

    N1qlQuery addedQuery = N1qlQuery.simple(added);

    List<TransmitterBirdHistoryDTO2> list = new ArrayList<>();
    template.queryN1QL(addedQuery).forEach(n1qlQueryRow -> {
      TransmitterBirdHistoryDTO2 dto = new TransmitterBirdHistoryDTO2();

      String dateTimeStr = n1qlQueryRow.value().getString("addedDate");
      Date dateTime = new Date();
      if (dateTimeStr != null) {
        dateTime = commonUtils.nullSafeGetDate(dateTimeStr);
      }
      dto.setBirdId(n1qlQueryRow.value().getString("addedTo"));
      dto.setBirdName(n1qlQueryRow.value().getString("addedBirdName"));
      dto.setTune(n1qlQueryRow.value().getString("tune"));
      dto.setInstalledDate(dateTime);
      list.add(dto);
    });

    return list;
  }

  private List<TransmitterBirdHistoryDTO2> getRemovedTxList(String txID) {
    Expression e = x("r.docType").eq(s("Record"))
        .and(queryUtils.notLikeSyncId("r"))
        .and(x("r.transmitterChange.txFrom").eq(s(txID)))
        .and(x("b.birdName").ne(s("Unknown")));

    Statement removed = selectDistinct(x("r.birdID").as("removedFrom"),
        x("b.birdName").as("removedBirdName"), x("r.dateTime").as("removedDate"))
        .from(i(template.getCouchbaseBucket().name()).as("r"))
        .join(i(template.getCouchbaseBucket().name()).as("b"))
        .onKeys(x("r.birdID"))
        .where(e)
        .orderBy(Sort.desc(s("removedDate")));

    N1qlQuery removedQuery = N1qlQuery.simple(removed);

    List<TransmitterBirdHistoryDTO2> list = new ArrayList<>();
    template.queryN1QL(removedQuery).forEach(n1qlQueryRow -> {
      TransmitterBirdHistoryDTO2 dto = new TransmitterBirdHistoryDTO2();

      String dateTimeStr = n1qlQueryRow.value().getString("removedDate");
      Date dateTime = new Date();
      if (dateTimeStr != null) {
        dateTime = commonUtils.nullSafeGetDate(dateTimeStr);
      }
      dto.setBirdId(n1qlQueryRow.value().getString("removedFrom"));
      dto.setBirdName(n1qlQueryRow.value().getString("removedBirdName"));
      dto.setRemovedDate(dateTime);
      list.add(dto);
    });

    return list;
  }

  public List<RecordEntity> findWeightRecordsByBirdID(String birdID) {
    Statement statement = N1qlUtils
        .createSelectClauseForEntity(template.getCouchbaseBucket().name())
        .from(i(template.getCouchbaseBucket().name()))
        .where(
            i("docType").eq(s("Record"))
                .and(queryUtils.notLikeSyncId())
                .and(i("birdID").eq(s(birdID)))
        );
    N1qlQuery query = N1qlQuery.simple(statement);
    return template.findByN1QL(query, RecordEntity.class);
  }

  public List<RecordEntity> findEggWeights(String birdID, Date layDate, Date hatchDate) {

    Expression whereClause = i("docType").eq(s("Record"))
        .and(queryUtils.notLikeSyncId())
        .and(i("birdID").eq(s(birdID)))
        .and(i("weight").isNotMissing())
        .and(i("dateTime").gte(
            s(layDate.toInstant().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT))));

    if (hatchDate != null) {
      whereClause = whereClause.and(i("dateTime").lt(s(
          hatchDate.toInstant().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT))));
    }

    Statement statement = N1qlUtils
        .createSelectClauseForEntity(template.getCouchbaseBucket().name())
        .from(i(template.getCouchbaseBucket().name()))
        .where(whereClause)
        .orderBy(Sort.asc(i("dateTime")));

    System.out.println(statement.toString());

    N1qlQuery query = N1qlQuery.simple(statement);
    return template.findByN1QL(query, RecordEntity.class);
  }

  @Override
  public RecordEntity findRecordBySampleID(String sampleID) {
    Expression whereClause = i("docType").eq(s("Record"))
        .and(queryUtils.notLikeSyncId())
        .and(par(
            anyIn("s", i("otherSampleList")).satisfies(path("s", i("sampleID").eq(s(sampleID))))
                .or(anyIn("s", x("bloodSampleDetail.bloodSampleList"))
                    .satisfies(path("s", i("sampleID").eq(s(sampleID)))))
                .or(anyIn("s", i("swabSampleList"))
                    .satisfies(path("s", i("sampleID").eq(s(sampleID)))))
                .or(anyIn("s", i("spermSampleList"))
                    .satisfies(path("s", i("sampleID").eq(s(sampleID)))))
        ));

    Statement statement = N1qlUtils
        .createSelectClauseForEntity(template.getCouchbaseBucket().name())
        .from(i(template.getCouchbaseBucket().name()))
        .where(whereClause);

    System.out.println(statement.toString());

    N1qlQuery query = N1qlQuery.simple(statement);
    List<RecordEntity> results = template.findByN1QL(query, RecordEntity.class);
    if (results.isEmpty()) {
      return null;
    } else if (results.size() > 1) {
      throw new RuntimeException("Multiple records found associated with sample " + sampleID);
    } else {
      return results.get(0);
    }
  }

}
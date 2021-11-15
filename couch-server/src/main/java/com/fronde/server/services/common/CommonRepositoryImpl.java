package com.fronde.server.services.common;

import static com.couchbase.client.java.query.dsl.Expression.par;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.functions.StringFunctions.lower;
import static com.couchbase.client.java.query.dsl.functions.TypeFunctions.toNumber;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.dsl.Expression;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.id.IdSearchCriteria;
import com.fronde.server.services.id.IdSearchDTO;
import com.fronde.server.utils.CommonUtils;
import com.fronde.server.utils.QueryUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CommonRepositoryImpl implements CommonRepository {

  private static final Logger logger = LoggerFactory.getLogger(CommonRepositoryImpl.class);
  private final String wildcard = "%";
  @Autowired
  protected CouchbaseTemplate template;
  @Autowired
  protected QueryUtils queryUtils;
  @Autowired
  protected CommonUtils commonUtils;

  @Override
  public PagedResponse<IdSearchDTO> findIdSearchByCriteria(IdSearchCriteria criteria) {
    if (criteria.getLatestOnly() != null && criteria.getLatestOnly()) {
      return findLatestIdSearchByCriteria(criteria);
    } else {
      return findAllIdSearchByCriteria(criteria);
    }
  }

  public PagedResponse<IdSearchDTO> findLatestIdSearchByCriteria(IdSearchCriteria criteria) {
    long queryStartTime = System.currentTimeMillis();

    List<IdSearchDTO> list = null;

    List<String> birds = getBirds(criteria);

    // Exempt from notLikeSyncId() check because searching for ID's in array
    Expression we = x("meta(b).id").in(JsonArray.from(birds));

    String unionSql = null;
    unionSql = unionAll(unionSql, getTransmitterChangeSql(we, null, false));
    unionSql = unionAll(unionSql, getBandsSql(we, null, false));
    unionSql = unionAll(unionSql, getChipsSql(we, null, false));

    String fromSql = "FROM ( \n" +
        unionSql +
        ") tbc \n";

    String sql = "select " +
        "tbc.birdId, \n" +
        "tbc.birdName, \n" +
        "tbc.currentIsland as island, \n" +
        "tbc.transmitterGroup, \n" +
        "tbc.sex, \n" +
        "tbc.ls.ageClass, \n" +
        "tbc.transmitterChange.txTo, \n" +
        // TODO rename to txToName
        "tbc.txt.txId, \n" +
        "tbc.transmitterChange.txFrom, \n" +
        // TODO rename to txFromName
        "tbc.txf.txId as txFromId, \n" +
        "tbc.dateTime, \n" +
        "tbc.txt.channel, \n" +
        "tbc.txt.frequency, \n" +
        "tbc.txt.txFineTune, \n" +
        "tbc.tm.name as mortType, \n" +
        "tbc.transmitterChange.newStatus as action, \n" +
        "tbc.txt.lifeExpectancy as txRemainingLife, \n" +
        "tbc.txt.software, \n" +
        "tbc.chips.microchip as chip, \n" +
        "tbc.bands.newBandNumber as band, \n" +
        "tbc.bands.leg, \n" +
        "tbc.txt.uhfId \n" +
        fromSql +
        "ORDER BY tbc.birdName, tbc.dateTime DESC \n" +
        "LIMIT " + (criteria.getOffset() + criteria.getPageSize()) * 3;

    if (birds.isEmpty()) {
      // optimsation when no birds found for criteria
      list = Collections.emptyList();
    } else {
      N1qlQuery query = N1qlQuery.simple(sql);

      list = template.findByN1QLProjection(query, IdSearchDTO.class);

      for (IdSearchDTO dto : list) {
        String status = dto.getAction();
        if (status != null && ("Deployed old".equalsIgnoreCase(status) || "Deployed new"
            .equalsIgnoreCase(status))) {
          // only set this if the remaining life (aka life expectancy) has been set otherwise we leave it as blank
          if (dto.getTxRemainingLife() != null) {
            dto.setExpiryDate(
                CommonUtils.getExpectedExpiryDate(dto.getTxRemainingLife().longValue(),
                    dto.getDateTime()));
          }
        }
      }
    }

    if (!list.isEmpty()) {
      list = consolidateResults(list, criteria.getOffset(), criteria.getPageSize());
    }

    long queryDuration = System.currentTimeMillis() - queryStartTime;

    Integer totalCount = 0;
    long countDuration = 0;
    if (!list.isEmpty()) {
      long countStartTime = System.currentTimeMillis();
      String countSql = null;
      countSql = "select count(distinct birdId) as count " + fromSql;
      N1qlQuery countQuery = N1qlQuery.simple(countSql);
      totalCount = template.queryN1QL(countQuery).allRows().get(0).value().getInt("count");
      countDuration = System.currentTimeMillis() - countStartTime;
    }

    PagedResponse pagedResponse = new PagedResponse();
    pagedResponse.setPage(criteria.getPageNumber());
    pagedResponse.setPageSize(criteria.getPageSize());
    pagedResponse.setTotal(totalCount);
    pagedResponse.setResults(list);
    return pagedResponse;

  }

  private PagedResponse<IdSearchDTO> findAllIdSearchByCriteria(IdSearchCriteria criteria) {
    // even though we are forced to query using a string
    // we can still specify the where clause using an expression as the to string returns the sql
    // only exception is age class which must be added to the sql string directly
    Expression we = x("b.docType").eq(s("Bird"));
    we = we.and(queryUtils.notLikeSyncId("b"));

    //No Unknown bird entries
    // TODO ask if there is another way here
    we = we.and(x("b.birdName").ne("'Unknown'"));

    if (criteria.getBird() != null) {
      we = we.and(x("meta(b).id").eq(s(criteria.getBird())));
    }
    if (criteria.getAliveOnly() != null && criteria.getAliveOnly()) {
      // TODO ask about consistency of this
      we = we.and(par(x("b.alive").isMissing().or(x("b.alive"))));
    }
    if (criteria.getSex() != null) {
      // TODO ask about consistency of this
      //            if (!criteria.getSex().equalsIgnoreCase("unknown")) {
      we = we.and(x("b.sex").eq(s(criteria.getSex())));
      //            } else {
      //                we = we.and(x("b.sex").isNotValued());
      //            }
    }
    if (criteria.getIsland() != null) {
      we = we.and(lower(x("b.currentIsland")).like(lower(s(criteria.getIsland() + wildcard))));
    }
    if (criteria.getTransmitterGroup() != null) {
      we = we.and(x("b.transmitterGroup").eq(s(criteria.getTransmitterGroup())));
    }

    Expression he = null;
    if (!StringUtils.isEmpty(criteria.getAgeClass())) {
      he = and(he, lower(x("mostRecentStage[1].ageClass"))
          .like(lower(s(criteria.getAgeClass() + wildcard))));
    }

    // we have to treat the criteria for different changes separately
    boolean includeAll = true;
    Expression txe = null;
    if (!StringUtils.isEmpty(criteria.getTxId())) {
      // TODO what about the remove transmitter?
      txe = and(txe,
          par(lower(x("tbc.txt.txId")).like(lower(s(criteria.getTxId() + wildcard)))
              .or(lower(x("tbc.txf.txId")).like(lower(s(criteria.getTxId() + wildcard))))));
      includeAll = false;
    }
    if (!StringUtils.isEmpty(criteria.getMortType())) {
      txe = and(txe, x("tbc.tm.name").eq(s(criteria.getMortType())));
      includeAll = false;
    }
    if (!StringUtils.isEmpty(criteria.getChannel())) {
      // toNumber returns null if string cannot be parsed into a number
      // we don't want that to match channels that are null (precaution)
      txe = and(txe, toNumber(s(criteria.getChannel().trim())).isNotNull());
      txe = and(txe, x("tbc.txt.channel").eq(toNumber(s(criteria.getChannel().trim()))));
      includeAll = false;
    }

    if (criteria.getUhfId() != null) {
      txe = and(txe, x("tbc.txt.uhfId").eq(toNumber(x(criteria.getUhfId()))));
      includeAll = false;
    }

    Expression ce = null;
    if (!StringUtils.isEmpty(criteria.getMicrochip())) {
      // TODO why is this one like while others are equals?
      ce = and(ce,
          par(x("tbc.chips.microchip").like(s(wildcard + criteria.getMicrochip()))
              .or(x("tbc.chips.microchip").like(s(criteria.getMicrochip() + wildcard)))));
      includeAll = false;
    }
    Expression be = null;
    Expression latestOnlyBe = null;
    if (!StringUtils.isEmpty(criteria.getBand())) {
      be = and(be, x("tbc.bands.newBandNumber").like(s(criteria.getBand() + wildcard)));
      includeAll = false;
    }

    String unionSql = null;
    String whereSql = "";
    if (includeAll || txe != null) {
      unionSql = unionAll(unionSql, getTransmitterChangeSql(we, he, true));
      whereSql = orWhere(whereSql, txe);
    }
    if (includeAll || be != null) {
      unionSql = unionAll(unionSql, getBandsSql(we, he, true));
      whereSql = orWhere(whereSql, be);
    }
    if (includeAll || ce != null) {
      unionSql = unionAll(unionSql, getChipsSql(we, he, true));
      whereSql = orWhere(whereSql, ce);
    }

    String fromSql = "FROM ( \n" +
        unionSql +
        ") tbc \n" +
        whereSql;

    String sql = "select " +
        "tbc.birdId, \n" +
        "tbc.birdName, \n" +
        "tbc.currentIsland as island, \n" +
        "tbc.transmitterGroup, \n" +
        "tbc.sex, \n" +
        "tbc.ls.ageClass, \n" +
        "tbc.transmitterChange.txTo, \n" +
        // TODO rename to txToName
        "tbc.txt.txId, \n" +
        "tbc.transmitterChange.txFrom, \n" +
        // TODO rename to txFromName
        "tbc.txf.txId as txFromId, \n" +
        "tbc.dateTime, \n" +
        "tbc.txt.channel, \n" +
        "tbc.txt.frequency, \n" +
        "tbc.transmitterChange.newTxFineTune as txFineTune, \n" +
        "tbc.tm.name as mortType, \n" +
        "tbc.transmitterChange.newStatus as action, \n" +
        "tbc.txt.lifeExpectancy as txRemainingLife, \n" +
        "tbc.txt.software, \n" +
        "tbc.chips.microchip as chip, \n" +
        "tbc.bands.newBandNumber as band, \n" +
        "tbc.bands.leg, \n" +
        "tbc.txt.uhfId \n" +
        fromSql +
        "ORDER BY tbc.birdName, tbc.dateTime DESC \n" +
        "LIMIT " + criteria.getPageSize() + " \n" +
        "OFFSET " + criteria.getOffset() + " \n";

    N1qlQuery query = N1qlQuery.simple(sql);

    long queryStartTime = System.currentTimeMillis();

    List<IdSearchDTO> list = template.findByN1QLProjection(query, IdSearchDTO.class);

    boolean logSQL = false;
    for (IdSearchDTO dto : list) {
      String status = dto.getAction();
      if (status != null && ("Deployed old".equalsIgnoreCase(status) || "Deployed new"
          .equalsIgnoreCase(status))) {

        if (dto.getTxRemainingLife() != null) {
          dto.setExpiryDate(CommonUtils.getExpectedExpiryDate(dto.getTxRemainingLife().longValue(),
              dto.getDateTime()));
        } else {
          logger.warn("KD-435 TxRemainingLife is null so can't calculate the expiry date");
          logSQL = true;
        }
      }
    }

    if (logSQL) {
      logger.warn("KD-425 SQL = " + sql);
    }

    //long queryDuration = System.currentTimeMillis() - queryStartTime;

    Integer totalCount = 0;
    long countDuration = 0;
    if (!list.isEmpty()) {
      long countStartTime = System.currentTimeMillis();
      String countSql = "select count(*) as count " +
          fromSql;
      N1qlQuery countQuery = N1qlQuery.simple(countSql);
      totalCount = template.queryN1QL(countQuery).allRows().get(0).value().getInt("count");
      countDuration = System.currentTimeMillis() - countStartTime;
    }

    PagedResponse pagedResponse = new PagedResponse();
    pagedResponse.setPage(criteria.getPageNumber());
    pagedResponse.setPageSize(criteria.getPageSize());
    pagedResponse.setTotal(totalCount);
    pagedResponse.setResults(list);
    return pagedResponse;
  }

  private List<String> getBirds(IdSearchCriteria criteria) {
    // even though we are forced to query using a string
    // we can still specify the where clause using an expression as the to string returns the sql
    // only exception is age class which must be added to the sql string directly
    Expression we = x("b.docType").eq(s("Bird"));
    we = we.and(queryUtils.notLikeSyncId("b"));

    //No Unknown bird entries
    // TODO ask if there is another way here
    we = we.and(x("b.birdName").ne("'Unknown'"));

    if (criteria.getBird() != null) {
      we = we.and(x("meta(b).id").eq(s(criteria.getBird())));
    }
    if (criteria.getAliveOnly() != null && criteria.getAliveOnly()) {
      // TODO ask about consistency of this
      we = we.and(par(x("b.alive").isMissing().or(x("b.alive"))));
    }
    if (criteria.getSex() != null) {
      // TODO ask about consistency of this
      if (!criteria.getSex().equalsIgnoreCase("unknown")) {
        we = we.and(x("b.sex").eq(s(criteria.getSex())));
      } else {
        we = we.and(x("b.sex").isNotValued());
      }
    }
    if (criteria.getIsland() != null) {
      we = we.and(lower(x("b.currentIsland")).like(lower(s(criteria.getIsland() + wildcard))));
    }
    if (criteria.getTransmitterGroup() != null) {
      we = we.and(x("b.transmitterGroup").eq(s(criteria.getTransmitterGroup())));
    }

    Expression he = null;
    if (!StringUtils.isEmpty(criteria.getAgeClass())) {
      he = and(he, lower(x("mostRecentStage[1].ageClass"))
          .like(lower(s(criteria.getAgeClass() + wildcard))));
    }

    boolean includeAll = true;
    Expression txhe = null;
    if (!StringUtils.isEmpty(criteria.getTxId())) {
      // TODO what about the remove transmitter?
      txhe = and(txhe,
          par(lower(x("mostRecentTransmitterChange[1].txt.txId"))
              .like(lower(s(criteria.getTxId() + wildcard)))
              .or(lower(x(
                  "mostRecentTransmitterChange[1].txf.txId"))
                  .like(lower(s(criteria.getTxId() + wildcard))))));
      includeAll = false;
    }
    if (!StringUtils.isEmpty(criteria.getMortType())) {
      txhe = and(txhe, x("mostRecentTransmitterChange[1].tm.name").eq(s(criteria.getMortType())));
      includeAll = false;
    }
    if (!StringUtils.isEmpty(criteria.getChannel())) {
      // toNumber returns null if string cannot be parsed into a number
      // we don't want that to match channels that are null (precaution)
      txhe = and(txhe, toNumber(s(criteria.getChannel().trim())).isNotNull());
      txhe = and(txhe,
          x("mostRecentTransmitterChange[1].txt.channel")
              .eq(toNumber(s(criteria.getChannel().trim()))));
      includeAll = false;
    }

    if (criteria.getUhfId() != null) {
      txhe = and(txhe, x("mostRecentTransmitterChange[1].txt.uhfId")
          .eq(toNumber(x(criteria.getUhfId()))));
      includeAll = false;
    }

    Expression che = null;
    if (!StringUtils.isEmpty(criteria.getMicrochip())) {
      // TODO why is this one like while others are equals?
      che = and(che,
          par(x("mostRecentChips[1].chips.microchip").like(s(wildcard + criteria.getMicrochip()))
              .or(x("mostRecentChips[1].chips.microchip").like(s(
                  criteria.getMicrochip() + wildcard)))));
      includeAll = false;
    }
    Expression bhe = null;
    if (!StringUtils.isEmpty(criteria.getBand())) {
      bhe = and(bhe,
          x("mostRecentBands[1].bands.newBandNumber").like(s(criteria.getBand() + wildcard)));
      includeAll = false;
    }

    String unionOrIntersetSql = null;
    if (includeAll) {
      unionOrIntersetSql = unionAll(unionOrIntersetSql,
          getBirdIdTransmitterChangeSql(we, and(he, txhe), false));
      unionOrIntersetSql = unionAll(unionOrIntersetSql, getBirdIdBandsSql(we, and(he, bhe), false));
      unionOrIntersetSql = unionAll(unionOrIntersetSql, getBirdIdChipsSql(we, and(he, che), false));
    } else {
      if (txhe != null) {
        unionOrIntersetSql = intersetAll(unionOrIntersetSql,
            getBirdIdTransmitterChangeSql(we, and(he, txhe), false));
      }
      if (bhe != null) {
        unionOrIntersetSql = intersetAll(unionOrIntersetSql,
            getBirdIdBandsSql(we, and(he, bhe), false));
      }
      if (che != null) {
        unionOrIntersetSql = intersetAll(unionOrIntersetSql,
            getBirdIdChipsSql(we, and(he, che), false));
      }
    }

    String fromSql = "FROM ( \n" +
        unionOrIntersetSql +
        ") tbcb \n";

    String sql = "select " +
        "tbcb.id \n" +
        fromSql;

    List<String> results = new ArrayList<>();

    N1qlQuery query = N1qlQuery.simple(sql);

    template.queryN1QL(query).forEach(result -> {
      results.add(result.value().getString("id"));
    });

    return results;
  }

  private String getBirdIdTransmitterChangeSql(Expression we, Expression he, boolean all) {
    String selectSql = "select meta(b).id as id \n";

    return getTransmitterChangeSql(selectSql, we, he, all);
  }

  private String getTransmitterChangeSql(Expression we, Expression he, boolean all) {
    String selectSql = "select meta(b).id as birdId, \n" +
        "b.birdName, \n" +
        "b.currentIsland, \n" +
        "b.transmitterGroup, \n" +
        "b.sex, \n" +
        "mostRecentStage[1] as ls, \n" +
        (all ? "rtx.dateTime" : "mostRecentTransmitterChange[1].rtx.dateTime") + ", \n" +
        (all ? "rtx.transmitterChange" : "mostRecentTransmitterChange[1].rtx.transmitterChange")
        + ", \n" +
        (all ? "txt" : "mostRecentTransmitterChange[1].txt as txt") + ", \n" +
        (all ? "txf" : "mostRecentTransmitterChange[1].txf as txf") + ", \n" +
        (all ? "tm" : "mostRecentTransmitterChange[1].tm as tm") + ", \n" +
        "{} as bands, \n" +
        "{} as chips \n";

    return getTransmitterChangeSql(selectSql, we, he, all);
  }

  private String getTransmitterChangeSql(String selectSql, Expression we, Expression he,
      boolean all) {
    String bucketName = template.getCouchbaseBucket().name();

    String havingSql = he == null ? "" : "HAVING " + he + " \n";

    String fromSql = "from `" + bucketName + "` as b \n" +
        // using LEFT JOIN not possible for life stage and record but that is OK
        "JOIN `" + bucketName + "` AS ls ON KEY ls.birdID FOR b \n" +
        "JOIN `" + bucketName + "` AS rtx ON KEY rtx.birdID FOR b \n" +
        // LEFT JOIN only works if we don't put additional constraints on txt, txf, and tm without check they are valued
        "LEFT JOIN `" + bucketName + "` AS txt ON KEYS rtx.transmitterChange.txTo \n" +
        "LEFT JOIN `" + bucketName + "` AS txf ON KEYS rtx.transmitterChange.txFrom \n" +
        "LEFT JOIN `" + bucketName + "` AS tm ON KEYS txt.txMortalityId \n" +
        // b.docType = 'Bird' will come from we
        "WHERE " + we + "\n" +
        "and ls.docType = 'LifeStage' \n" +
        "and rtx.docType = 'Record' \n" +
        "and rtx.transmitterChange is valued \n" +
        "group by b" + (all ? ", rtx, txt, txf, tm" : "") + " \n" +
        "letting mostRecentStage = max([ls.modifiedTime, ls])" + (all
        ? ""
        : ", mostRecentTransmitterChange = max([rtx.dateTime, {rtx, txt, txf, tm}])") + " \n" +
        havingSql;

    String sql = selectSql +
        fromSql;

    return sql;
  }

  private String getBirdIdBandsSql(Expression we, Expression he, boolean all) {
    String selectSql = "select meta(b).id as id \n";

    return getBandsSql(selectSql, we, he, all);
  }

  private String getBandsSql(Expression we, Expression he, boolean all) {
    String selectSql = "select meta(b).id as birdId, \n" +
        "b.birdName, \n" +
        "b.currentIsland, \n" +
        "b.transmitterGroup, \n" +
        "b.sex, \n" +
        "mostRecentStage[1] as ls, \n" +
        (all ? "rb.dateTime" : "mostRecentBands[1].dateTime") + ", \n" +
        "{} as transmitterChange, \n" +
        "{} as txTo, \n" +
        "{} as txFrom, \n" +
        "{} as txm, \n" +
        (all ? "rb.bands" : "mostRecentBands[1].bands") + ", \n" +
        "{} as chips \n";

    return getBandsSql(selectSql, we, he, all);
  }

  private String getBandsSql(String selectSql, Expression we, Expression he, boolean all) {
    String bucketName = template.getCouchbaseBucket().name();

    String havingSql = he == null ? "" : "HAVING " + he + " \n";

    String fromSql = "from `" + bucketName + "` as b \n" +
        // using LEFT JOIN not possible for life stage and record but that is OK
        "JOIN `" + bucketName + "` AS ls ON KEY ls.birdID FOR b \n" +
        "JOIN `" + bucketName + "` AS rb ON KEY rb.birdID FOR b \n" +
        // b.docType = 'Bird' will come from e
        "WHERE " + we + "\n" +
        "and ls.docType = 'LifeStage' \n" +
        "and rb.docType = 'Record' \n" +
        "and rb.bands is valued \n" +
        "group by b" + (all ? ", rb" : "") + " \n" +
        "letting mostRecentStage = max([ls.modifiedTime, ls])" + (all
        ? ""
        : ", mostRecentBands = max([rb.dateTime, rb])") + " \n" +
        havingSql;

    String sql = selectSql +
        fromSql;

    return sql;
  }

  private String getBirdIdChipsSql(Expression we, Expression he, boolean all) {
    String selectSql = "select meta(b).id as id \n";

    return getChipsSql(selectSql, we, he, all);
  }

  private String getChipsSql(Expression we, Expression he, boolean all) {
    String selectSql = "select meta(b).id as birdId, \n" +
        "b.birdName, \n" +
        "b.currentIsland, \n" +
        "b.transmitterGroup, \n" +
        "b.sex, \n" +
        "mostRecentStage[1] as ls, \n" +
        (all ? "rc.dateTime" : "mostRecentChips[1].dateTime") + ", \n" +
        "{} as transmitterChange, \n" +
        "{} as txTo, \n" +
        "{} as txFrom, \n" +
        "{} as txm, \n" +
        "{} as bands, \n" +
        (all ? "rc.chips" : "mostRecentChips[1].chips") + " \n";

    return getChipsSql(selectSql, we, he, all);
  }

  private String getChipsSql(String selectSql, Expression we, Expression he, boolean all) {
    String bucketName = template.getCouchbaseBucket().name();

    String havingSql = he == null ? "" : "HAVING " + he + " \n";

    String fromSql = "from `" + bucketName + "` as b \n" +
        // using LEFT JOIN not possible for life stage and record but that is OK
        "JOIN `" + bucketName + "` AS ls ON KEY ls.birdID FOR b \n" +
        "JOIN `" + bucketName + "` AS rc ON KEY rc.birdID FOR b \n" +
        // b.docType = 'Bird' will come from e
        "WHERE " + we + "\n" +
        "and ls.docType = 'LifeStage' \n" +
        "and rc.docType = 'Record' \n" +
        "and rc.chips is valued \n" +
        "group by b" + (all ? ", rc" : "") + " \n" +
        "letting mostRecentStage = max([ls.modifiedTime, ls])" + (all
        ? ""
        : ", mostRecentChips = max([rc.dateTime, rc])") + " \n" +
        havingSql;

    String sql = selectSql +
        fromSql;

    return sql;
  }

  private List<IdSearchDTO> consolidateResults(List<IdSearchDTO> list, int offset, int pageSize) {
    IdSearchDTO last = null;
    for (ListIterator<IdSearchDTO> li = list.listIterator(); li.hasNext(); ) {
      IdSearchDTO current = li.next();
      if (last != null && last.getBirdId().equals(current.getBirdId())) {
        if (current.getBand() != null || current.getLeg() != null) {
          // this is a band so add all the band fields to the last one
          if (last.getBand() == null) {
            last.setBand(current.getBand());
          }
          if (last.getLeg() == null) {
            last.setLeg(current.getLeg());
          }
        }
        if (current.getChip() != null) {
          // this is a chip record so add all the chips fields to the last one
          if (last.getChip() == null) {
            last.setChip(current.getChip());
          }
        }
        if (current.getAction() != null || current.getTxRemainingLife() != null
            || current.getChannel() != null || current
            .getExpiryDate() != null || current.getMortType() != null
            || current.getSoftware() != null || current
            .getTxFineTune() != null || current.getTxFrom() != null || current.getTxFromId() != null
            || current
            .getTxId() != null || current.getTxTo() != null || current.getUhfId() != null) {
          if (last.getAction() == null) {
            last.setAction(current.getAction());
          }
          if (last.getTxRemainingLife() == null) {
            last.setTxRemainingLife(current.getTxRemainingLife());
          }
          if (last.getChannel() == null) {
            last.setChannel(current.getChannel());
          }
          if (last.getExpiryDate() == null) {
            last.setExpiryDate(current.getExpiryDate());
          }

          last.setFrequency(
              Optional.ofNullable(last.getFrequency()).orElseGet(current::getFrequency));

          if (last.getMortType() == null) {
            last.setMortType(current.getMortType());
          }
          if (last.getSoftware() == null) {
            last.setSoftware(current.getSoftware());
          }
          if (last.getTxFineTune() == null) {
            last.setTxFineTune(current.getTxFineTune());
          }
          if (last.getTxFrom() == null) {
            last.setTxFrom(current.getTxFrom());
          }
          if (last.getTxFromId() == null) {
            last.setTxFromId(current.getTxFromId());
          }
          if (last.getTxId() == null) {
            last.setTxId(current.getTxId());
          }
          if (last.getTxTo() == null) {
            last.setTxTo(current.getTxTo());
          }
          if (last.getUhfId() == null) {
            last.setUhfId(current.getUhfId());
          }
          // always set the date to the transmitter date
          last.setDateTime(current.getDateTime());
        }
        li.remove();
      } else {
        last = current;
      }
    }
    return list.subList(offset, Math.min(offset + pageSize, list.size()));

  }

  private Expression and(Expression e1, Expression e2) {
    if (e1 == null) {
      return e2;
    } else if (e2 == null) {
      return e1;
    } else if (e2 != null) {
      return e1.and(e2);
    } else {
      return null;
    }
  }

  private String unionAll(String unionSql, String subquerySql) {
    if (unionSql == null) {
      return "(" + subquerySql + ") \n";
    } else {
      return unionSql +
          "UNION ALL \n" +
          "(" + subquerySql + ") \n";
    }
  }

  private String intersetAll(String intersectSql, String subquerySql) {
    if (intersectSql == null) {
      return "(" + subquerySql + ") \n";
    } else {
      return intersectSql +
          "INTERSECT ALL \n" +
          "(" + subquerySql + ") \n";
    }
  }

  private String orWhere(String whereSql, Expression e) {
    if (e == null) {
      return whereSql;
    } else if (StringUtils.isEmpty(whereSql)) {
      return "WHERE " + par(e) + " \n";
    } else {
      return whereSql + " OR " + par(e) + " \n";
    }
  }

}

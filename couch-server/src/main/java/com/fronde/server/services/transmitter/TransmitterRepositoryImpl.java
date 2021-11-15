package com.fronde.server.services.transmitter;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.Select.selectDistinct;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.par;
import static com.couchbase.client.java.query.dsl.Expression.path;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.functions.AggregateFunctions.count;
import static com.couchbase.client.java.query.dsl.functions.MetaFunctions.meta;
import static com.couchbase.client.java.query.dsl.functions.StringFunctions.lower;
import static com.couchbase.client.java.query.dsl.functions.TypeFunctions.toNumber;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.Expression;
import com.couchbase.client.java.query.dsl.Sort;
import com.fronde.server.domain.TransmitterEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.options.TransmitterDTO;
import com.fronde.server.services.options.TransmitterSearchDTO;
import com.fronde.server.utils.QueryUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.repository.query.support.N1qlUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TransmitterRepositoryImpl implements TransmitterRepositoryCustom {

  @Autowired
  protected CouchbaseTemplate template;

  @Autowired
  protected QueryUtils queryUtils;

  private final String wildcard = "%";

  @Override
  public PagedResponse<TransmitterEntity> findByCriteria(TransmitterCriteria criteria) {
    Expression e = i("docType").eq(s("Transmitter"));
    e = e.and(queryUtils.notLikeSyncId());

    // throw exception for unsupported criteria
    if (criteria.getBirdID() != null) {
      throw new UnsupportedOperationException("Unsupported criteria property 'birdID'");
    }
    if (criteria.getAliveOnly() != null && criteria.getAliveOnly()) {
      throw new UnsupportedOperationException("Unsupported criteria property 'aliveOnly'");
    }
    if (!StringUtils.isEmpty(criteria.getSex())) {
      throw new UnsupportedOperationException("Unsupported criteria property 'sex'");
    }
    if (!StringUtils.isEmpty(criteria.getTransmitterGroup())) {
      throw new UnsupportedOperationException("Unsupported criteria property 'transmitterGroup'");
    }
    if (!StringUtils.isEmpty(criteria.getAgeClass())) {
      throw new UnsupportedOperationException("Unsupported criteria property 'ageClass'");
    }
    if (!StringUtils.isEmpty(criteria.getTxMortalityTypes())) {
      throw new UnsupportedOperationException("Unsupported criteria property 'txMortalityTypes'");
    }
    if (!StringUtils.isEmpty(criteria.getTxId())) {
      e = e.and(x("txId")).eq(s(criteria.getTxId()));
    }
    if (!StringUtils.isEmpty(criteria.getCurrentIsland())) {
      e = e.and(x("island").like(s(criteria.getCurrentIsland().trim() + wildcard)));
    }
    if (criteria.getSpareOnly() != null && criteria.getSpareOnly()) {
      Expression or = x("false");
      or = or.or(x("status").eq(s("New")));
      or = or.or(x("status").eq(s("Moved")));
      or = or.or(x("status").eq(s("Removed")));
      or = or.or(x("status").eq(s("Fallen off")));
      e = e.and(par(or));
    }
    if (criteria.getShowDecommissioned() == null || !criteria.getShowDecommissioned()) {
      e = e.and(x("status").ne(s("Decommissioned")));
    }

    PagedResponse<TransmitterEntity> pagedResponse = queryUtils.query(criteria, e,
        TransmitterEntity.class, Sort.asc("txId"));

    return pagedResponse;
  }

  @Override
  public PagedResponse<TransmitterSearchDTO> findSearchDtoByCriteria(TransmitterCriteria criteria) {
    Expression bucketName = i(template.getCouchbaseBucket().name());
    Expression e = x("t.docType").eq(s("Transmitter"));
    e = e.and(queryUtils.notLikeSyncId("t"));

    List<String> birds = getBirds(criteria);

    e = addCriteriaExpression(criteria, e);

    // if we are restricting the results to transmitters associated with birds if birds != null
    if (birds != null) {
      // only include transmitters that are deployed we searching for transmitters attached to specific birds
      e = e.and(par(x("t.status").eq(s("Deployed new")).or(x("t.status").eq(s("Deployed old")))));
      e = e.and(x("meta(b).id").in(JsonArray.from(birds)));
    }

    Statement statement = select(
        x("{" +
            x("\"txDocId\": meta(t).id") + ", \n" +
            x("t.txId") + ", \n" +
            x("t.status") + ", \n" +
            x("\"mortType\": txm.name") + ", \n" +
            x("t.island") + ", \n" +
            x("t.channel") + ", \n" +
            x("t.lifeExpectancy") + ", \n" +
            x("t.rigging") + ", \n" +
            x("t.uhfId") + " \n" +
            "}").as("searchDTO"),
        x("meta(b).id").as("birdID"),
        x("b.birdName"),
        x("b.alive")
    )
        .from(bucketName).as("t")
        .leftJoin(bucketName).as("txm").onKeys("t.txMortalityId")
        .leftJoin(bucketName).as("r").onKeys(x("t.lastRecordId"))
        .leftJoin(bucketName).as("b").onKeys(x("r.birdID"))
        .where(e)
        .orderBy(Sort.desc("t.txId"))
        .limit(criteria.getPageSize())
        .offset(criteria.getOffset());

    N1qlQuery query = N1qlQuery.simple(statement);

    List<TransmitterSearchPreDTO> preList = template
        .findByN1QLProjection(query, TransmitterSearchPreDTO.class);

    List<TransmitterSearchDTO> list = new ArrayList<>();
    for (TransmitterSearchPreDTO preDTO : preList) {
      preDTO.populateBirdDetails();
      list.add(preDTO.getSearchDTO());
    }

    Integer totalCount = queryUtils
        .countRows(e, "t", "txm", "t.txMortalityId", "r", "t.lastRecordId", "b", "r.birdID");
    PagedResponse pagedResponse = new PagedResponse();
    pagedResponse.setPage(criteria.getPageNumber());
    pagedResponse.setPageSize(criteria.getPageSize());
    pagedResponse.setTotal(totalCount);
    pagedResponse.setResults(list);

    return pagedResponse;
  }

  /**
   * Returns list of birds based on criteria or null if there were no criteria for the birds
   */
  private List<String> getBirds(TransmitterCriteria criteria) {
    boolean filterByBird = false;

    Expression e = x("b.docType").eq(s("Bird"));
    e = e.and(queryUtils.notLikeSyncId("b"));

    if (criteria.getBirdID() != null) {
      e = and(e, x("meta(b).id").eq(s(criteria.getBirdID())));
      filterByBird = true;
    }
    if (criteria.getAliveOnly() != null && criteria.getAliveOnly()) {
      e = and(e, x("b.alive"));
      filterByBird = true;
    }
    if (!StringUtils.isEmpty(criteria.getSex())) {
      e = and(e, x("b.sex").like(s(criteria.getSex().trim() + wildcard)));
      filterByBird = true;
    }
    if (!StringUtils.isEmpty(criteria.getTransmitterGroup())) {
      e = and(e, x("b.transmitterGroup").like(s(criteria.getTransmitterGroup().trim() + wildcard)));
      filterByBird = true;
    }
    if (!StringUtils.isEmpty(criteria.getAgeClass())) {
      filterByBird = true;
    }

    if (filterByBird) {
      return getBirds(e, criteria.getAgeClass());
    } else {
      return null;
    }
  }

  private Expression and(Expression e1, Expression e2) {
    if (e1 == null) {
      return e2;
    } else {
      return e1.and(e2);
    }
  }

  /**
   * Returns a list of ids for birds that match the given expression and ageClass.
   * <p>
   * Note that all terms in the expression must be aliased with 'b'
   */
  private List<String> getBirds(Expression e, String ageClass) {
    // TODO consider making this part of bird repository
    String bucketName = template.getCouchbaseBucket().name();

    // this is probably exempt from _sync checking since it joins on id
    String fromSql = "from `" + bucketName + "` as b \n" +
        "LEFT JOIN `" + bucketName + "` AS ls ON KEY ls.birdID FOR b \n" +
        "WHERE " + e + "\n" +
        "and ls.docType = 'LifeStage' \n" +
        "group by b \n" +
        "letting mostRecentStage = max([ls.modifiedTime, ls.ageClass]) \n" +
        (ageClass != null ? "HAVING lower(mostRecentStage[1]) like lower(\"" + ageClass + "%\")\n "
            : "");

    String sql = "select meta(b).id as id " + fromSql;

    N1qlQuery query = N1qlQuery.simple(sql);

    List<String> results = new ArrayList<>();

    template.queryN1QL(query).forEach(result -> {
      results.add(result.value().getString("id"));
    });

    return results;
  }

  @Override
  public List<String> findTransmitters(boolean fullList) {
    Expression e = i("docType").eq(s("Transmitter"));
    e = e.and(queryUtils.notLikeSyncId());

    if (!fullList) {
      e = setSpareTxExpression(e);
    }

    Statement statement = selectDistinct(i("txId"))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e);

    N1qlQuery query = N1qlQuery.simple(statement);
    List<String> transmitters = new ArrayList<>();
    template.queryN1QL(query).forEach(result -> {
      String txId = result.value().getString("txId");
      transmitters.add(txId);
    });
    return transmitters;
  }

  @Override
  public TransmitterDTO findTransmitterDTOByTxId(String txId) {
    Expression bucket = i(template.getCouchbaseBucket().name());
    Expression metaId = path(meta(bucket), "id");
    Expression e = i("docType").eq(s("Transmitter"));
    e = e.and(queryUtils.notLikeSyncId());
    e = e.and(i("txId")).eq(s(txId));

    Statement statement = selectDistinct(metaId.as("id"), i("txId"), i("channel"), i("frequency"))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e);

    N1qlQuery query = N1qlQuery.simple(statement);
    TransmitterDTO transmitter = new TransmitterDTO();

    // Only expecting find one result.
    template.queryN1QL(query).forEach(result -> {
      String currentTxId = result.value().getString("txId");
      if (txId.equalsIgnoreCase(currentTxId)) {
        transmitter.setDocId(result.value().getString("id"));
        transmitter.setTxId(currentTxId);
        transmitter.setChannel(result.value().getInt("channel"));
        transmitter.setFrequency(result.value().getNumber("frequency").floatValue());
      }
    });

    return transmitter;
  }

  private Expression setSpareTxExpression(Expression e) {
    Expression or = x("false");
    or = or.or(path(i("t"), i("status")).eq(s("New")));
    or = or.or(path(i("t"), i("status")).eq(s("Moved")));
    or = or.or(path(i("t"), i("status")).eq(s("Removed")));
    or = or.or(path(i("t"), i("status")).eq(s("Fallen off")));
    return e.and(par(or));
  }

  private Expression setOmitDecommissionedTx(Expression e) {
    return e.and(x("t.status").ne(s("Decommissioned")));
  }

  private Expression addCriteriaExpression(TransmitterCriteria criteria, Expression e) {
    if (!StringUtils.isEmpty(criteria.getTxId())) {
      e = e.and(x("t.txId")).eq(s(criteria.getTxId()));
    }

    if (!StringUtils.isEmpty(criteria.getCurrentIsland())) {
      e = e.and(x("t.island").like(s(criteria.getCurrentIsland().trim() + wildcard)));
    }

    if (!StringUtils.isEmpty(criteria.getTxMortalityTypes())) {
      e = e.and(
          lower(x("txm.name")).like(lower(s(criteria.getTxMortalityTypes().trim() + wildcard))));
    }

    if (criteria.getSpareOnly() != null && criteria.getSpareOnly()) {
      e = setSpareTxExpression(e);
    }

    if (criteria.getShowDecommissioned() == null || !criteria.getShowDecommissioned()) {
      e = setOmitDecommissionedTx(e);
    }

    if (!StringUtils.isEmpty(criteria.getRigging())) {
      e = e.and(x("t.rigging").like(s(criteria.getRigging().trim() + wildcard)));
    }

    if (!StringUtils.isEmpty(criteria.getChannel())) {
      // toNumber returns null if string cannot be parsed into a number
      // we don't want that to match channels that are null (precaution)
      e = e.and(toNumber(s(criteria.getChannel().trim())).isNotNull());
      e = e.and(x("t.channel").eq(toNumber(s(criteria.getChannel().trim()))));
    }

    if (criteria.getUhfId() != null) {
      e = e.and(x("t.uhfId").eq(toNumber(x(criteria.getUhfId()))));
    }

    return e;
  }

  @Override
  public boolean hasReferences(String transmitterID) {
    Expression bucket = i(template.getCouchbaseBucket().name());
    Statement statement = select(count(path(meta(""), "id")).gt(0).as("hasReferences"))
        .from(bucket).where(i("docType").eq(s("Record")).and(
            par(path("transmitterChange", i("txId")).eq(s(transmitterID))
                .or(path("transmitterChange", i("txFrom")).eq(s(transmitterID)))
                .or(path("transmitterChange", i("txTo")).eq(s(transmitterID))))));

    N1qlQuery query = N1qlQuery.simple(statement);

    return template.queryN1QL(query).allRows().get(0).value().getBoolean("hasReferences");
  }

  @Override
  public List<String> findTxIds() {
    // TODO
    return Collections.emptyList();
  }

  @Override
  public TransmitterEntity findCurrentTransmitterByBirdID(String birdID) {
    Expression e = path("t", i("docType")).eq(s("Transmitter"));
    e = e.and(queryUtils.notLikeSyncId());

    e = e.and(path("t", i("status").in(JsonArray.from("Deployed new", "Deployed old"))));
    e = e.and(path("r", i("birdID").eq(s(birdID))));

    Statement statement = N1qlUtils.createSelectClauseForEntity("t")
        .from(i(template.getCouchbaseBucket().name())).as("t")
        .join(i(template.getCouchbaseBucket().name())).as("r").onKeys(path("t", i("lastRecordId")))
        .where(e);

    N1qlQuery query = N1qlQuery.simple(statement);

    List<TransmitterEntity> list = template.findByN1QL(query, TransmitterEntity.class);

    if (list.isEmpty()) {
      return null;
    } else {
      return list.get(0);
    }
  }

  @Override
  public boolean existsByTxIdExcluding(String txId, String excludingTransmitterID) {
    Expression e = x("docType").eq(s("Transmitter"));
    e = e.and(queryUtils.notLikeSyncId());
    e = e.and(lower(i("txId")).eq(lower(s(txId))));
    e = e.and(path(meta(""), i("id")).ne(s(excludingTransmitterID)));

    Statement s = select(path(meta(""), i("id")))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e);

    N1qlQuery query = N1qlQuery.simple(s);

    List<N1qlQueryRow> rows = template.queryN1QL(query).allRows();
    return !rows.isEmpty();
  }

}
package com.fronde.server.services.sample;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.Select.selectDistinct;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.par;
import static com.couchbase.client.java.query.dsl.Expression.path;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.functions.AggregateFunctions.count;
import static com.couchbase.client.java.query.dsl.functions.StringFunctions.lower;
import static com.couchbase.client.java.query.dsl.functions.TypeFunctions.toNumber;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.Expression;
import com.couchbase.client.java.query.dsl.Sort;
import com.fronde.server.domain.SampleEntity;
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
public class SampleRepositoryImpl implements SampleRepositoryCustom {

  @Autowired
  protected CouchbaseTemplate template;

  @Autowired
  protected QueryUtils queryUtils;

  private final String wildcard = "%";

  public PagedResponse<SampleEntity> findByCriteria(SampleCriteria criteria) {
    throw new UnsupportedOperationException("Use findSearchDTOByCriteria instead");
  }

  @Override
  public PagedResponse<SampleSearchDTO> findSearchDTOByCriteria(SampleCriteria criteria) {
    Expression bucketName = i(template.getCouchbaseBucket().name());
    Expression e = x("smpl.docType").eq(s("Sample"));
    e = e.and(queryUtils.notLikeSyncId("smpl"));

    e = addCriteriaExpression(criteria, e);

    Statement statement = select(
        x("meta(smpl).id as sampleID"),
        x("smpl.sampleName"),
        x("meta(b).id as birdID"),
        x("b.birdName"),
        x("smpl.sampleCategory"),
        x("smpl.sampleType"),
        x("smpl.container"),
        x("smpl.sampleTakenBy as sampleTakenByID"),
        x("p.name as sampleTakenByName"),
        x("smpl.collectionDate"),
        x("smpl.collectionIsland"),
        x("smpl.collectionLocationID"),
        x("l.locationName as collectionLocationName"),
        x("(smpl.haematologyTestList is valued and smpl.haematologyTestList <> []) as hasHaematologyTests"),
        x("(smpl.chemistryAssayList is valued and smpl.chemistryAssayList <> []) as hasChemistryAssays"),
        x("(smpl.microbiologyAndParasitologyTestList is valued and smpl.microbiologyAndParasitologyTestList <> []) as hasMicrobiologyAndParasitologyTests"),
        x("(smpl.spermMeasureList is valued and smpl.spermMeasureList <> []) as hasSpermMeasures")
    )
        .from(bucketName).as("smpl")
        .leftJoin(bucketName).as("b").onKeys(x("smpl.birdID"))
        .leftJoin(bucketName).as("l").onKeys(x("smpl.collectionLocationID"))
        .leftJoin(bucketName).as("p").onKeys(x("smpl.sampleTakenBy"))
        .where(e)
        .orderBy(Sort.desc("smpl.collectionDate"), Sort.asc("b.birdName"),
            Sort.asc("smpl.sampleCategory"), Sort.asc("smpl.sampleType"),
            Sort.asc("smpl.sampleName"))
        .limit(criteria.getPageSize())
        .offset(criteria.getOffset());

    N1qlQuery query = N1qlQuery.simple(statement);

    System.out.println("findSearchDTOByCriteria(): " + query);

    List<SampleSearchDTO> list = template.findByN1QLProjection(query, SampleSearchDTO.class);

    Integer totalCount = queryUtils.countRows(e, "smpl");
    PagedResponse pagedResponse = new PagedResponse();
    pagedResponse.setPage(criteria.getPageNumber());
    pagedResponse.setPageSize(criteria.getPageSize());
    pagedResponse.setTotal(totalCount);
    pagedResponse.setResults(list);

    return pagedResponse;
  }

  private Expression addCriteriaExpression(SampleCriteria criteria, Expression e) {
    if (criteria.getBirdIDs() != null && criteria.getBirdIDs().size() > 0) {
      e = e.and(x("smpl.birdID").in(JsonArray.from(criteria.getBirdIDs())));
    }
    // TODO incorporate age class
    if (criteria.getSex() != null) {
      e = e.and(lower(x("b.sex")).like(lower(s(criteria.getSex() + wildcard))));
    }
    if (criteria.getCollectionIsland() != null) {
      e = e.and(lower(x("smpl.collectionIsland"))
          .like(lower(s(criteria.getCollectionIsland() + wildcard))));
    }
    if (criteria.getCollectionLocationID() != null) {
      e = e.and(x("smpl.collectionLocationID").eq(s(criteria.getCollectionLocationID())));
    }
    if (criteria.getSampleTakenBy() != null) {
      e = e.and(x("smpl.sampleTakenBy").eq(s(criteria.getSampleTakenBy())));
    }
    if (criteria.getSampleName() != null) {
      e = e.and(lower(x("smpl.sampleName")).like(lower(s(criteria.getSampleName() + wildcard))));
    }
    if (criteria.getSampleCategory() != null) {
      e = e.and(
          lower(x("smpl.sampleCategory")).like(lower(s(criteria.getSampleCategory() + wildcard))));
    }
    if (criteria.getSampleType() != null) {
      e = e.and(lower(x("smpl.sampleType")).like(lower(s(criteria.getSampleType() + wildcard))));
    }
    if (criteria.getContainer() != null) {
      e = e.and(lower(x("smpl.container")).like(lower(s(criteria.getContainer() + wildcard))));
    }
    if (criteria.getFromDate() != null) {
      e = e.and(x("smpl.collectionDate").gt(s(
          criteria.getFromDate().toInstant().atZone(ZoneOffset.UTC)
              .format(DateTimeFormatter.ISO_INSTANT))));
    }
    if (criteria.getToDate() != null) {
      e = e.and(x("smpl.collectionDate").lt(s(
          criteria.getToDate().toInstant().atZone(ZoneOffset.UTC)
              .format(DateTimeFormatter.ISO_INSTANT))));
    }
    if (criteria.getShowResultsEntered() == null || !criteria.getShowResultsEntered()) {
      e = e.and(
          x("not ((smpl.haematologyTestList is valued and smpl.haematologyTestList <> []) or (smpl.chemistryAssayList is valued and smpl.chemistryAssayList <> []) or (smpl.microbiologyAndParasitologyTestList is valued and smpl.microbiologyAndParasitologyTestList <> []) or (smpl.spermMeasureList is valued and smpl.spermMeasureList <> []))"));
    }
    if (criteria.getShowResultsNotEntered() == null || !criteria.getShowResultsNotEntered()) {
      e = e.and(
          x("((smpl.haematologyTestList is valued and smpl.haematologyTestList <> []) or (smpl.chemistryAssayList is valued and smpl.chemistryAssayList <> []) or (smpl.microbiologyAndParasitologyTestList is valued and smpl.microbiologyAndParasitologyTestList <> []) or (smpl.spermMeasureList is valued and smpl.spermMeasureList <> []))"));
    }
    if (criteria.getShowArchived() == null || !criteria.getShowArchived()) {
      e = e.and(par(x("smpl.archived is valued").and(x("smpl.archived"))).not());
    }
    if (criteria.getShowNotArchived() == null || !criteria.getShowNotArchived()) {
      e = e.and(x("smpl.archived is valued")).and(x("smpl.archived"));
    }

    return e;
  }

  @Override
  public List<String> findOtherSampleTypes() {
    Expression e = x("docType").eq(s("Sample"));
    e = e.and(queryUtils.notLikeSyncId());

    Statement statement = selectDistinct(path(i("otherDetail"), i("type")))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e)
        .orderBy(Sort.asc("type"));

    N1qlQuery query = N1qlQuery.simple(statement);

    List<String> list = new ArrayList<>();
    template.queryN1QL(query).forEach(n1qlQueryRow -> {
      list.add(n1qlQueryRow.value().getString("type"));
    });

    return list;
  }

  @Override
  public List<String> findSpermDiluents() {
    Expression e = x("docType").eq(s("Sample"));
    e = e.and(queryUtils.notLikeSyncId());

    Statement statement = selectDistinct(path(i("spermDetail"), i("diluent")))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e)
        .orderBy(Sort.asc("diluent"));

    N1qlQuery query = N1qlQuery.simple(statement);

    List<String> list = new ArrayList<>();
    template.queryN1QL(query).forEach(n1qlQueryRow -> {
      list.add(n1qlQueryRow.value().getString("diluent"));
    });

    return list;
  }

  @Override
  public List<String> findSampleCategories() {
    Expression e = x("docType").eq(s("Sample"));
    e = e.and(queryUtils.notLikeSyncId());

    Statement statement = selectDistinct(i("sampleCategory"))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e)
        .orderBy(Sort.asc("sampleCategory"));

    N1qlQuery query = N1qlQuery.simple(statement);

    List<String> list = new ArrayList<>();
    template.queryN1QL(query).forEach(n1qlQueryRow -> {
      list.add(n1qlQueryRow.value().getString("sampleCategory"));
    });

    return list;
  }

  @Override
  public List<String> findSampleTypes() {
    Expression e = x("docType").eq(s("Sample"));
    e = e.and(queryUtils.notLikeSyncId());

    Statement statement = selectDistinct(i("sampleType"))
        .from(i(template.getCouchbaseBucket().name()))
        .where(e)
        .orderBy(Sort.asc("sampleType"));

    N1qlQuery query = N1qlQuery.simple(statement);

    List<String> list = new ArrayList<>();
    template.queryN1QL(query).forEach(n1qlQueryRow -> {
      list.add(n1qlQueryRow.value().getString("sampleType"));
    });

    return list;
  }

  @Override
  public List<TestStatsDTO> getHaematologyTestsStats(String type) {
    String statement =
        "select test, max(mean) mean, power(sum(sdDividend)/max(n), 0.5) as standardDeviation, min(result) minimum, max(result) maximum, max(n) n\n"
            +
            "from (\n" +
            "select test, result, power(result - mean, 2) sdDividend, n, mean\n" +
            "from (\n" +
            "select ht.test,\n" +
            "tonumber(ht.result) as result, \n" +
            "first stat.mean for stat in stats when stat.test = ht.test end as mean,\n" +
            "first stat.n for stat in stats when stat.test = ht.test end as n\n" +
            "from `kakapo-bird` smpl unnest haematologyTestList ht \n" +
            "let stats = (\n" +
            "select test, avg(result) as mean, count(result) as n\n" +
            "from (select ht.test, tonumber(ht.result) as result from `kakapo-bird` smpl unnest haematologyTestList ht\n"
            +
            "where smpl.docType = 'Sample'\n" +
            "and smpl.sampleCategory = 'Blood'\n" +
            "and smpl.sampleType = $type\n" +
            "and ht is not missing\n" +
            "and (ht.statsExclude is missing or not ht.statsExclude)\n" +
            "and meta(smpl).id not like \"\\\\_sync%\"\n" +
            ") ht2\n" +
            "group by test)\n" +
            "where smpl.docType = 'Sample'\n" +
            "and smpl.sampleCategory = 'Blood'\n" +
            "and smpl.sampleType = $type\n" +
            "and ht is not missing\n" +
            "and (ht.statsExclude is missing or not ht.statsExclude)\n" +
            "and meta(smpl).id not like \"\\\\_sync%\"\n" +
            ") ht3\n" +
            ") ht4\n" +
            "group by test\n" +
            "order by test";
    N1qlQuery query = N1qlQuery.parameterized(statement, JsonObject.create().put("type", type));

    List<TestStatsDTO> list = template.findByN1QLProjection(query, TestStatsDTO.class);

    return list;
  }

  @Override
  public List<ResultRankDTO> getHaematologyTestsRanks(String type, List<ResultDTO> results) {
    // couchbase's optimiser doesn't make a good decision when the results is empty so we coded specifically for it
    if (results.isEmpty()) {
      return Collections.emptyList();
    }

    Expression or = x("false");
    for (ResultDTO result : results) {
      Expression and = x("true");
      and = and.and(path(i("ht"), i("test")).eq(s(result.getTest())));
      and = and.and(toNumber(path(i("ht"), i("result"))).lt(result.getResult()));
      and = and.and(par(path(i("ht"), i("statsExclude")).isMissing()
          .or(path(i("ht"), i("statsExclude")).not())));
      or = or.or(par(and));
    }

    Expression e = path(i("smpl"), i("docType")).eq(s("Sample"));
    e = e.and(queryUtils.notLikeSyncId("smpl"));
    e = e.and(path(i("smpl"), i("sampleCategory")).eq(s("Blood")));
    e = e.and(path(i("smpl"), i("sampleType")).eq(s(type)));
    e = e.and(i("ht").isNotMissing());
    e = e.and(par(or));

    Statement statement = select(path(i("ht"), i("test")),
        count(path(i("ht"), i("result"))).add(1).as("rank"))
        .from(i(template.getCouchbaseBucket().name())).as("smpl")
        .unnest(path(i("smpl"), i("haematologyTestList"))).as("ht")
        .where(e)
        .groupBy(path(i("ht"), i("test")));

    N1qlQuery query = N1qlQuery.simple(statement);

    List<ResultRankDTO> ranks = new ArrayList<>();

    ranks.addAll(template.findByN1QLProjection(query, ResultRankDTO.class));

    // set any absent rank to being rank 1 (as nothing is less than it)
    for (ResultDTO result : results) {
      boolean found = false;
      for (ResultRankDTO rank : ranks) {
        found = rank.getTest().equals(result.getTest());
        if (found) {
          break;
        }
      }
      if (!found) {
        // add with rank of 1 (as nothing was found less than the result)
        ResultRankDTO rank = new ResultRankDTO();
        rank.setTest(result.getTest());
        rank.setRank(1);
        ranks.add(rank);
      }
    }

    return ranks;
  }

  @Override
  public List<TestStatsDTO> getChemistryAssaysStats(String type) {
    String statement =
        "select test, max(mean) mean, power(sum(sdDividend)/max(n), 0.5) as standardDeviation, min(result) minimum, max(result) maximum, max(n) n\n"
            +
            "from (\n" +
            "select test, result, power(result - mean, 2) sdDividend, n, mean\n" +
            "from (\n" +
            "select ca.chemistryAssay as test,\n" +
            "tonumber(ca.result) as result, \n" +
            "first stat.mean for stat in stats when stat.test = ca.chemistryAssay end as mean,\n" +
            "first stat.n for stat in stats when stat.test = ca.chemistryAssay end as n\n" +
            "from `kakapo-bird` smpl unnest chemistryAssayList ca \n" +
            "let stats = (\n" +
            "select test, avg(result) as mean, count(result) as n\n" +
            "from (select ca.chemistryAssay as test, tonumber(ca.result) as result from `kakapo-bird` smpl unnest chemistryAssayList ca\n"
            +
            "where smpl.docType = 'Sample'\n" +
            "and smpl.sampleCategory = 'Blood'\n" +
            "and smpl.sampleType = $type\n" +
            "and ca is not missing\n" +
            "and (ca.statsExclude is missing or not ca.statsExclude)\n" +
            "and meta(smpl).id not like \"\\\\_sync%\"\n" +
            ") ca2\n" +
            "group by test)\n" +
            "where smpl.docType = 'Sample'\n" +
            "and smpl.sampleCategory = 'Blood'\n" +
            "and smpl.sampleType = $type\n" +
            "and ca is not missing\n" +
            "and (ca.statsExclude is missing or not ca.statsExclude)\n" +
            "and meta(smpl).id not like \"\\\\_sync%\"\n" +
            ") ca3\n" +
            ") ca4\n" +
            "group by test\n" +
            "order by test";
    N1qlQuery query = N1qlQuery.parameterized(statement, JsonObject.create().put("type", type));

    List<TestStatsDTO> list = template.findByN1QLProjection(query, TestStatsDTO.class);

    return list;
  }

  @Override
  public List<ResultRankDTO> getChemistryAssaysRanks(String type, List<ResultDTO> results) {
    // couchbase's optimiser doesn't make a good decision when the results is empty so we coded specifically for it
    if (results.isEmpty()) {
      return Collections.emptyList();
    }

    Expression or = x("false");
    for (ResultDTO result : results) {
      Expression and = x("true");
      and = and.and(path(i("ca"), i("chemistryAssay")).eq(s(result.getTest())));
      and = and.and(toNumber(path(i("ca"), i("result"))).lt(result.getResult()));
      and = and.and(par(path(i("ca"), i("statsExclude")).isMissing()
          .or(path(i("ca"), i("statsExclude")).not())));
      or = or.or(par(and));
    }

    Expression e = path(i("smpl"), i("docType")).eq(s("Sample"));
    e = e.and(queryUtils.notLikeSyncId("smpl"));
    e = e.and(path(i("smpl"), i("sampleCategory")).eq(s("Blood")));
    e = e.and(path(i("smpl"), i("sampleType")).eq(s(type)));
    e = e.and(i("ca").isNotMissing());
    e = e.and(par(or));

    Statement statement = select(path(i("ca"), i("chemistryAssay")).as("test"),
        count(path(i("ca"), i("result"))).add(1).as("rank"))
        .from(i(template.getCouchbaseBucket().name())).as("smpl")
        .unnest(path(i("smpl"), i("chemistryAssayList"))).as("ca")
        .where(e)
        .groupBy(path(i("ca"), i("chemistryAssay")));

    N1qlQuery query = N1qlQuery.simple(statement);

    List<ResultRankDTO> ranks = new ArrayList<>();

    ranks.addAll(template.findByN1QLProjection(query, ResultRankDTO.class));

    // set any absent rank to being rank 1 (as nothing is less than it)
    for (ResultDTO result : results) {
      boolean found = false;
      for (ResultRankDTO rank : ranks) {
        found = rank.getTest().equals(result.getTest());
        if (found) {
          break;
        }
      }
      if (!found) {
        // add with rank of 1 (as nothing was found less than the result)
        ResultRankDTO rank = new ResultRankDTO();
        rank.setTest(result.getTest());
        rank.setRank(1);
        ranks.add(rank);
      }
    }

    return ranks;
  }
}

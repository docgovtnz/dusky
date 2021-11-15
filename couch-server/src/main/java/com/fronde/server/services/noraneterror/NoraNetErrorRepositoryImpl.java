package com.fronde.server.services.noraneterror;

import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.dsl.Expression;
import com.couchbase.client.java.query.dsl.Sort;
import com.fronde.server.domain.NoraNetErrorEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.domain.response.Response;
import com.fronde.server.utils.QueryUtils;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Component;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.s;

@Component
public class NoraNetErrorRepositoryImpl implements NoraNetErrorRepositoryCustom {

  protected final CouchbaseTemplate template;

  protected final QueryUtils queryUtils;

  public NoraNetErrorRepositoryImpl(CouchbaseTemplate template, QueryUtils queryUtils) {
    this.template = template;
    this.queryUtils = queryUtils;
  }

  private final String wildcard = "%";
  private final String separator = "\\\\_";

  public PagedResponse<NoraNetErrorEntity> findByCriteria(NoraNetErrorCriteria criteria) {
    Expression e = i("docType").eq(s("NoraNetError"));
    e = e.and(queryUtils.notLikeSyncId());

    if (criteria.getIslandNo() != null) {
      e = e.and("fileName")
          .like(s(wildcard + separator + criteria.getIslandNo() + separator + wildcard));
    }

    if (criteria.getFileType() != null) {
      e = e.and("fileName").like(s(criteria.getFileType() + separator + wildcard));
    }

    if (criteria.getFromDateProcessed() != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(criteria.getFromDateProcessed());
      cal.add(Calendar.HOUR, -12);
      criteria.setFromDateProcessed(cal.getTime());
      e = e.and(i("dateProcessed")
          .gte(s(criteria.getFromDateProcessed().toInstant().atZone(ZoneOffset.UTC)
              .format(DateTimeFormatter.ISO_INSTANT))));
    }

    if (criteria.getToDateProcessed() != null) {
      e = e.and(i("dateProcessed")
          .lte(s(criteria.getToDateProcessed().toInstant().atZone(ZoneOffset.UTC)
              .format(DateTimeFormatter.ISO_INSTANT))));
    }

    return queryUtils.query(criteria, e, NoraNetErrorEntity.class, Sort.desc("dateProcessed"));
  }

  @Override
  public Response<List<NoraNetErrorReportDTO>> findSearchDTOByCriteria(
      NoraNetErrorCriteria criteria) {

    Response<List<NoraNetErrorReportDTO>> response = new Response<>();

    Expression e = i("docType").eq(s("NoraNetError"));
    e = e.and(queryUtils.notLikeSyncId());

    if (criteria.getFromDateProcessed() != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(criteria.getFromDateProcessed());
      cal.add(Calendar.HOUR, -12);
      criteria.setFromDateProcessed(cal.getTime());
      e = e.and(i("dateProcessed")
          .gte(s(criteria.getFromDateProcessed().toInstant().atZone(ZoneOffset.UTC)
              .format(DateTimeFormatter.ISO_INSTANT))));
    }

    if (criteria.getToDateProcessed() != null) {
      e = e.and(i("dateProcessed")
          .lte(s(criteria.getToDateProcessed().toInstant().atZone(ZoneOffset.UTC)
              .format(DateTimeFormatter.ISO_INSTANT))));
    }

    String fromClause = " `kakapo-bird` nn";

    String queryString = "SELECT " +
        "meta(nn).id AS id, " +
        "nn.dateProcessed AS dateProcessed, " +
        "nn.fileName AS fileName, " +
        "nn.fileData AS fileData, " +
        "nn.message AS message, " +
        "nn.dataImported AS dataImported, " +
        "nn.actioned AS actioned " +
        "\nFROM " +
        fromClause +
        "\nWHERE " + e.toString() +
        "\nORDER BY dateProcessed DESC, fileName " +
        "\nLIMIT " + criteria.getPageSize() +
        "\nOFFSET " + criteria.getOffset();

    N1qlQuery query = N1qlQuery.simple(queryString);
    List<NoraNetErrorReportDTO> resultList = template.findByN1QLProjection(query, NoraNetErrorReportDTO.class);

    response.setModel(resultList);

    return response;
  }
}

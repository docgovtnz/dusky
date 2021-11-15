package com.fronde.server.services.credential;

import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;

import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.Expression;
import com.fronde.server.domain.CredentialEntity;
import com.fronde.server.utils.QueryUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.repository.query.support.N1qlUtils;
import org.springframework.stereotype.Component;

@Component
public class CredentialRepositoryImpl implements CredentialRepositoryCustom {

  @Autowired
  protected CouchbaseTemplate template;

  @Autowired
  protected QueryUtils queryUtils;

  private Date nullDate;

  public Date getNullDate() {
    if (nullDate == null) {
      try {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd mm:HH:ss");
        nullDate = dateFormat.parse("1980-01-01 00:00:00");
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    }
    return nullDate;
  }

  @Override
  public List<CredentialEntity> findAllByPersonId(String personId) {
    Expression e = i("docType").eq(s("Credential"));
    e = e.and(queryUtils.notLikeSyncId());

    e = e.and(x("personId").eq(s(personId)));

    Statement statement = N1qlUtils
        .createSelectClauseForEntity(template.getCouchbaseBucket().name())
        .from(i(template.getCouchbaseBucket().name()))
        .where(e);

    N1qlQuery query = N1qlQuery.simple(statement);
    List<CredentialEntity> list = template.findByN1QL(query, CredentialEntity.class);
    return list;
  }

  @Override
  public CredentialEntity findOneByPersonId(String personId) {

    List<CredentialEntity> list = findAllByPersonId(personId);

    CredentialEntity entity = null;
    if (list != null && list.size() > 0) {
      list.sort((o1, o2) -> {
        Date o1Time = o1.getModifiedTime() != null ? o1.getModifiedTime() : getNullDate();
        Date o2Time = o2.getModifiedTime() != null ? o2.getModifiedTime() : getNullDate();
        return o1Time.compareTo(o2Time);
      });

      entity = list.get(list.size() - 1);
    }

    return entity;
  }
}

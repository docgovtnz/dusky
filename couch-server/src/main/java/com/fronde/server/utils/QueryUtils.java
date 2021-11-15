package com.fronde.server.utils;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.Select.selectRaw;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.path;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.sub;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.functions.AggregateFunctions.count;
import static com.couchbase.client.java.query.dsl.functions.MetaFunctions.meta;

import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.Expression;
import com.couchbase.client.java.query.dsl.Sort;
import com.fronde.server.domain.criteria.PagedCriteria;
import com.fronde.server.domain.response.PagedResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.repository.query.CountFragment;
import org.springframework.data.couchbase.repository.query.support.N1qlUtils;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class QueryUtils {

  @Autowired
  protected CouchbaseTemplate template;

  /**
   * Creates a sub query for use with the in operator.
   *
   * @param key     - the key that contains the values to be compared using the in operator.
   * @param docType - the docType of the documents being searched by the sub query
   * @param where   - expression where all references to the sub query from are using the alias
   *                "sub"
   * @return
   */
  public Expression subquery(String key, String docType, Expression where) {
    return subquery(i(key), docType, where);
  }

  /**
   * Creates a sub query for use with the in operator.
   *
   * @param key     - the key that contains the values to be compared using the in operator.
   * @param docType - the docType of the documents being searched by the sub query
   * @param where   - expression where all references to the sub query from are using the alias "s"
   * @return
   */
  public Expression subquery(Expression key, String docType, Expression where) {
    return sub(selectRaw(key).from(i(template.getCouchbaseBucket().name()).as("sub"))
        .where(x("sub.docType").eq(s(docType)).and(where)));
  }

  public <T> PagedResponse<T> query(PagedCriteria criteria, Expression e, Class<T> objectClass,
      Sort... s) {

    Statement statement = N1qlUtils
        .createSelectClauseForEntity(template.getCouchbaseBucket().name())
        .from(i(template.getCouchbaseBucket().name()))
        .where(e)
        .orderBy(s)
        .limit(criteria.getPageSize())
        .offset(criteria.getOffset());

    N1qlQuery query = N1qlQuery.simple(statement);
    List<T> list = template.findByN1QL(query, objectClass);

    Integer totalCount = countRows(e, null);

    PagedResponse pagedResponse = new PagedResponse();
    pagedResponse.setPage(criteria.getPageNumber());
    pagedResponse.setPageSize(criteria.getPageSize());
    pagedResponse.setTotal(totalCount);
    pagedResponse.setResults(list);

    return pagedResponse;
  }

  public Integer countRows(Expression e) {
    return countRows(e, null);
  }

  public Integer countRows(Expression e, String alias) {
    Statement countStatement;
    if (alias != null) {
      countStatement = select(count("*").as(CountFragment.COUNT_ALIAS))
          .from(i(template.getCouchbaseBucket().name())).as(alias)
          .where(e);
    } else {
      countStatement = select(count("*").as(CountFragment.COUNT_ALIAS))
          .from(i(template.getCouchbaseBucket().name()))
          .where(e);
    }

    N1qlQuery countQuery = N1qlQuery.simple(countStatement);
    N1qlQueryResult queryRows = template.queryN1QL(countQuery);
    N1qlQueryRow firstRow = queryRows.allRows().get(0);
    Integer totalCount = firstRow.value().getInt("count");
    return totalCount;
  }

  public Integer countRows(Expression e, String alias, String jOne, String jOneKey, String jTwo,
      String jTwoKey, String jThree, String jThreeKey) {
    Expression bucketName = i(template.getCouchbaseBucket().name());
    Statement countStatement = select(count("*").as(CountFragment.COUNT_ALIAS))
        .from(i(template.getCouchbaseBucket().name())).as(alias)
        .leftJoin(bucketName).as(jOne).onKeys(jOneKey)
        .leftJoin(bucketName).as(jTwo).onKeys(jTwoKey)
        .leftJoin(bucketName).as(jThree).onKeys(jThreeKey)
        .where(e);

    N1qlQuery countQuery = N1qlQuery.simple(countStatement);
    N1qlQueryResult queryRows = template.queryN1QL(countQuery);
    N1qlQueryRow firstRow = queryRows.allRows().get(0);
    Integer totalCount = firstRow.value().getInt("count");
    return totalCount;
  }

  /**
   * These following methods are quite important. Every query in the application must exclude
   * "_sync%" because it filters out data replication conflicts that will otherwise be included.
   */
  public Expression notLikeSyncId() {
    return notLikeSyncId("");
  }

  /**
   * If this method is ever refactored to include other prefixes you'll also need to scan the code
   * for usages of "_sync" because there are places where the expression was included in strings
   * instead of calling this method.
   */
  public Expression notLikeSyncId(String pathAlias) {
    return path(meta(pathAlias), "id").notLike(s("\\\\_sync%"));
  }
}

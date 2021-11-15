package com.fronde.server.services.optionlist;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.Select.selectRaw;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.sub;
import static com.couchbase.client.java.query.dsl.functions.AggregateFunctions.arrayAgg;

import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.Expression;
import com.fronde.server.domain.OptionListEntity;
import com.fronde.server.domain.OptionListItemEntity;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.validation.ValidationMessage;
import com.fronde.server.utils.QueryUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Component;
import com.couchbase.client.java.document.json.JsonObject;

@Component
public class OptionListRepositoryImpl implements OptionListRepositoryCustom {

  @Autowired
  protected CouchbaseTemplate template;

  @Autowired
  protected QueryUtils queryUtils;

  @Override
  public Map<String, String> getOptionListTitles() {
    Expression bucket = i(template.getCouchbaseBucket().name());

    Statement statement = select("titleItemList")
        .from(bucket)
        .where(i("docType").eq(s("OptionList")).and(i("name").eq(s("ListTitles"))));
    N1qlQuery query = N1qlQuery.simple(statement);
    List<N1qlQueryRow> rows = template.queryN1QL(query).allRows();
    if (rows.isEmpty()) {
      return Collections.emptyMap();
    } else {
      N1qlQueryRow row = rows.get(0);
      JsonObject til = row.value();
      //Map<String, Object> map = row.value().toMap();
      // we have to create a second map to get around Java's type checking
      Map<String, String> result = new HashMap<>();

      for (Object nt1: til.getArray("titleItemList")) {
        result.put(((JsonObject) nt1).get("name").toString(), ((JsonObject) nt1).get("title").toString());
      }
      return result;
    }
  }

  @Override
  public Map<String, List<String>> getOptions() {
    Expression bucket = i(template.getCouchbaseBucket().name());

    // inspired from https://forums.couchbase.com/t/concat-array-of-objects-into-a-single-object/19406
    // the effect of the statement below is to convert the list of ObjectListEntity documents in the map keyed by the name of the option list.
    // e.g. converts [
    //    {name: 'ol1', optionListItemList: [{text: 'option1'}, {text: 'option2'}]},
    //    {name: 'ol2', optionListItemList: [{text: 'option1'}, {text: 'option2'}]}
    // ]
    // into
    // {'ol1': ['option1', 'option2'], 'ol2': ['option1', 'option2']}
    Statement statement = selectRaw("OBJECT ol.name:ol.list FOR ol IN ols.olArray END ")
        .from(sub(
            select(arrayAgg("{name, 'list': ARRAY i.text FOR i IN optionListItemList END}")
                .as("olArray"))
                .from(bucket)
                .where(i("docType").eq(s("OptionList")).and(queryUtils.notLikeSyncId()))
            ).as("ols")
        );

    N1qlQuery query = N1qlQuery.simple(statement);
    List<N1qlQueryRow> rows = template.queryN1QL(query).allRows();
    if (rows.isEmpty()) {
      return Collections.emptyMap();
    } else {
      N1qlQueryRow row = rows.get(0);
      Map<String, Object> map = row.value().toMap();
      // we have to create a second map to get around Java's type checking
      Map<String, List<String>> result = new HashMap<>();
      for (String key : map.keySet()) {
        result.put(key, (List<String>) map.get(key));
      }
      return result;
    }
  }

  @Override
  public List<String> findDisplayOptionsByCriteria(OptionListCriteria criteria) {

    if (criteria.getOptionListName()==null || criteria.getOptionListText()==null) {
      return null;
    }

     String queryString = "SELECT " +
        "displayItem.name AS name\n" +
        "FROM `kakapo-bird` op\n" +
        "LEFT UNNEST op.optionListItemList AS item\n" +
        "LEFT UNNEST item.optionListDisplayList AS displayItem\n" +
        "WHERE op.docType = 'OptionList'\n" +
        "AND op.name = '" + criteria.getOptionListName() + "'\n" +
        "AND item.text = '" + criteria.getOptionListText() + "'\n" +
        "AND op.optionListItemList IS NOT MISSING\n" +
        "AND item.optionListDisplayList IS NOT MISSING";
    // possible requirement to order by existing order eg:
    // ORDER BY ARRAY_POSITION(displayItem, displayItem.physicalIndex);

    N1qlQuery query = N1qlQuery.simple(queryString);
    List<String> list = new ArrayList<>();
    template.queryN1QL(query).forEach(n1qlQueryRow -> {
      list.add(n1qlQueryRow.value().getString("name"));
    });
    return list;

  }

  @Override
  public Response<OptionListEntity> saveList(OptionListEntity newList) {
    String sqlTxt = "upsert into ~bucket (key, value) values ('~listName', {\"docType\": \"OptionList\", \"name\": \"~listName\","
        + "      \"optionListItemList\": [\n  ~listItems  \n"
        + "      ]})";
    Response<OptionListEntity> response = new Response<>(newList);
    response.setMessages(null);

    Object[] items = newList.getOptionListItemList().toArray();
    String itemTxt = "{\"text\": \"" + encodeForSQl(((OptionListItemEntity)items[0]).getText()) + "\"}";
    for(int i = 1; i < items.length; i++){
      itemTxt = itemTxt +  ", \n {\"text\": \"" + encodeForSQl(((OptionListItemEntity)items[i]).getText()) + "\"}";
    }
    Expression bucket = i(template.getCouchbaseBucket().name());
    //Statement statement = select("titleItemList**").from(bucket).where(i("docType").eq(s("OptionList")).and(i("name").eq(s("ListTitles"))));
    //N1qlQuery query = N1qlQuery.simple(statement);
    sqlTxt = sqlTxt.replaceAll("~bucket", bucket.toString());
    sqlTxt = sqlTxt.replaceAll("~listName", newList.getName());
    sqlTxt = sqlTxt.replaceAll("~listItems", itemTxt);
    N1qlQueryResult queryResult = template.queryN1QL(N1qlQuery.simple(sqlTxt));
    //N1qlQueryResult queryResult = template.queryN1QL(N1qlQuery.simple(createIndexSQL));
    //logger.info("createIndex: " + queryResult);

    //List<N1qlQueryRow> rows = template.queryN1QL(query).allRows();

    if (!queryResult.finalSuccess()) {
      ValidationMessage msg = new ValidationMessage();
      msg.setMessageText("Fail to save: " + queryResult);
      List<ValidationMessage> msgList = new ArrayList<ValidationMessage>();
      msgList.add(msg);
      response.setMessages(msgList);
    }
    return response;
  }

  private String encodeForSQl(String inTxt){
    //inTxt = inTxt.stripLeading().stripTrailing();
    inTxt = inTxt.trim();
    if (inTxt.endsWith("\\")){
      inTxt = inTxt.substring(0, inTxt.length()-1);
    }
    return inTxt.replace("\\", "\\\\\\\\").replace("\"", "\\\\\"");
  }

}
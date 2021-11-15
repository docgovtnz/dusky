package com.fronde.server.services.weight;

import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.services.bird.BirdRepository;
import com.fronde.server.services.record.RecordRepository;
import com.fronde.server.services.weight.strategy.WeightStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Component;

@Component
public class WeightRepository {

  public static final String REF_DATA_KEY = "WEIGHT_REF_DATA_ID";

  public static final String REF_EGG_WEIGHTS = "REF_DATA_EGG_WEIGHT_LOSS_PERCENTAGES";

  public static final String START_DATE = "01/01/1990";

  @Autowired
  protected CouchbaseTemplate template;

  @Autowired
  protected RecordRepository recordRepository;

  @Autowired
  protected BirdRepository birdRepository;

  public WeightReferenceData getReferenceData() {
    WeightReferenceData result = template.findById(REF_DATA_KEY, WeightReferenceData.class);
    return result;
  }

  public EggReferenceDataDTO getEggReferenceData() {
    return template.findById(REF_EGG_WEIGHTS, EggReferenceDataDTO.class);
  }

  public Map<String, Dataset> getEggWeights(List<String> birdIDs) {
    String sql = "select\n" +
        "  meta(r).id as id,\n" +
        "  r.birdID,\n" +
        "  b.birdName,\n" +
        "  DATE_DIFF_STR(r.dateTime, b.dateLaid, 'second') / 86400 x,\n" +
        "  r.weight.weight * 1000 y\n" +
        "from\n" +
        "  `kakapo-bird` r \n" +
        "  join `kakapo-bird` b on keys r.birdID\n" +
        "where\n" +
        "  r.docType = 'Record'\n" +
        "  and r.weight.weight is not missing\n" +
        "  and b.dateLaid is not missing\n" +
        "  and r.dateTime >= b.dateLaid\n" +
        "  and (b.dateHatched is missing or r.dateTime < b.dateHatched)\n" +
        "  and r.birdID in $birdIDs\n" +
        " order by\n" +
        "  r.birdName,  x";

    N1qlQuery query = N1qlQuery.parameterized(sql, JsonObject.create().put("birdIDs", birdIDs));

    Map<String, Dataset> resultMap = new HashMap<>();

    N1qlQueryResult results = template.queryN1QL(query);
    for (N1qlQueryRow row : results.allRows()) {

      String key = row.value().getString("birdID");

      Dataset dataset = resultMap.get(key);
      if (dataset == null) {
        dataset = new Dataset();
        String birdName = row.value().getString("birdName");
        dataset.setLabel(birdName + " (Actual)");
        dataset.setBirdID(key);
        dataset.setBirdName(birdName);
        dataset.setPointRadius(2);
        resultMap.put(key, dataset);
      }

      float x = row.value().getDouble("x").floatValue();
      float y = row.value().getDouble("y").floatValue();
      String recordId = row.value().getString("id");
      dataset.getData().add(new Point(x, y, recordId, null));
    }

    return resultMap;
  }

  public Map<String, Dataset> getBirdWeights(List<String> birdIDs) {
    String sql = "select\n" +
        "  r.birdID,\n" +
        "  b.birdName,\n" +
        "  DATE_DIFF_STR(r.dateTime, b.dateHatched, 'second') / 86400 x,\n" +
        "  r.weight.weight y,\n" +
        "  meta(r).id as id,\n" +
        "  r.chickHealth.cropStatus\n" +
        "from\n" +
        "  `kakapo-bird` r \n" +
        "  join `kakapo-bird` b on keys r.birdID\n" +
        "where\n" +
        "  r.docType = 'Record'\n" +
        "  and r.weight.weight is not missing\n" +
        "  and b.dateHatched is not missing\n" +
        "  and r.dateTime >= b.dateHatched\n" +
        "  and r.dateTime <= DATE_ADD_STR(b.dateHatched, 366, 'day')\n" +
        "  and r.birdID in $birdIDs\n" +
        " order by\n" +
        "  r.birdName," +
        "  x";

    N1qlQuery query = N1qlQuery.parameterized(sql, JsonObject.create().put("birdIDs", birdIDs));

    Map<String, Dataset> resultMap = new HashMap<>();

    N1qlQueryResult results = template.queryN1QL(query);
    for (N1qlQueryRow row : results.allRows()) {

      String key = row.value().getString("birdID");

      Dataset dataset = resultMap.get(key);
      if (dataset == null) {
        dataset = new Dataset();
        String birdName = row.value().getString("birdName");
        dataset.setLabel(birdName);
        dataset.setBirdID(key);
        dataset.setBirdName(birdName);
        dataset.setPointRadius(2);
        dataset.setBorderWidth(3);
        resultMap.put(key, dataset);
      }

      float x = row.value().getDouble("x").floatValue();
      float y = row.value().getDouble("y").floatValue();
      String recordId = row.value().getString("id");
      String cropStatus = row.value().getString("cropStatus");
      dataset.getData().add(new Point(x, y, recordId, cropStatus));
    }
    return resultMap;
  }

  //todo   ** return chart dataset with date for multi-bird
  public Map<String, Dataset> getBirdWeightsWithDate(List<String> birdIDs, WeightStrategy strategy)
      throws ParseException {
    Map<String, Dataset> resultMap = new HashMap<>();
    for (String birdID : birdIDs) {
      BirdEntity bird = birdRepository.findById(birdID).get();
      // Find the records.
      List<RecordEntity> recordList = recordRepository.findWeightRecordsByBirdID(birdID);
      // Filter the records.
      List<RecordEntity> filteredRecords = recordList
          .stream()
          .filter(record -> record.getDateTime() != null)
          .filter(record -> bird.getDateHatched() == null || !record.getDateTime()
              .before(bird.getDateHatched()))
          .filter(record -> record.getWeight() != null && record.getWeight().getWeight() != null)
          .collect(Collectors.toList());
      // Sort by date/time.
      filteredRecords.sort(Comparator.comparing(RecordEntity::getDateTime));
      // Send to the strategy. to process as Raw/Min
      List<WeightSummaryDTO> weightDTOs =  strategy.process(bird, filteredRecords);
      //todo ** convert to chart dataset
      String key = birdID;
      Dataset dataset = resultMap.get(key);
      if (dataset == null) {
        dataset = new Dataset();
        String birdName = bird.getBirdName();
        dataset.setLabel(birdName);
        dataset.setBirdID(key);
        dataset.setBirdName(birdName);
        dataset.setPointRadius(2);
        dataset.setBorderWidth(3);
        resultMap.put(key, dataset);
      }
      for (WeightSummaryDTO w: weightDTOs) {
        Date x = w.getDateTime();
        float y = w.getWeight() * 1000;
        dataset.getDataDate().add(new PointDate(x, y));
        //todo include data point for delta calculation
        float x2 = (w.getDateTime().getTime()/86400000);
//        String recordId = row.value().getString("id");
//        String cropStatus = row.value().getString("cropStatus");
        dataset.getData().add(new Point(x2, y, "", ""));

      }
    }
    return resultMap;
  }

}

package com.fronde.server.services.latestweightreport;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.bird.BirdCriteria;
import com.fronde.server.services.bird.BirdRepository;
import com.fronde.server.services.bird.BirdSearchDTO;
import com.fronde.server.utils.QueryUtils;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Component;

@Component
public class LatestWeightReportRepositoryImpl implements LatestWeightReportRepository {

  @Autowired
  protected QueryUtils queryUtils;

  @Autowired
  protected CouchbaseTemplate template;

  @Autowired
  protected BirdRepository birdRepository;


  public PagedResponse<LatestWeightReportDTO> generateReport(BirdCriteria criteria) {

    // First run the query for birds.
    PagedResponse<BirdSearchDTO> birds = birdRepository.findSearchDTOByCriteria(criteria);

    // Short-cut if no birds are found.
    if (birds.getTotal() == 0) {
      return new PagedResponse<>();
    } else {
      // Map the birds to weight DTOs.
      List<LatestWeightReportDTO> report = new LinkedList<>();
      Map<String, LatestWeightReportDTO> map = new HashMap<>();
      List<String> birdIDs = new LinkedList<>();
      birds.getResults().stream().forEach(birdDTO -> {
        LatestWeightReportDTO reportDTO = new LatestWeightReportDTO();
        reportDTO.setBirdID(birdDTO.getBirdID());
        reportDTO.setBirdName(birdDTO.getBirdName());
        report.add(reportDTO);
        map.put(reportDTO.getBirdID(), reportDTO);
        birdIDs.add(reportDTO.getBirdID());
      });

      // Query for the weights.
      String SQL = "SELECT\n" +
          "  r.birdID,\n" +
          "  weightsArray[0: LEAST(2, ARRAY_LENGTH(weightsArray))] weights\n" +
          "FROM \n" +
          "  `kakapo-bird` r\n" +
          "WHERE\n" +
          "  r.docType = 'Record' \n" +
          "  AND IFMISSING(r.weight.weight,-1) <> -1\n" +
          (birdIDs.size() < 30 ? "  AND r.birdID IN $birdIDs\n" : "") +
          "GROUP BY\n" +
          "  r.birdID\n" +
          "LETTING \n" +
          "  weightsArray = ARRAY_REVERSE(ARRAY_SORT(ARRAY_AGG({ 'dateTime': r.dateTime, 'weight': r.weight.weight })))";

      N1qlQuery q = N1qlQuery
          .parameterized(SQL, JsonObject.create().put("birdIDs", JsonArray.from(birdIDs)));
      N1qlQueryResult result = template.queryN1QL(q);

      result.forEach(row -> {
        LatestWeightReportDTO dto = map.get(row.value().getString("birdID"));

        if (dto != null && row.value().getArray("weights") != null) {

          if (row.value().getArray("weights").size() > 0) {
            JsonObject val = row.value().getArray("weights").getObject(0);
            dto.setLatestDate(
                Date.from(ZonedDateTime.parse(val.getString("dateTime")).toInstant()));
            dto.setLatestWeight(val.getDouble("weight").floatValue());
          }
          if (row.value().getArray("weights").size() > 1) {
            JsonObject val = row.value().getArray("weights").getObject(1);
            dto.setPreviousDate(
                Date.from(ZonedDateTime.parse(val.getString("dateTime")).toInstant()));
            dto.setPreviousWeight(val.getDouble("weight").floatValue());
          }
        }

      });

      PagedResponse<LatestWeightReportDTO> response = new PagedResponse<>();
      response.setResults(report);
      response.setPage(birds.getPage());
      response.setTotal(birds.getTotal());
      response.setPageSize(birds.getPageSize());
      return response;
    }
  }
}




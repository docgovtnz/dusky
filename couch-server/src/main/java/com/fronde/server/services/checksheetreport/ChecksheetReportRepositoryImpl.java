package com.fronde.server.services.checksheetreport;

import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.fronde.server.utils.QueryUtils;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChecksheetReportRepositoryImpl implements ChecksheetReportRepository {

  @Autowired
  protected QueryUtils queryUtils;

  @Autowired
  protected CouchbaseTemplate template;

  public List<ChecksheetDTO> generateReport(ChecksheetReportCriteria criteria) {
    String queryString = "SELECT \n" +
        " r.birdID,\n" +
        " b.birdName,\n" +
        " r.dateTime,\n" +
        " r.recordType,\n" +
        " r.reason\n" +
        "FROM `kakapo-bird` AS r JOIN `kakapo-bird` AS b ON KEYS r.birdID\n" +
        "LEFT JOIN `kakapo-bird` AS ls ON KEY ls.birdID FOR b \n" +
        "WHERE r.docType='Record' AND meta(r).id NOT LIKE '\\\\_sync%' AND r.dateTime >= $startDate AND r.dateTime <= $endDate\n" +
        "AND (b.dateHatched IS MISSING OR b.dateHatched <= $endDate)\n" +
        "AND b.currentIsland=$island AND b.alive = true AND ($sex IS NULL OR b.sex = $sex)\n" +
        "AND ls.docType = 'LifeStage'\n" +
        "GROUP BY b,r\n" +
        "LETTING mostRecentStage = max([ls.modifiedTime, ls.ageClass])\n" +
        "HAVING LOWER(mostRecentStage[1]) NOT LIKE LOWER('Egg%')";

    List<ChecksheetDTO> results = generateQueryResults(criteria, queryString);

    List<ChecksheetDTO> noraNetResults = generateNoraNetData(criteria);

    results.addAll(noraNetResults);

    results.sort(Comparator.comparing(checksheet -> checksheet.getBirdName()));

    return results;
  }

  private List<ChecksheetDTO> generateNoraNetData(ChecksheetReportCriteria criteria) {

    String queryString = "SELECT DISTINCT " +
        "dtb.birdID, " +
        "b.birdName, " +
        "n.activityDate AS dateTime, " +
        "'NoraNet Detection' AS recordType\n" +
        "FROM `kakapo-bird` n " +
        "UNNEST detectionList AS dt " +
        "LEFT UNNEST dt.birdList as dtb " +
        "LEFT JOIN `kakapo-bird` b ON KEYS dtb.birdID\n" +
        "WHERE n.docType='NoraNet' AND meta(n).id NOT LIKE '\\\\_sync%' " +
        "AND n.activityDate >= $startDate AND n.activityDate <= $endDate " +
        "AND n.island=$island " +
        "AND b.alive = true AND ($sex IS NULL OR b.sex = $sex)";

    List<ChecksheetDTO> results = generateQueryResults(criteria, queryString);;
    return results;
  }

  private List<ChecksheetDTO> generateQueryResults(ChecksheetReportCriteria criteria, String queryString) {
    N1qlQuery query = N1qlQuery.parameterized(queryString,
        JsonObject.create()
            .put("startDate", criteria.getFromDate().toInstant().atZone(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_INSTANT))
            .put("endDate", criteria.getToDate().toInstant().atZone(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_INSTANT))
            .put("island", criteria.getIsland())
            .put("sex", criteria.getSex()));
    return template.findByN1QLProjection(query, ChecksheetDTO.class);
  }

  public List<ChecksheetDTO> getAllBirds(ChecksheetReportCriteria criteria) {

    String allBirdsQuery = "SELECT\n" +
        "  meta(b).id birdID, \n" +
        "  b.birdName \n" +
        "FROM `kakapo-bird` b \n" +
        "LEFT JOIN `kakapo-bird` AS ls ON KEY ls.birdID FOR b \n" +
        "WHERE \n" +
        "  b.docType = 'Bird' \n" +
        "  AND meta(b).id NOT LIKE '\\\\_sync%' \n" +
        "  AND b.currentIsland = $island\n" +
        "  AND b.alive = true \n" +
        "  AND ls.docType = 'LifeStage'\n" +
        "  AND (b.dateHatched IS MISSING OR b.dateHatched <= $endDate)\n" +
        "  AND ($sex is null or b.sex = $sex)\n" +
        "  GROUP BY b\n" +
        "  LETTING mostRecentStage = max([ls.modifiedTime, ls.ageClass])\n" +
        "  HAVING LOWER(mostRecentStage[1]) NOT LIKE LOWER('Egg%')\n";

    N1qlQuery query2 = N1qlQuery.parameterized(allBirdsQuery,
        JsonObject.create()
            .put("island", criteria.getIsland())
            .put("endDate", criteria.getToDate().toInstant().atZone(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_INSTANT))
            .put("sex", criteria.getSex()));

    List<ChecksheetDTO> allBirds = template.findByN1QLProjection(query2, ChecksheetDTO.class);
    return allBirds;
  }
}
package com.fronde.server.services.matingreport;

import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.fronde.server.utils.QueryUtils;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Component;

@Component
public class MatingReportRepositoryImpl implements MatingReportRepository {

  @Autowired
  protected QueryUtils queryUtils;

  @Autowired
  protected CouchbaseTemplate template;

  public List<MatingReportDTO> generateReport(MatingReportCriteria criteria) {
    List<MatingReportDTO> dedupped = findMatingDTOByCriteria(criteria);

    // Counting switched off for now until it can be reimplemented correctly on a seasonal basis see: KD-136

    // determine female mating count
        /*Map<String, Integer> counts = new HashMap<>();

        List<MatingReportDTO> unfilteredResults = null;
        if (criteria.getBirdID() == null && criteria.getMinimumQuality() == null) {
            unfilteredResults = dedupped;
        } else {
            MatingReportCriteria unfilteredCriteria = new MatingReportCriteria();
            unfilteredCriteria.setFromDate(criteria.getFromDate());
            unfilteredCriteria.setToDate(criteria.getToDate());
            unfilteredCriteria.setIsland(criteria.getIsland());
            unfilteredResults = findMatingDTOByCriteria(unfilteredCriteria);
        }

        for (MatingReportDTO mating: unfilteredResults) {
            Integer count = counts.get(mating.getFemaleBirdID());
            if (count == null) {
                count = 0;
                counts.put(mating.getFemaleBirdID(), count);
            }
            counts.put(mating.getFemaleBirdID(), count + 1);
        }

        for (MatingReportDTO mating: dedupped) {
            mating.setFemaleMatingCount(counts.get(mating.getFemaleBirdID()));
        }*/

    return dedupped;
  }

  private List<MatingReportDTO> findMatingDTOByCriteria(MatingReportCriteria criteria) {
//        String queryString = "SELECT \n" +
//                " r.birdID,\n" +
//                " b.birdName,\n" +
//                " r.dateTime,\n" +
//                " r.recordType,\n" +
//                " r.reason\n" +
//                "FROM `kakapo-bird` r JOIN `kakapo-bird` b ON KEYS r.birdID\n" +
//                "WHERE r.docType='Record' and r.dateTime >= $startDate and r.dateTime <= $endDate " +
//                "      and b.currentIsland=$island and b.alive = true and ($sex IS NULL or b.sex = $sex)\n" +
//                "ORDER BY \n" +
//                "   b.birdName";

    String queryString = "SELECT \n" +
        "checkmateRecord.time as dateTime, \n" +
        "r.island, \n" +
        "meta(femaleBird).id femaleBirdID, \n" +
        "femaleBird.birdName femaleBirdName, \n" +
        "meta(maleBird).id maleBirdID, \n" +
        "maleBird.birdName maleBirdName, \n" +
        "checkmateRecord.duration, \n" +
        "checkmateRecord.quality \n" +
        "FROM \n" +
        "`kakapo-bird` r \n" +
        "INNER UNNEST checkmate.checkmateDataList checkmateRecord \n" +
        "LEFT JOIN `kakapo-bird` femaleBird ON KEYS checkmateRecord.birdId \n" +
        "LEFT JOIN `kakapo-bird` maleBird ON KEYS r.birdID \n" +
        "WHERE \n" +
        "    r.docType = 'Record' \n" +
        "AND meta(r).id NOT LIKE '\\\\_sync%' \n" +
        (criteria.getIsland() != null ? "AND r.island = $island \n" : "") +
        "AND ANY cr IN r.checkmate.checkmateDataList SATISFIES cr.time BETWEEN $fromDate AND $toDate END \n"
        +
        "AND checkmateRecord.time BETWEEN $fromDate AND $toDate \n" +
        (criteria.getBirdID() != null
            ? "AND (r.birdID = $birdID OR checkmateRecord.birdId = $birdID) \n" : "") +
        (criteria.getMinimumQuality() != null ? "AND checkmateRecord.quality >= $quality \n" : "") +
        "ORDER BY \n" +
        "checkmateRecord.time";

    N1qlQuery query = N1qlQuery.parameterized(queryString, JsonObject.create()
        .put("island", criteria.getIsland())
        .put("fromDate", criteria.getFromDate().toInstant().atZone(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT))
        .put("toDate", criteria.getToDate().toInstant().atZone(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT))
        .put("birdID", criteria.getBirdID())
        .put("quality", criteria.getMinimumQuality()));

    List<MatingReportDTO> results = template.findByN1QLProjection(query, MatingReportDTO.class);

    List<MatingReportDTO> dedupped = new ArrayList<>();

    // determine deduped results
    Map<MatingKey, SortedSet> map = new HashMap<>();
    for (MatingReportDTO mating : results) {
      MatingKey key = new MatingKey(mating.getMaleBirdID(), mating.getFemaleBirdID(),
          mating.getQuality(), mating.getDuration());
      SortedSet keyTimes = map.get(key);
      if (keyTimes == null) {
        keyTimes = new TreeSet().descendingSet();
        map.put(key, keyTimes);
      }
      Date from = mating.getDateTime();
      Date to = Date.from(mating.getDateTime().toInstant().minus(2, ChronoUnit.HOURS));
      if (keyTimes.subSet(from, to).isEmpty()) {
        keyTimes.add(mating.getDateTime());
        dedupped.add(mating);
      }
    }

    return dedupped;
  }

}

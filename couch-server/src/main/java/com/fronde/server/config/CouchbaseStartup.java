package com.fronde.server.config;

import static com.couchbase.client.java.query.Select.selectAll;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.s;

import com.couchbase.client.java.query.N1qlParams;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;
import com.fronde.server.services.application.RebuildStatus;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class CouchbaseStartup {

  private static final Logger logger = LoggerFactory.getLogger(CouchbaseStartup.class);

  private static final int MAX_INDEX_QUERY_TIMEOUT_MINUTES = 10;

  @Autowired
  protected CouchbaseTemplate template;

  private final String[][] INDEXES = {
      {"idx_docType", "CREATE INDEX `idx_docType` ON `kakapo-bird`(`docType`)"},
      {"idx_record_date",
          "CREATE INDEX `idx_record_date` ON `kakapo-bird`(`docType`, `dateTime`) WHERE (`docType` = 'Record')"},
      {"idx_record_recordType",
          "CREATE INDEX `idx_record_recordType` ON `kakapo-bird`(`docType`, `recordType`) WHERE (`docType` = 'Record')"},
      {"idx_record_activity",
          "CREATE INDEX `idx_record_activity` ON `kakapo-bird`(`docType`, `activity`) WHERE (`docType` = 'Record')"},
      {"idx_record_reason",
          "CREATE INDEX `idx_record_reason` ON `kakapo-bird`(`docType`, `reason`, `activity`) WHERE (`docType` = 'Record')"},
      {"idx_record_birdID",
          "CREATE INDEX `idx_record_birdID` ON `kakapo-bird`(`docType`, `birdID`) WHERE (`docType` = 'Record')"},
      {"idx_record_search_dto",
          "CREATE INDEX `idx_record_search_dto` ON `kakapo-bird`(`docType`, `dateTime` DESC, `island`, `birdID`, `locationID`, `recordType`, `activity`) WHERE (`docType` = 'Record')"},
      {"idx_record_where_are_they",
          "CREATE INDEX `idx_record_where_are_they` ON `kakapo-bird`(`birdID`,`dateTime`,`island`) WHERE (`docType` = 'Record')"},
      {"idx_modifiedByPersonId",
          "CREATE INDEX `idx_modifiedByPersonId` ON `kakapo-bird`(`modifiedByPersonId`)"},
      {"idx_observer",
          "CREATE INDEX idx_observer ON `kakapo-bird` ( DISTINCT ARRAY o.personID FOR o IN observerList END ) WHERE (`docType` = 'Record')"},
      {"idx_snarkactivity_date_activityType",
          "CREATE INDEX idx_snarkactivity_date_activityType ON `kakapo-bird`(`docType`, `date`, `activityType`) WHERE (`docType` = 'SnarkActivity')"},
      {"idx_snarkactivity_locationID_date_activityType",
          "CREATE INDEX idx_snarkactivity_locationID_date_activityType ON `kakapo-bird`(`docType`, `locationID`, `date`, `activityType`) WHERE (`docType` = 'SnarkActivity')"},
      {"idx_location_island",
          "CREATE INDEX idx_location_island ON `kakapo-bird`(`docType`, `island`) WHERE (`docType` = 'Location')"},
      {"idx_snarkactivity_snarkRecordList",
          "CREATE INDEX idx_snarkactivity_snarkRecordList ON `kakapo-bird` ( DISTINCT ARRAY sr.birdID FOR sr IN snarkRecordList END ) WHERE (`docType` = 'SnarkActivity')"},
      {"idx_lifestage_birdID",
          "CREATE INDEX idx_lifestage_birdID ON `kakapo-bird`(`birdID`) WHERE (`docType` = 'LifeStage')"},
      {"idx_record_txChange",
          "CREATE INDEX `idx_record_txChange` ON `kakapo-bird`(`docType`,`transmitterChange`) WHERE (`docType` = 'Record')"},
      {"idx_record_transmitterChange_birdID",
          "CREATE INDEX idx_record_transmitterChange_birdID ON `kakapo-bird`(`birdID`) WHERE (`docType` = 'Record' and transmitterChange is valued)"},
      {"idx_record_bands_birdID",
          "CREATE INDEX idx_record_bands_birdID ON `kakapo-bird`(`birdID`) WHERE (`docType` = 'Record' and bands is valued)"},
      {"idx_record_chips_birdID",
          "CREATE INDEX idx_record_chips_birdID ON `kakapo-bird`(`birdID`) WHERE (`docType` = 'Record' and chips is valued)"},
      {"idx_record_datetime_birdID",
          "CREATE INDEX idx_record_datetime_birdID ON `kakapo-bird`(`docType`,`dateTime`,`birdID`) WHERE (`docType` = 'Record')"},
      {"idx_location_birdID",
          "CREATE INDEX idx_location_birdID ON `kakapo-bird`(`docType`, `birdID`) WHERE (`docType` = 'Location')"},
      {"idx_feedout_targetBirdList",
          "CREATE INDEX idx_feedout_targetBirdList ON `kakapo-bird`( DISTINCT ARRAY tb.birdID FOR tb IN targetBirdList END ) WHERE (`docType` = 'FeedOut')"},
      // checkmateDataList.birdId is correct and differs to birdID used elsewhere
      {"idx_record_checkmateDataList",
          "CREATE INDEX idx_record_checkmateDataList ON `kakapo-bird`( DISTINCT ARRAY cd.birdId FOR cd IN checkmate.checkmateDataList END ) WHERE (`docType` = 'Record')"},
      {"idx_locationID",
          "CREATE INDEX idx_locationID ON `kakapo-bird`(`locationID`) WHERE (`docType` = 'Record' or `docType` = 'FeedOut' or `docType` = 'SnarkImport' or `docType` = 'SnarkActivity')"},
      {"idx_observerPersonID",
          "CREATE INDEX idx_observerPersonID ON `kakapo-bird`(`observerPersonID`) WHERE (`docType` = 'SnarkImport' or `docType` = 'SnarkActivity')"},
      {"idx_record_transmitterChange_txId",
          "CREATE INDEX `idx_record_transmitterChange_txId` ON `kakapo-bird`(`docType`,`transmitterChange`.`txId`) WHERE (`docType` = 'Record')"},
      {"idx_record_transmitterChange_txTo",
          "CREATE INDEX `idx_record_transmitterChange_txTo` ON `kakapo-bird`(`docType`,`transmitterChange`.`txTo`) WHERE (`docType` = 'Record')"},
      {"idx_record_transmitterChange_txFrom",
          "CREATE INDEX `idx_record_transmitterChange_txFrom` ON `kakapo-bird`(`docType`,`transmitterChange`.`txFrom`) WHERE (`docType` = 'Record')"},
      {"idx_record_snarkData",
          "CREATE INDEX `idx_record_snarkData` ON `kakapo-bird`(`docType`,`snarkData`.`snarkActivityID`) WHERE (`docType` = 'Record')"},
      {"idx_transmitter_lastRecordId",
          "CREATE INDEX `idx_transmitter_lastRecordId` ON `kakapo-bird`(`lastRecordId`) WHERE (`docType` = 'Transmitter')"},
      {"idx_feedout_dateInMissing",
          "CREATE INDEX `idx_feedout_dateInMissing` ON `kakapo-bird` (IFMISSING(`dateIn`, '9999-12-31')) WHERE (`docType` = \"FeedOut\")"},
      {"idx_record_checkmateDataList_time",
          "CREATE INDEX `idx_record_checkmateDataList_time` ON `kakapo-bird`(`docType`, DISTINCT ARRAY cr.time FOR cr IN checkmate.checkmateDataList END) WHERE (`docType` = 'Record')"},
      {"idx_bird_currentLocationID",
          "CREATE INDEX `idx_bird_currentLocationID` ON `kakapo-bird`(`currentLocationID`) WHERE (`docType` = 'Bird')"},
      {"idx_nestobs_date",
          "CREATE INDEX `idx_nestobs_date` ON `kakapo-bird` (`docType`, `dateTime`)"},
      {"idx_latestweight_report",
          "CREATE INDEX `idx_latestweight_report` ON `kakapo-bird`(ifmissing((`weight`.`weight`), (-1)),`birdID`,`dateTime`) WHERE (`docType` = \"Record\")"},
      {"idx_record_weight_birdID_dateTime",
          "CREATE INDEX `idx_record_weight_birdID_dateTime` ON `kakapo-bird`(`docType`,weight.weight is not missing,`birdID`,`dateTime`) WHERE (`docType` = 'Record')"},
      {"idx_record_otherSampleList",
          "CREATE INDEX idx_record_otherSampleList ON `kakapo-bird`( DISTINCT ARRAY s.sampleID FOR s IN otherSampleList END ) WHERE (`docType` = 'Record')"},
      {"idx_record_bloodSampleList",
          "CREATE INDEX idx_record_bloodSampleList ON `kakapo-bird`( DISTINCT ARRAY s.sampleID for s in bloodSampleDetail.bloodSampleList END ) WHERE (`docType` = 'Record')"},
      {"idx_record_swabSampleList",
          "CREATE INDEX idx_record_swabSampleList ON `kakapo-bird`( DISTINCT ARRAY s.sampleID FOR s IN swabSampleList END ) WHERE (`docType` = 'Record')"},
      {"idx_record_spermSampleList",
          "CREATE INDEX idx_record_spermSampleList ON `kakapo-bird`( DISTINCT ARRAY s.sampleID FOR s IN spermSampleList END ) WHERE (`docType` = 'Record')"},
      {"idx_sample_sampleName",
          "CREATE INDEX idx_sample_sampleName ON `kakapo-bird`(`docType`, `sampleName`) WHERE (`docType` = 'Sample')"},
      {"idx_sample_sampleCategory",
          "CREATE INDEX idx_sample_sampleCategory ON `kakapo-bird`(`docType`, `sampleCategory`) WHERE (`docType` = 'Sample')"},
      {"idx_sample_resultsEntered_sampleCategory",
          "CREATE INDEX idx_sample_resultsEntered_sampleCategory ON `kakapo-bird`(`docType`, (haematologyTestList is valued and haematologyTestList <> []) or (chemistryAssayList is valued and chemistryAssayList <> []) or (microbiologyAndParasitologyTestList is valued and microbiologyAndParasitologyTestList <> []),`sampleCategory`) WHERE (`docType` = 'Sample')"},
      {"idx_sample_resultsNotEntered_sampleCategory",
          "CREATE INDEX idx_sample_resultsNotEntered_sampleCategory ON `kakapo-bird`(`docType`, not ((haematologyTestList is valued and haematologyTestList <> []) or (chemistryAssayList is valued and chemistryAssayList <> []) or (microbiologyAndParasitologyTestList is valued and microbiologyAndParasitologyTestList <> [])),`sampleCategory`) WHERE (`docType` = 'Sample')"},
      {"idx_sample_haematologyTestList",
          "CREATE INDEX idx_sample_haematologyTestList ON `kakapo-bird`(`haematologyTestList` is not missing,`sampleCategory`,`sampleType`,DISTINCT ARRAY ht FOR ht IN haematologyTestList WHEN (ht.statsExclude is missing or not ht.statsExclude) END) WHERE (`docType` = 'Sample')"},
      {"idx_sample_chemistryAssayList",
          "CREATE INDEX idx_sample_chemistryAssayList ON `kakapo-bird`(`chemistryAssayList` is not missing,`sampleCategory`,`sampleType`,DISTINCT ARRAY ca FOR ca IN chemistryAssayList WHEN (ca.statsExclude is missing or not ca.statsExclude) END) WHERE (`docType` = 'Sample')"},
      {"idx_noranet_detectionList",
          "CREATE INDEX idx_noranet_detectionList ON `kakapo-bird` (`detectionList` is not missing, DISTINCT ARRAY dt.uhfId FOR dt IN detectionList END ) WHERE (`docType` = 'NoraNet' and `detectionList` is valued)"},
      {"idx_noranet_standardList",
          "CREATE INDEX idx_noranet_standardList ON `kakapo-bird` (`standardList` is not missing, DISTINCT ARRAY dt.uhfId FOR dt IN standardList END ) WHERE (`docType` = 'NoraNet' and `standardList` is valued)"},
      {"idx_noranet_eggTimerList",
          "CREATE INDEX idx_noranet_eggTimerList ON `kakapo-bird` (`eggTimerList` is not missing, DISTINCT ARRAY dt.uhfId FOR dt IN eggTimerList END ) WHERE (`docType` = 'NoraNet' and `eggTimerList` is valued)"},
      {"idx_noranet_cmShortList",
          "CREATE INDEX idx_noranet_cmShortList ON `kakapo-bird` (`cmShortList` is not missing, DISTINCT ARRAY dt.uhfId FOR dt IN cmShortList END ) WHERE (`docType` = 'NoraNet' and `cmShortList` is valued)"},
      {"idx_noranet_cmLongList",
          "CREATE INDEX idx_noranet_cmLongList ON `kakapo-bird` (`cmLongList` is not missing, DISTINCT ARRAY dt.uhfId FOR dt IN cmLongList END ) WHERE (`docType` = 'NoraNet' and `cmLongList` is valued)"},

  };

  public void init() {
    createAdditionalIndexes();
    rebuildIndexes();
  }

  private static IndexRebuilder rebuilder = null;

  public synchronized RebuildStatus resetIndexes() {
    if (rebuilder == null || rebuilder.complete) {
      rebuilder = new IndexRebuilder();
      Thread t = new Thread(rebuilder);
      t.start();
    }
    return rebuilder.rebuildStatus;
  }

  public RebuildStatus getRebuildStatus() {
    if (rebuilder == null) {
      return new RebuildStatus("Rebuilder not running", 0);
    } else {
      return rebuilder.rebuildStatus;
    }

  }

  public class IndexRebuilder implements Runnable {

    int progress = 0;
    boolean complete = false;
    RebuildStatus rebuildStatus = new RebuildStatus("Starting", 0);


    public void run() {
      // Delete all the indexes.
      this.rebuildStatus.startTimer();

      logger.info("Deleting primary index");
      N1qlQueryResult queryResult = template.queryN1QL(N1qlQuery
          .simple("drop index `kakapo-bird`.`#primary`", N1qlParams.build()
              .serverSideTimeout(MAX_INDEX_QUERY_TIMEOUT_MINUTES, TimeUnit.MINUTES)));

      for (int i = 0; i < INDEXES.length; i++) {
        logger.info("Deleting index: " + INDEXES[i][0]);
        queryResult = template.queryN1QL(N1qlQuery
            .simple("DROP INDEX `kakapo-bird`.`" + INDEXES[i][0] + "`", N1qlParams.build()
                .serverSideTimeout(MAX_INDEX_QUERY_TIMEOUT_MINUTES, TimeUnit.MINUTES)));
      }
      logger.info("Index deletion complete");

      logger.info("Creating primary index");
      rebuildStatus.setStatus("In Progress");
      queryResult = template.queryN1QL(N1qlQuery.simple("CREATE PRIMARY INDEX ON `kakapo-bird`",
          N1qlParams.build().serverSideTimeout(MAX_INDEX_QUERY_TIMEOUT_MINUTES, TimeUnit.MINUTES)));
      rebuildStatus.setProgress((int) (1 / ((float) (INDEXES.length + 1)) * 100));

      // Recreate all the indexes.
      for (int i = 0; i < INDEXES.length; i++) {
        logger.info("Creating index: " + INDEXES[i][0]);
        queryResult = template.queryN1QL(N1qlQuery.simple(INDEXES[i][1], N1qlParams.build()
            .serverSideTimeout(MAX_INDEX_QUERY_TIMEOUT_MINUTES, TimeUnit.MINUTES)));
        rebuildStatus.setProgress((int) ((i + 1) / ((float) (INDEXES.length + 1)) * 100));
      }

      logger.info("Index creation complete");

      rebuildStatus.setProgress(100);
      rebuildStatus.setStatus("Complete");

      // Clear out the rebuilder thread.
      this.complete = true;
    }
  }


  private void createAdditionalIndexes() {
    int createdCount = 0;
    for (String[] nextRow : INDEXES) {
      String indexName = nextRow[0];
      if (!doesIndexExist(indexName)) {
        String indexSQL = nextRow[1];
        createIndex(indexSQL);
        createdCount++;
      }
    }
    logger.info("CouchbaseStartup created " + createdCount + " indexes.");
  }

  private boolean doesIndexExist(String indexName) {
    Statement statement = selectAll(i("*"))
        .from("system:indexes")
        .where(i("name").eq(s(indexName)));

    N1qlQueryResult queryResult = template.queryN1QL(N1qlQuery.simple(statement));
    return queryResult.allRows().size() > 0;
  }

  private void createIndex(String createIndexSQL) {
    logger.info("createIndex: " + createIndexSQL);
    N1qlQueryResult queryResult = template.queryN1QL(N1qlQuery.simple(createIndexSQL));
    logger.info("createIndex: " + queryResult);
  }

  private void rebuildIndexes() {
    logger.info("Checking status of Indexes and rebuild if necessary");
    Statement statement = selectAll(i("name"))
        .from("system:indexes")
        .where(i("state").eq(s("deferred")));

    N1qlQueryResult queryResult = template.queryN1QL(N1qlQuery.simple(statement));
    logger.info("Index status result: " + queryResult);

    if (queryResult.allRows().size() > 0) {

      StringBuilder sb = new StringBuilder();
      for (N1qlQueryRow indexRow : queryResult.allRows()) {
        if (sb.length() > 0) {
          sb.append(',');
        }
        sb.append(indexRow.value().get("name").toString());
      }

      if (sb.length() > 0 && !",".equals(sb.toString())) {
        logger.info("rebuildIndex: " + sb);
        N1qlQueryResult buildQueryResult = template.queryN1QL(
            N1qlQuery.simple(String.format("BUILD INDEX ON `kakapo-bird` (%s)", sb)));
        logger.info("rebuildIndex result (index builds may be deferred): " + buildQueryResult);
      } else {
        logger.warn("Index rebuild results but no names?");
      }

    } else {
      logger.info("No indexes to rebuild");
    }
  }


}

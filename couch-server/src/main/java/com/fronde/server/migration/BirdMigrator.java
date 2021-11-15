package com.fronde.server.migration;

import com.fronde.server.domain.BirdEntity;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
@Order(20)
public class BirdMigrator extends BirdBaseMigrator implements Migrator {

  @Autowired
  protected MigrationUtils migrationUtils;

  @Override
  public void migrate() {
    super.migrate();
  }

  @Override
  protected BirdEntity readEntity(SqlRowSet rs) {
    BirdEntity bird = super.readEntity(rs);

    // override the random ID and use the one from the BirdIDMap
    bird.setId(migrationUtils.convertBirdIDForBirds(bird.getOldBirdID()));

    bird = readCurrentBand(bird);
    bird = readCurrentChip(bird);
    bird.setCurrentTxRecordId(readCurrentTxRecordID(bird.getOldBirdID()));

    return bird;
  }

  /**
   * Set a birds most recent bandId and leg it was attached to.
   *
   * @param bird
   * @return Record couchbase ID
   */
  protected BirdEntity readCurrentBand(BirdEntity bird) {

    class Bands {

      private String bandId;
      private String leg;

      public String getBandId() {
        return bandId;
      }

      public void setBandId(String bandId) {
        this.bandId = bandId;
      }

      public String getLeg() {
        return leg;
      }

      public void setLeg(String leg) {
        this.leg = leg;
      }
    }

    String birdID = bird.getOldBirdID();

    List<Bands> list = jdbcTemplate.query(
        "SELECT * FROM [BandChange] as b JOIN [Recovery] as r on b.[RecoveryID] = r.[RocoveryID] WHERE r.[BirdID] = ? AND b.[NewBandNumber] IS NOT NULL ORDER BY r.[DateTime] DESC",
        new Object[]{birdID},
        (rs, rowNum) -> {
          Bands band = new Bands();
          band.setBandId(rs.getString("NewBandNumber"));
          band.setLeg(rs.getString("Leg"));
          return band;
        }
    );
    if (!list.isEmpty()) {
      Bands birdBand = list.get(0);
      // Don't set if the bird has no band. Needed to check for weird spelling of "Unbanded".
      if (!birdBand.getBandId().toLowerCase().contains("unban") && !birdBand.getBandId()
          .toLowerCase().contains("unbun")) {
        bird.setCurrentBandId(birdBand.getBandId());
      }
      bird.setCurrentBandLeg(birdBand.getLeg());
    }
    return bird;
  }

  /**
   * Get the most recent recordID for a chip change by bird ID.
   *
   * @param bird
   * @return
   */
  protected BirdEntity readCurrentChip(BirdEntity bird) {
    String birdID = bird.getOldBirdID();
    List<String> list = jdbcTemplate.query(
        "SELECT * FROM [BandChange] as b JOIN [Recovery] as r on b.[RecoveryID] = r.[RocoveryID] WHERE r.[BirdID] = ? AND b.[Microchip] IS NOT NULL ORDER BY r.[DateTime] DESC",
        new Object[]{birdID},
        (rs, rowNum) -> rs.getString("Microchip")
    );
    if (!list.isEmpty()) {
      bird.setCurrentChipId(list.get(0));
    }
    return bird;
  }

  /**
   * Get the most recent recordID for a tx change by bird ID.
   *
   * @param birdID
   * @return Record couchbase ID
   */
  protected String readCurrentTxRecordID(String birdID) {
    List<String> list = jdbcTemplate.query(
        "SELECT t.[RecoveryID] FROM [TxChange] as t JOIN [Recovery] as r on t.[RecoveryID] = r.[RocoveryID] WHERE r.[BirdID] = ? AND t.[TxId] IS NOT NULL ORDER BY r.[DateTime] DESC",
        new Object[]{birdID},
        (rs, rowNum) -> rs.getString("RecoveryID")
    );
    String recordId = null;
    if (!list.isEmpty()) {
      recordId = migrationUtils.convertRecordIDForRecords(list.get(0));
    }
    return recordId;
  }
}



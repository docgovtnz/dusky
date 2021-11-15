package com.fronde.server.migration;

import com.fronde.server.domain.SnarkActivityEntity;
import com.fronde.server.domain.SnarkRecordEntity;
import com.fronde.server.domain.TrackAndBowlActivityEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
@Order(999)
public class SnarkActivityMigrator extends SnarkActivityBaseMigrator implements Migrator {

  @Override
  public void migrate() {
    super.migrate();
  }

  private static final Map<Integer, String> fightingSignMap = new HashMap<>();
  private static final Map<Integer, String> grubbingMap = new HashMap<>();
  private static final Map<Integer, String> activityTypeMap = new HashMap<>();
  private static final Map<Integer, String> sticksMap = new HashMap<>();
  private static final Map<Integer, String> trackActivityMap = new HashMap<>();
  private static final Map<Integer, String> matingSignMap = new HashMap<>();
  private static final Map<Integer, String> tapeUsedMap = new HashMap<>();

  static {
    activityTypeMap.put(1, "Track and Bowl");
    activityTypeMap.put(2, "Hopper");
    activityTypeMap.put(3, "Nest");

    fightingSignMap.put(0, "None");
    fightingSignMap.put(1, "Possibly");
    fightingSignMap.put(2, "Definitely");

    grubbingMap.put(0, "None");
    grubbingMap.put(1, "A Little");
    grubbingMap.put(2, "A Lot");

    matingSignMap.put(0, "None");
    matingSignMap.put(1, "Possibly");
    matingSignMap.put(2, "Definitely");

    sticksMap.put(0, "Up");
    sticksMap.put(1, "Down");
    sticksMap.put(2, "Gone");

    trackActivityMap.put(0, "None");
    trackActivityMap.put(1, "Possibly");
    trackActivityMap.put(2, "Definitely");

    tapeUsedMap.put(0, "None");
    tapeUsedMap.put(1, "<1/4 Tape");
    tapeUsedMap.put(2, "1/4 - Full Tape");
    tapeUsedMap.put(3, "Full Tape");
    tapeUsedMap.put(4, "Batteries Flat");
  }

  protected SnarkActivityEntity readEntity(SqlRowSet rs) {
    SnarkActivityEntity entity = super.readEntity(rs);
    entity.setId(migrationUtils.convertTAndBRecIdForSnarkActivities(entity.getOldTAndBRecId()));
    entity.setActivityType(activityTypeMap.get(entity.getOldTbHopper()));
    entity.setLocationID(migrationUtils.convertLocationName(entity.getOldTAndB()));
    entity.setObserverPersonID(migrationUtils.convertPersonName(entity.getObserverPersonID()));
    // if the type is track and bowl or there happens to be a value for a track and bowl column
    if ("Track and Bowl".equals(entity.getActivityType()) ||
        entity.getOldBooming() ||
        entity.getOldChinging() ||
        (entity.getOldFightingSign() != null && entity.getOldFightingSign() != 0) ||
        entity.getOldGrubbing() != null ||
        (entity.getOldMatingSign() != null && entity.getOldMatingSign() != 0) ||
        entity.getOldSkraaking() ||
        entity.getOldSticks() != null ||
        (entity.getOldTapeUsed() != null && entity.getOldTapeUsed()) ||
        (entity.getOldTimeRecorded() != null && entity.getOldMatingSign() != 0) ||
        entity.getOldTrackActivity() != null) {
      // add track and bowl activity details
      TrackAndBowlActivityEntity tba = new TrackAndBowlActivityEntity();
      tba.setBoom(entity.getOldBooming());
      tba.setChing(entity.getOldChinging());
      tba.setFightingSign(fightingSignMap.get(entity.getOldFightingSign()));
      tba.setGrubbing(grubbingMap.get(entity.getOldGrubbing()));
      tba.setMatingSign(matingSignMap.get(entity.getOldMatingSign()));
      tba.setSkraak(entity.getOldSkraaking());
      tba.setSticks(sticksMap.get(entity.getOldSticks()));
      if (entity.getOldTimeRecorded() != null) {
        tba.setTapeUsed(tapeUsedMap.get(entity.getOldTimeRecorded()));
      } else {
        tba.setTapeUsed("None");
      }
      tba.setTrackActivity(trackActivityMap.get(entity.getOldTrackActivity()));
      entity.setTrackAndBowlActivity(tba);
    }
    return entity;
  }

  protected List<SnarkRecordEntity> readSnarkRecordList(String parentID) {
    List<SnarkRecordEntity> list = super.readSnarkRecordList(parentID);
    for (SnarkRecordEntity entity : list) {
      entity.setBirdID(migrationUtils.convertBirdID(entity.getOldBirdId()));
      entity.setRecordID(migrationUtils.convertRecordID(entity.getOldSnarkRecId()));
    }
    // if there are not snark records then just put as null so it won't be shown
    if (list.isEmpty()) {
      return null;
    } else {
      return list;
    }
  }

}

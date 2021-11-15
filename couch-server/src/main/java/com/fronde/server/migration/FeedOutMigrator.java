package com.fronde.server.migration;

import static com.fronde.server.migration.MigrationUtils.getIntOrNull;

import com.fronde.server.domain.FeedOutEntity;
import com.fronde.server.domain.FoodTallyEntity;
import com.fronde.server.domain.TargetBirdEntity;
import com.fronde.server.services.common.ObjectIdFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
@Order(999)
public class FeedOutMigrator extends FeedOutBaseMigrator implements Migrator {

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  private static final Map<String, Map<String, String>> feedTypeMap = new HashMap<String, Map<String, String>>();
  private static final String FT_IN_COL = "inCol";
  private static final String FT_OUT_COL = "outCol";

  static {
    addFeedType("Almonds", "Almonds in", "Almonds out");
    addFeedType("Apple", "Apple in", "Apple out");
    addFeedType("Corn", "Corn in", "Corn out");
    addFeedType("Gel: rimu fruit mimic", "Gel: rimu fruit mimic in", "Gel: rimu fruit mimic out");
    addFeedType("Green walnuts", "Green walnuts in", "Green walnuts out");
    addFeedType("HPC (Harrisons Pellets)", "HPC (Harrisons Pellets) in",
        "HPC (Harrisons Pellets) out");
    addFeedType("Kahikatea", "Kahikatea in", "Kahikatea out");
    addFeedType("Kumera", "Kumera in", "Kumera out");
    addFeedType("Large pine cones", "Large pine cones in", "Large pine cones out");
    addFeedType("Macadamia", "Macadamia in", "Macadamia out");
    addFeedType("Pellets: unripe rimu mimic", "Pellets: unripe rimu mimic in",
        "Pellets: unripe rimu mimic out");
    addFeedType("Pine cones", "Pine cones in", "Pine cones out");
    addFeedType("Power treats (Harrisons Pellets)", "Power treats (Harrisons Pellets) in",
        "Power treats (Harrisons Pellets) out");
    addFeedType("Pumpkin pellets YCP02", "Pumpkin pellets YCP02 in", "Pumpkin pellets YCP02 out");
    addFeedType("Spirulina pellets", "Spirulina pellets in", "Spirulina pellets out");
    addFeedType("Walnuts", "Walnuts in", "Walnuts out");
  }

  private static void addFeedType(String name, String inCol, String outCol) {
    Map<String, String> feedType = new HashMap<String, String>();
    feedTypeMap.put(name, feedType);
    feedType.put(FT_IN_COL, inCol);
    feedType.put(FT_OUT_COL, outCol);
  }

  @Override
  public void migrate() {
    super.migrate();
  }

  protected FeedOutEntity readEntity(SqlRowSet rs) {
    FeedOutEntity entity = new FeedOutEntity();
    entity.setFoodTallyList(readFoodTallyList(rs));
    entity.setTargetBirdList(readTargetBirdList(rs));
    entity.setComments(rs.getString("Comments"));
    entity.setDateIn(rs.getTimestamp("Date in"));
    entity.setDateOut(rs.getTimestamp("Date out"));
    entity.setDocType("FeedOut");
    entity.setFeedId(rs.getString("Feed id"));
    entity.setId(objectIdFactory.create());
    entity.setLocationID(migrationUtils.convertLocationName(rs.getString("location")));
    entity.setModifiedByPersonId(migrationUtils.getModifiedByPersonId());
    entity.setModifiedTime(migrationUtils.getModifiedTime());
    entity.setNewRecord(rs.getString("New record"));
    // Skip migration for: revision
    entity.setOtherFood(rs.getString("Other food"));
    entity.setOtherIn(getIntOrNull(rs, "Other in"));
    entity.setOtherOut(getIntOrNull(rs, "Other out"));

    return entity;
  }

  private List<FoodTallyEntity> readFoodTallyList(SqlRowSet rs) {
    List<FoodTallyEntity> l = new ArrayList<FoodTallyEntity>();

    for (Map.Entry<String, Map<String, String>> e : feedTypeMap.entrySet()) {
      String name = e.getKey();
      int inValue = rs.getInt(e.getValue().get(FT_IN_COL));
      boolean inValueNull = rs.wasNull();
      int outValue = rs.getInt(e.getValue().get(FT_OUT_COL));
      boolean outValueNull = rs.wasNull();
      if (!inValueNull || !outValueNull) {
        FoodTallyEntity ft = new FoodTallyEntity();
        ft.setName(name);
        if (!inValueNull) {
          ft.setIn(inValue);
        }
        if (!outValueNull) {
          ft.setOut(outValue);
        }
        l.add(ft);
      }
    }
    Integer otherIn = getIntOrNull(rs, "Other in");
    Integer otherOut = getIntOrNull(rs, "Other out");
    String otherFood = rs.getString("Other food");
    if (otherFood != null) {
      FoodTallyEntity ft = new FoodTallyEntity();
      ft.setName(otherFood);
      ft.setIn(otherIn);
      ft.setOut(otherOut);
      l.add(ft);
    }
    if (l.isEmpty()) {
      return null;
    }
    return l;
  }

  private List<TargetBirdEntity> readTargetBirdList(SqlRowSet rs) {
    List<TargetBirdEntity> l = new ArrayList<TargetBirdEntity>();

    String[] birdColumns = new String[]{"Bird 1", "Bird 2", "Bird 3"};
    for (String c : birdColumns) {
      String birdID = rs.getString(c);
      if (birdID != null && !birdID.trim().isEmpty()) {
        TargetBirdEntity bt = new TargetBirdEntity();
        bt.setBirdID(migrationUtils.convertBirdID(birdID));
        l.add(bt);
      }
    }
    if (l.isEmpty()) {
      return null;
    }
    return l;
  }
}



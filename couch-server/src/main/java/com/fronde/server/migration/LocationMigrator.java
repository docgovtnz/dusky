package com.fronde.server.migration;

import com.fronde.server.domain.LocationEntity;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Order(30)
public class LocationMigrator extends LocationBaseMigrator implements Migrator {

  private static final Map<String, String> nestTypeMap;

  static {
    nestTypeMap = new HashMap<String, String>();
    // nest type is a float in the source database and comes with ".0" when converted to a string
    nestTypeMap.put("1.0", "Hole");
    nestTypeMap.put("2.0", "Rock");
    nestTypeMap.put("3.0", "Tree");
    nestTypeMap.put("4.0", "Cave");
    nestTypeMap.put("5.0", "Veg");
  }

  @Autowired
  protected MigrationUtils migrationUtils;

  @Override
  public void migrate() {
    super.migrate();
  }

  @Override
  protected LocationEntity readEntity(SqlRowSet rs) {
    LocationEntity location = super.readEntity(rs);

    // override the random ID and use the one from the LocationNameMap
    location.setId(migrationUtils.convertLocationNameForLocations(location.getLocationName()));

    if (location.getNestDetails() != null) {
      if (!StringUtils.isEmpty(location.getNestDetails().getNestType())) {
        location.getNestDetails()
            .setNestType(nestTypeMap.get(location.getNestDetails().getNestType()));
      }
      if (!StringUtils.isEmpty(location.getNestDetails().getOldSiteDescription())) {
        if (!StringUtils.isEmpty(location.getComments())) {
          location.setComments(
              location.getComments() + "\nSite Description: " + location.getNestDetails()
                  .getOldSiteDescription());
        } else {
          location.setComments(location.getNestDetails().getOldSiteDescription());
        }
      }
    }

    return location;
  }

}



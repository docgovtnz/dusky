package com.fronde.server.migration;

import static com.fronde.server.migration.MigrationUtils.getBooleanOrNull;
import static com.fronde.server.migration.MigrationUtils.getFloatOrNull;
import static com.fronde.server.migration.MigrationUtils.getIntOrNull;

import com.fronde.server.domain.LocationEntity;
import com.fronde.server.domain.NestDetailsEntity;
import com.fronde.server.services.common.ObjectIdFactory;
import com.fronde.server.services.location.LocationRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public abstract class LocationBaseMigrator implements Migrator {


  @Autowired
  protected MigrationUtils migrationUtils;

  @Autowired
  protected JdbcTemplate jdbcTemplate;

  @Autowired
  protected LocationRepository repository;

  @Autowired
  protected ProgressMonitor progressMonitor;

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  public void migrate() {
    progressMonitor.reset(LocationEntity.class);
    progressMonitor.println("started");

    SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM [Named Locations]");
    while (sqlRowSet.next()) {
      LocationEntity entity = readEntity(sqlRowSet);
      migrationUtils.createRevision(entity);
      repository.save(entity);
      progressMonitor.tick();
    }

    progressMonitor.end();
    progressMonitor.println("save ok");
  }

  protected LocationEntity readEntity(SqlRowSet rs) {
    LocationEntity entity = new LocationEntity();

    entity.setActive(getBooleanOrNull(rs, "Activity Status"));
    entity.setBirdID(migrationUtils.convertBirdIDForBirds(rs.getString("BirdID")));
    entity.setCaptivityType(rs.getString("CaptivityType"));
    entity.setComments(rs.getString("Comments"));
    entity.setDocType("Location");
    entity.setEasting(getFloatOrNull(rs, "Easting"));
    entity.setFirstDate(rs.getTimestamp("FirstDate"));
    entity.setGpscount(getIntOrNull(rs, "Gpscount"));
    entity.setId(objectIdFactory.create());
    entity.setIsland(rs.getString("ISLAND"));
    entity.setLocationName(rs.getString("LocationName"));
    entity.setLocationType(rs.getString("LocationType"));
    entity.setMappingMethod(rs.getString("Maping Method"));
    entity.setModifiedByPersonId(migrationUtils.getModifiedByPersonId());
    entity.setModifiedTime(migrationUtils.getModifiedTime());
    entity.setNorthing(getFloatOrNull(rs, "Northing"));
    // Skip migration for: revision
    entity.setNestDetails(readNestDetails(rs.getString("LocationName")));

    return entity;
  }

  protected NestDetailsEntity readNestDetails(String parentID) {
    List<NestDetailsEntity> list = jdbcTemplate.query(
        "SELECT * FROM [Nest Details] as t WHERE t.[Location Name] = ? ", new Object[]{parentID},
        (rs, rowNum) -> {
          NestDetailsEntity entity = new NestDetailsEntity();

          entity.setChamberHeight(getFloatOrNull(rs, "Chamber Height"));
          entity.setChamberLength(getFloatOrNull(rs, "Chamber length"));
          entity.setChamberRise(getFloatOrNull(rs, "Chamber Rise"));
          entity.setChamberWidth(getFloatOrNull(rs, "Chamber Diameter"));
          // Skip migration for: clutch
          entity.setEntranceHeight(getFloatOrNull(rs, "Entrance Height"));
          entity.setEntranceRise(getFloatOrNull(rs, "Entrance Rise"));
          entity.setEntranceWidth(getFloatOrNull(rs, "Entrance Diameter"));
          entity.setLocationName(rs.getString("Location Name"));
          // Skip migration for: nestBox
          // Skip migration for: nestBoxDateAdded
          entity.setNestType(rs.getString("Nest Type"));
          entity.setOldSiteDescription(rs.getString("Site Description"));
          return entity;
        }
    );

    return MigrationUtils.optionalResult(list);
  }

}



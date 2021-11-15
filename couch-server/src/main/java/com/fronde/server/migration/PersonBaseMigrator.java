package com.fronde.server.migration;

import static com.fronde.server.migration.MigrationUtils.getBooleanOrNull;

import com.fronde.server.domain.PersonEntity;
import com.fronde.server.services.common.ObjectIdFactory;
import com.fronde.server.services.person.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public abstract class PersonBaseMigrator implements Migrator {


  @Autowired
  protected MigrationUtils migrationUtils;

  @Autowired
  protected JdbcTemplate jdbcTemplate;

  @Autowired
  protected PersonRepository repository;

  @Autowired
  protected ProgressMonitor progressMonitor;

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  public void migrate() {
    progressMonitor.reset(PersonEntity.class);
    progressMonitor.println("started");

    SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM [Observer Details]");
    while (sqlRowSet.next()) {
      PersonEntity entity = readEntity(sqlRowSet);
      migrationUtils.createRevision(entity);
      repository.save(entity);
      progressMonitor.tick();
    }

    progressMonitor.end();
    progressMonitor.println("save ok");
  }

  protected PersonEntity readEntity(SqlRowSet rs) {
    PersonEntity entity = new PersonEntity();

    // Skip migration for: accountExpiry
    entity.setComments(rs.getString("Notes"));
    entity.setCurrentCapacity(rs.getString("CurrentCapacity"));
    entity.setDocType("Person");
    entity.setEmailAddress(rs.getString("EmailAddress"));
    entity.setHasAccount(getBooleanOrNull(rs, "HasAccount"));
    entity.setId(objectIdFactory.create());
    entity.setModifiedByPersonId(migrationUtils.getModifiedByPersonId());
    entity.setModifiedTime(migrationUtils.getModifiedTime());
    entity.setName(rs.getString("Observer"));
    // Skip migration for: personRole
    entity.setPhoneNumber(rs.getString("PhoneNumber"));
    // Skip migration for: revision
    entity.setUserName(rs.getString("Username"));

    return entity;
  }

}



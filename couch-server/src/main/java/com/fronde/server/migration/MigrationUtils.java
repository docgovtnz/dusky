package com.fronde.server.migration;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;

import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.Statement;
import com.fronde.server.domain.BaseEntity;
import com.fronde.server.domain.HarnessChangeEntity;
import com.fronde.server.domain.TransmitterChangeEntity;
import com.fronde.server.services.common.ObjectIdFactory;
import com.fronde.server.services.revision.RevisionService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class MigrationUtils {

  private Map<String, String> sexMap;
  private Map<Integer, String> plumageMap;
  private Map<Integer, String> legColourMap;
  private Map<String, String> birdIDMap;
  private Map<String, String> locationNameMap;
  private Map<String, String> txIdMap;
  private Map<Integer, String> txMortIdMap;
  private Map<String, String> personNameMap;
  private Map<String, String> recordIDMap;
  private Map<String, String> tAndBRecIdMap;

  @Autowired
  protected RevisionService revisionService;

  @Autowired
  protected CouchbaseTemplate template;

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  public void createRevision(BaseEntity entity) {
    revisionService.createRevision(entity);
  }

  public Map<String, String> getSexMap() {
    if (sexMap == null) {
      sexMap = new HashMap();
      sexMap.put("1", "Male");
      sexMap.put("2", "Female");
      sexMap.put("3", "Unknown");
    }
    return sexMap;
  }

  public Map<Integer, String> getPlumageMap() {
    if (plumageMap == null) {
      plumageMap = new HashMap<>();
      plumageMap.put(1, "Green");
      plumageMap.put(2, "Olive");
      plumageMap.put(3, "Grey-green");
    }
    return plumageMap;
  }

  public Map<Integer, String> getLegColourMap() {
    if (legColourMap == null) {
      legColourMap = new HashMap<Integer, String>();
      legColourMap.put(1, "Dark grey");
      legColourMap.put(2, "Medium grey");
      legColourMap.put(3, "Pale grey");
    }
    return legColourMap;
  }

  public Map<String, String> getBirdIDMap() {
    if (birdIDMap == null) {
      birdIDMap = new HashMap<>();
    }
    return birdIDMap;
  }

  public Map<String, String> getLocationNameMap() {
    if (locationNameMap == null) {
      locationNameMap = new HashMap<>();
    }
    return locationNameMap;
  }

  public Map<String, String> getTxIdMap() {
    if (txIdMap == null) {
      txIdMap = new HashMap<>();
    }
    return txIdMap;
  }

  public Map<Integer, String> getTxMortIdMap() {
    if (txMortIdMap == null) {
      txMortIdMap = new HashMap<>();
    }
    return txMortIdMap;
  }

  public Map<String, String> getPersonNameMap() {
    if (personNameMap == null) {
      personNameMap = new HashMap<>();
    }
    return personNameMap;
  }

  public Map<String, String> getRecordIDMap() {
    if (recordIDMap == null) {
      recordIDMap = new HashMap<>();
    }
    return recordIDMap;
  }

  public Map<String, String> getTAndBRecIdMap() {
    if (tAndBRecIdMap == null) {
      tAndBRecIdMap = new HashMap<>();
    }
    return tAndBRecIdMap;
  }

  public Date getModifiedTime() {
    return new Date();
  }

  public String getModifiedByPersonId() {
    return "Data Migration";
  }

  public String convertSex(String value) {
    return getSexMap().get(value);
  }

  public String convertPlumageColour(Integer value) {
    return getPlumageMap().get(value);
  }

  public String convertLegColour(Integer value) {
    return getLegColourMap().get(value);
  }

  /**
   * Every request for a BirdID is guaranteed to get one, even if the bird hasn't been created yet.
   * When Birds are created then they must use this method to get their ID rather than creating a
   * fresh random one, so BirdMigrator overrides the parent class behaviour.
   *
   * @param birdID
   * @return
   */
  public String convertBirdIDForBirds(String birdID) {
    if (birdID != null) {
      String id = getBirdIDMap().get(birdID);
      if (id == null) {
        id = objectIdFactory.create();
        getBirdIDMap().put(birdID, id);
      }
      return id;
    } else {
      //throw new NullPointerException("BirdID is null");
      return null;
    }
  }

  public String convertBirdID(String birdID) {
    if (birdID != null) {
      String id = getBirdIDMap().get(birdID);
      // Need to think about this situation - it's the Unknown bird problem, this is an BirdID for a Bird
      // that doesn't exist.
      //throw new RuntimeException("Migration failure: birdID does not exist - " + birdID);
      return id;
    } else {
      return null;
    }
  }

  /**
   * Every request for a LocationName is guaranteed to get one, even if the location hasn't been
   * created yet. When Locations are created then they must use this method to get their ID rather
   * than creating a fresh random one, so LocationMigrator overrides the parent class behaviour.
   *
   * @param locationName
   * @return
   */
  public String convertLocationNameForLocations(String locationName) {
    if (locationName != null) {
      String id = getLocationNameMap().get(locationName);
      if (id == null) {
        id = objectIdFactory.create();
        getLocationNameMap().put(locationName, id);
      }
      return id;
    } else {
      //throw new NullPointerException("LocationName is null");
      return null;
    }
  }

  public String convertLocationName(String locationName) {
    if (locationName != null) {
      String id = getLocationNameMap().get(locationName);
      // Need to think about this situation - it's the Unknown location problem, this is an LocationName for a Location
      // that doesn't exist.
      //throw new RuntimeException("Migration failure: locationName does not exist - " + locationName);
      return id;
    } else {
      return null;
    }
  }

  public String convertTxIdForTransmitters(String txId) {
    if (txId != null) {
      String id = getTxIdMap().get(txId);
      if (id == null) {
        id = objectIdFactory.create();
        getTxIdMap().put(txId, id);
      }
      return id;
    } else {
      return null;
    }
  }

  public String convertTxId(String txId) {
    if (txId != null) {
      String id = getTxIdMap().get(txId);
      // Need to think about this situation - it's the Unknown transmitter problem, this is an txId for a Transmitter
      // that doesn't exist.
      //throw new RuntimeException("Migration failure: txId does not exist - " + txId);
      return id;
    } else {
      return null;
    }
  }

  public String convertTxMortIdForTxMortalities(Integer txMortId) {
    if (txMortId != null) {
      String id = getTxMortIdMap().get(txMortId);
      if (id == null) {
        id = objectIdFactory.create();
        getTxMortIdMap().put(txMortId, id);
      }
      return id;
    } else {
      return null;
    }
  }

  public String convertTxMortId(Integer txMortId) {
    if (txMortId != null) {
      String id = getTxMortIdMap().get(txMortId);
      // Need to think about this situation - it's the Unknown txMortality problem, this is an txMortId for a TxMortality
      // that doesn't exist.
      //throw new RuntimeException("Migration failure: txId does not exist - " + txId);
      return id;
    } else {
      return null;
    }
  }

  /**
   * returns, and if needed creates, a unique id for the person name. Case insensitive
   */
  public String convertPersonNameForPersons(String personName) {
    personName = personName.toLowerCase();
    if (personName != null) {
      String id = getPersonNameMap().get(personName);
      if (id == null) {
        id = objectIdFactory.create();
        getPersonNameMap().put(personName, id);
      } else {
        throw new RuntimeException("Duplicate person found for person name of " + personName);
      }
      return id;
    } else {
      return null;
    }
  }

  /**
   * returns the unique id for the person name. Case insensitive
   */
  public String convertPersonName(String personName) {
    personName = personName.toLowerCase();
    if (personName != null) {
      String id = getPersonNameMap().get(personName);
      // Need to think about this situation - it's the Unknown Person problem, this is a personName for a Person
      // that doesn't exist.
      //throw new RuntimeException("Migration failure: txId does not exist - " + txId);
      return id;
    } else {
      return null;
    }
  }

  /**
   * Every request for a RecordID is guaranteed to get one, even if the record hasn't been created
   * yet. When Records are created then they must use this method to get their ID rather than
   * creating a fresh random one, so RecordMigrator overrides the parent class behaviour.
   *
   * @param recordID
   * @return
   */
  public String convertRecordIDForRecords(String recordID) {
    if (recordID != null) {
      String id = getRecordIDMap().get(recordID);
      if (id == null) {
        id = objectIdFactory.create();
        getRecordIDMap().put(recordID, id);
      }
      return id;
    } else {
      //throw new NullPointerException("RecordID is null");
      return null;
    }
  }

  public String convertRecordID(String recordID) {
    if (recordID != null) {
      String id = getRecordIDMap().get(recordID);
      // Need to think about this situation - it's the Unknown record problem, this is an RecordID for a Record
      // that doesn't exist.
      //throw new RuntimeException("Migration failure: recordID does not exist - " + recordID);
      return id;
    } else {
      return null;
    }
  }

  /**
   * Every request for a TAndBRecId is guaranteed to get one, even if the record hasn't been created
   * yet. When SnarkActivities are created then they must use this method to get their ID rather
   * than creating a fresh random one, so SnarkActivityMigrator overrides the parent class
   * behaviour.
   *
   * @param tAndBRecId
   * @return
   */
  public String convertTAndBRecIdForSnarkActivities(String tAndBRecId) {
    if (tAndBRecId != null) {
      String id = getTAndBRecIdMap().get(tAndBRecId);
      if (id == null) {
        id = objectIdFactory.create();
        getTAndBRecIdMap().put(tAndBRecId, id);
      }
      return id;
    } else {
      //throw new NullPointerException("TAndBRecId is null");
      return null;
    }
  }

  public String convertTAndBRecId(String tAndBRecId) {
    if (tAndBRecId != null) {
      String id = getTAndBRecIdMap().get(tAndBRecId);
      // Need to think about this situation - it's the Unknown record problem, this is an TAndBRecId for a SnarkActivity
      // that doesn't exist.
      //throw new RuntimeException("Migration failure: tAndBRecId does not exist - " + tAndBRecId);
      return id;
    } else {
      return null;
    }
  }

  /**
   * A utility method that can be used during development to test migration of a single entity only.
   * Without this, birds need to be migrated so that the birdIdMap is populated
   */
  public void populateBirdIDMap() {
    Statement s = select(x("meta().id"), x("oldBirdID"))
        .from(i(template.getCouchbaseBucket().name())).where(i("docType").eq(s("Bird")));

    N1qlQuery query = N1qlQuery.simple(s);
    template.queryN1QL(query).forEach(result -> {
      getBirdIDMap().put(result.value().getString("oldBirdID"), result.value().getString("id"));
    });
  }

  /**
   * A utility method that can be used during development to test migration of a single entity only.
   * Without this, locations need to be migrated so that the locationNameMap is populated
   */
  public void populateLocationNameMap() {
    Statement s = select(x("meta().id"), x("locationName"))
        .from(i(template.getCouchbaseBucket().name())).where(i("docType").eq(s("Location")));

    N1qlQuery query = N1qlQuery.simple(s);
    template.queryN1QL(query).forEach(result -> {
      getLocationNameMap()
          .put(result.value().getString("locationName"), result.value().getString("id"));
    });
  }

  /**
   * A utility method that can be used during development to test migration of a single entity only.
   * Without this, transmitters need to be migrated so that the txIdMap is populated
   */
  public void populateTxIdMap() {
    Statement s = select(x("meta().id"), x("txId")).from(i(template.getCouchbaseBucket().name()))
        .where(i("docType").eq(s("Transmitter")));

    N1qlQuery query = N1qlQuery.simple(s);
    template.queryN1QL(query).forEach(result -> {
      getTxIdMap().put(result.value().getString("txId"), result.value().getString("id"));
    });
  }

  /**
   * A utility method that can be used during development to test migration of a single entity only.
   * Without this, tx mortalities need to be migrated so that the txMortIdMap is populated
   */
  public void populateTxMortIdMap() {
    Statement s = select(x("meta().id"), x("oldTxMortId"))
        .from(i(template.getCouchbaseBucket().name())).where(i("docType").eq(s("TxMortality")));

    N1qlQuery query = N1qlQuery.simple(s);
    template.queryN1QL(query).forEach(result -> {
      getTxMortIdMap().put(result.value().getInt("oldTxMortId"), result.value().getString("id"));
    });
  }

  /**
   * A utility method that can be used during development to test migration of a single entity only.
   * Without this, persons need to be migrated so that the personNameMap is populated
   */
  public void populatePersonNameMap() {
    Statement s = select(x("meta().id"), x("name")).from(i(template.getCouchbaseBucket().name()))
        .where(i("docType").eq(s("Person")));

    N1qlQuery query = N1qlQuery.simple(s);
    template.queryN1QL(query).forEach(result -> {
      getPersonNameMap()
          .put(result.value().getString("name").toLowerCase(), result.value().getString("id"));
    });
  }

  /**
   * A utility method that can be used during development to test migration of a single entity only.
   * Without this, records need to be migrated so that the recordIdMap is populated
   */
  public void populateRecordIDMap() {
    Statement s = select(x("meta().id"), x("recordID"))
        .from(i(template.getCouchbaseBucket().name())).where(i("docType").eq(s("Record")));

    N1qlQuery query = N1qlQuery.simple(s);
    template.queryN1QL(query).forEach(result -> {
      getRecordIDMap().put(result.value().getString("oldRecordID"), result.value().getString("id"));
    });
  }

  /**
   * A utility method that can be used during development to test migration of a single entity only.
   * Without this, snarkActivitys need to be migrated so that the snarkActivityIdMap is populated
   */
  public void populateTAndBRecIdMap() {
    Statement s = select(x("meta().id"), x("tandBrecid"))
        .from(i(template.getCouchbaseBucket().name())).where(i("docType").eq(s("SnarkActivity")));

    N1qlQuery query = N1qlQuery.simple(s);
    template.queryN1QL(query).forEach(result -> {
      getTAndBRecIdMap()
          .put(result.value().getString("tandBrecid"), result.value().getString("id"));
    });
  }

  public static <T> T optionalResult(@NotNull List<T> list) {
    if (list.size() == 0) {
      return null;
    } else if (list.size() == 1) {
      return list.get(0);
    } else {
      throw new MigrationException(
          "OneToOne result set had more than one result for class " + list.get(0).getClass()
              .getName());
    }
  }

  public static Integer getIntOrNull(ResultSet rs, String columnName) throws SQLException {
    Integer value = rs.getInt(columnName);
    if (rs.wasNull()) {
      return null;
    } else {
      return value;
    }
  }

  public static Boolean getBooleanOrNull(ResultSet rs, String columnName) throws SQLException {
    Boolean value = rs.getBoolean(columnName);
    if (rs.wasNull()) {
      return null;
    } else {
      return value;
    }
  }

  public static Float getFloatOrNull(ResultSet rs, String columnName) throws SQLException {
    Float value = rs.getFloat(columnName);
    if (rs.wasNull()) {
      return null;
    } else {
      return value;
    }
  }

  public static Integer getIntOrNull(SqlRowSet rs, String columnName) {
    Integer value = rs.getInt(columnName);
    if (rs.wasNull()) {
      return null;
    } else {
      return value;
    }
  }

  public static Boolean getBooleanOrNull(SqlRowSet rs, String columnName) {
    Boolean value = rs.getBoolean(columnName);
    if (rs.wasNull()) {
      return null;
    } else {
      return value;
    }
  }

  public static Float getFloatOrNull(SqlRowSet rs, String columnName) {
    Float value = rs.getFloat(columnName);
    if (rs.wasNull()) {
      return null;
    } else {
      return value;
    }
  }

  public static TransmitterChangeEntity getTransmitterChangeForRecord(
      List<TransmitterChangeEntity> list) {
    // Get removed transmitter TxChange data.
    TransmitterChangeEntity txcFrom = list.stream().filter(txc -> {
      if (txc.getNewStatus() != null) {
        return "Removed".equalsIgnoreCase(txc.getNewStatus());
      }
      return false;
    }).findAny().orElse(null);
    // Get added transmitter TxChange data.
    TransmitterChangeEntity txcTo = list.stream().filter(txc -> {
      if (txc.getNewStatus() != null) {
        return !"Removed".equalsIgnoreCase(txc.getNewStatus());
      }
      return false;
    }).findAny().orElse(null);

    // Set txcTo record as primary
    if (txcTo != null) {
      txcTo.setTxTo(txcTo.getTxId());

      if (txcFrom != null) {
        txcTo.setTxFrom(txcFrom.getTxId());
        txcTo.setTxFromStatus(txcFrom.getTxFromStatus());
      }
      return txcTo;
    }
    if (txcFrom != null) {
      txcFrom.setTxFrom(txcFrom.getTxId());
      return txcFrom;
    }
    return null;
  }

  public static HarnessChangeEntity getHarnessChangeForRecord(List<HarnessChangeEntity> list) {
    // Get removed transmitter TxChange data.
    HarnessChangeEntity actual = list.stream().filter(hc -> {
      if (hc.getTxtranstype() != null) {
        return hc.getTxtranstype() != null && hc.getTxtranstype().contains("Deployed");
      }
      return false;
    }).findAny().orElse(null);

    if (actual != null && (actual.getNewHarnessLength() != null
        || actual.getOldHarnessLengthRight() != null || actual.getOldHarnessLengthLeft() != null)) {
      return actual;
    } else {
      return null;
    }
  }

}

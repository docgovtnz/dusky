package com.fronde.server.services.snarkimport.processor;

import static com.fronde.server.services.snarkimport.reader.SnarkFileRecord.RECORD_TYPE_ARRIVAL;
import static com.fronde.server.services.snarkimport.reader.SnarkFileRecord.RECORD_TYPE_DEPARTURE;
import static com.fronde.server.services.snarkimport.reader.SnarkFileRecord.RECORD_TYPE_LOCK;
import static com.fronde.server.services.snarkimport.reader.SnarkFileRecord.RECORD_TYPE_TIME;
import static com.fronde.server.services.snarkimport.reader.SnarkFileRecord.RECORD_TYPE_WEIGHT;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.SnarkRecordEntity;
import com.fronde.server.services.snarkimport.EveningDTO;
import com.fronde.server.services.snarkimport.MysteryWeightDTO;
import com.fronde.server.services.snarkimport.reader.SnarkFileBirdRecord;
import com.fronde.server.services.snarkimport.reader.SnarkFileLockRecord;
import com.fronde.server.services.snarkimport.reader.SnarkFileReader;
import com.fronde.server.services.snarkimport.reader.SnarkFileRecord;
import com.fronde.server.services.snarkimport.reader.SnarkFileTimeRecord;
import com.fronde.server.services.snarkimport.reader.SnarkFileTimedRecord;
import com.fronde.server.services.snarkimport.reader.SnarkFileWeightRecord;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 2.0 - Refactored to accommodate KD-605, KD-634 and KD-635
 * @date 03/07/2021
 */
public class SnarkFileProcessor {

  public static final String UNIQUE_BIRD_NOT_FOUND = "Unique Bird not found";
  public static final String MORE_BIRDS_FOUND = "More birds found on the same UHF ID";
  private static final Logger logger = LoggerFactory.getLogger(SnarkFileProcessor.class);
  public static String AGGREGATION_METHOD_MEDIAN = "MEDIAN";
  public static String AGGREGATION_METHOD_AVERAGE = "AVERAGE";
  private LocalTime eveningCutoff;
  private Duration absentThreshold;
  private Duration presentThreshold;
  private Duration visitSelectionThreshold;
  private Integer averageWeightSampleLimit;
  private Integer minimumWeightQualityThreshold;
  private Integer weightToKgRatio;
  private Integer readerYearOffset;
  private String weightAggregationMethod;
  private BirdIdConverter converter;

  public SnarkFileProcessResult processFile(String island, byte[] snarkFileData,
      boolean showLockRecords) {
    return processFile(island, new ByteArrayInputStream(snarkFileData), showLockRecords);
  }

  public SnarkFileProcessResult processFile(String island, InputStream stream, boolean showLockRecords) {
    try (SnarkFileReader reader = new SnarkFileReader(readerYearOffset, stream)) {
      return processFile(island, reader.readData(), showLockRecords);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public SnarkFileProcessResult processFile(String island, List<SnarkFileRecord> records, boolean showLockRecords) {
    return processContextualisedRecords(island, toContextualised(records), showLockRecords);
  }

  private SnarkFileProcessResult processContextualisedRecords(String island,
      List<ContextualisedSfr> records, boolean showLockRecords) {
    SnarkFileProcessResult result = new SnarkFileProcessResult();
    result.setShowLockRecords(showLockRecords);
    addSnarkRecords(island, result, records);
    addMysteryWeights(result, records);
    return result;
  }

  private void addMysteryWeights(SnarkFileProcessResult result, List<ContextualisedSfr> records) {
    List<MysteryWeightDTO> mysteryWeightDtos = getMysteryWeights(records);
    result.setMysteryWeightList(mysteryWeightDtos);
  }

  private List<MysteryWeightDTO> getMysteryWeights(List<ContextualisedSfr> records) {
    List<MysteryWeightDTO> list = new ArrayList<>();
    for (ContextualisedSfr record : records) {
      if (record.getRecord().getRecordType().equals(RECORD_TYPE_WEIGHT)) {
        if (record.getPresentBirds().size() == 0) {
          SnarkFileWeightRecord weightRecord = (SnarkFileWeightRecord) record.getRecord();
          int quality = weightRecord.getQuality();
          if (quality >= minimumWeightQualityThreshold) {
            Float weightInKgs = ((float) weightRecord.getWeight()) / weightToKgRatio;
            MysteryWeightDTO mw = new MysteryWeightDTO();
            mw.setDateTime(toDate(record.getDateTime()));
            mw.setWeight(weightInKgs);
            list.add(mw);
          }
        }
      }
    }
    return list;
  }

  /**
   * KD-605, KD-634, KD-635
   * <ol>
   *   <li>show all visits from a snark file</li>
   *   <li>apply auto-selection rules and weight selection rules</li>
   * <ol/>
   *
   * @param island  island
   * @param result  results to be displayed on the Snark Import screen
   * @param records raw snark file records
   */
  private void addSnarkRecords(String island, SnarkFileProcessResult result,
      List<ContextualisedSfr> records) {

    Map<Date, List<SnarkRecordEntity>> recordsByDate = new HashMap<>(records.size(), 1f);
    Map<LocalDate, List<ContextualisedSfr>> byEvening = getByEvening(records);
    List<LocalDate> evenings = new ArrayList<>(byEvening.keySet());
    Collections.sort(evenings);

    for (LocalDate evening : evenings) {
      // add all snark records to result
      List<ContextualisedSfr> eveningRecords = byEvening.get(evening);
      addAllSnarkRecords(eveningRecords, evening, island, recordsByDate,
          result.isShowLockRecords());

      // process records to apply auto-selection and weight selection rules
      List<SnarkRecordEntity> snarkList = recordsByDate.get(toDate(evening));
      Optional.ofNullable(snarkList)
          .ifPresent(list -> processRecordsWithRules(list, toDate(evening), result));
    }
  }

  /**
   * KD-605, KD634 add all snark records (Arrival, Departure, Weight) from a snark file
   *  @param eveningRecords        snark records on one evening
   * @param evening               evening date
   * @param island                island name
   * @param snarkRecordsByEvening return a Map (key->Date, value->A list of Snark record entities)
   * @param showLockRecords
   */
  private void addAllSnarkRecords(List<ContextualisedSfr> eveningRecords, LocalDate evening,
      String island, Map<Date, List<SnarkRecordEntity>> snarkRecordsByEvening,
      Boolean showLockRecords) {

    for (ContextualisedSfr sfr : eveningRecords) {
      String recordType = sfr.getRecord().getRecordType();
      if (RECORD_TYPE_ARRIVAL.equals(recordType) || RECORD_TYPE_DEPARTURE.equals(recordType)
          || RECORD_TYPE_WEIGHT.equals(recordType)
          || (showLockRecords && RECORD_TYPE_LOCK.equals(recordType))) {

        Date eveningAsDate = toDate(evening);
        SnarkRecordEntity record = toSnarkRecord(island, sfr);

        if (!snarkRecordsByEvening.containsKey(eveningAsDate)) {
          snarkRecordsByEvening.put(eveningAsDate, new ArrayList<>());
        }
        snarkRecordsByEvening.get(eveningAsDate).add(record);
      }
    }
  }

  /**
   * <h1>KD-605, KD-634 Process all snark records with auto-selection and weight selection
   * rules</h1>
   * <h2>Weight records rules</h2>
   * <ol>
   *   <li>Eliminate weights below threshold.</li>
   *   <li>Deselect weights that aren't the first for each bird each night.</li>
   *   <li>Deselect weights recorded when more than 1 birds are present.</li>
   *   <li>Deselect weights recorded when 0 birds are present.</li>
   * <ol/>
   * <h2>Visit (Arrival & Departure) records rules</h2>
   * <ol>
   *   <li>Always include a bird's first arrival/departure for the evening</li>
   *   <li>Always include a bird's last arrival/departure for the evening</li>
   *   <li>Always include arrival/departure blocks >= 5 minutes for a bird</li>
   *   <li>Deselect visits of duration less than 5 minutes, EXCEPT if:</li>
   *   <ul>
   *     <li>it is that bird’s first visit of the night,</li>
   *     <li>it is that bird’s last visit of the night,</li>
   *     <li>it overlaps with another bird’s visit.</li>
   *   </ul>
   * </ol>
   *
   * @param snarkList snark records to be processed with a set of rules
   * @param evening   evening date
   * @param result    display all records on the Snark Import Summary screen
   */
  private void processRecordsWithRules(List<SnarkRecordEntity> snarkList, Date evening,
      SnarkFileProcessResult result) {
    applyWeightRecordRules(snarkList);
    assignBirdsToWeightRecords(snarkList);
    applyVisitRules(snarkList);
    result.getSnarkRecordsByEvening().put(evening, snarkList);
  }

  /**
   * KD-605: Snark data weight record rules
   * <ol>
   *   <li>Eliminate weights below threshold.</li>
   *   <li>Deselect weights that aren't the first for each bird each night.</li>
   *   <li>Deselect weights recorded when more than 1 birds are present.</li>
   *   <li>Deselect weights recorded when 0 birds are present.</li>
   * <ol/>
   *
   * @param snarkRecList a list of snark records for the night
   */
  private void applyWeightRecordRules(List<SnarkRecordEntity> snarkRecList) {
    // Eliminate weights below threshold
    for (Iterator<SnarkRecordEntity> iterator = snarkRecList.iterator(); iterator.hasNext(); ) {
      SnarkRecordEntity wgtRec = iterator.next();
      if (Objects.equals(RECORD_TYPE_WEIGHT, wgtRec.getRecordType())) {
        if (wgtRec.getWeightQuality() < minimumWeightQualityThreshold) {
          iterator.remove();
        }
      }
    }
    // apply other weight rules
    applyOtherWeightRules(snarkRecList);
  }

  /**
   * KD-605: Snark data other weight rules
   * <li>Deselect weights that aren't the first for each bird each night.</li>
   * <li>Deselect weights recorded when more than 1 birds are present.</li>
   * <li>Deselect weights recorded when 0 birds are present.</li>
   *
   * @param snarkRecList a list of snark records for the night
   */
  private void applyOtherWeightRules(List<SnarkRecordEntity> snarkRecList) {
    Map<String, List<Integer[]>> birdsVisitIndexes = new HashMap<>();
    Map<String, List<Integer>> birdsFirstWeightIndexes = new HashMap<>();

    // prep all bird visits indexes and possible first weight records indexes
    for (String birdId : getFilteredBirdsList(snarkRecList)) {
      List<Integer[]> birdVisits = getBirdVisitBlocks(snarkRecList, birdId);
      if (!birdVisits.isEmpty()) {
        birdsVisitIndexes.put(birdId, birdVisits);
        birdsFirstWeightIndexes
            .put(birdId, getPossibleFirstWeightIndexes(birdVisits, snarkRecList));
      }
    }

    for (String bird : birdsFirstWeightIndexes.keySet()) {
      List<Integer> firstWeightIndexes = birdsFirstWeightIndexes.get(bird);
      List<Integer[]> otherBirdsVisitsIndexes = new ArrayList<>();

      if (!firstWeightIndexes.isEmpty()) {
        for (String otherBirdId : birdsVisitIndexes.keySet()) {
          if (!Objects.equals(otherBirdId, bird)) {
            otherBirdsVisitsIndexes.addAll(birdsVisitIndexes.get(otherBirdId));
          }
        }
        // determine if there are more than one birds are present
        if (!otherBirdsVisitsIndexes.isEmpty()) {
          for (Integer firstWeightIndex : firstWeightIndexes) {
            boolean overlap = false;
            for (Integer[] visitsIndex : otherBirdsVisitsIndexes) {
              if (visitsIndex[0] < firstWeightIndex && firstWeightIndex < visitsIndex[1]) {
                overlap = true;
                break;
              }
            }
            // current bird's weight record does not overlap with the other birds
            if (!overlap && isUniqueBirdFound(bird)) {
              snarkRecList.get(firstWeightIndex).setInclude(true);
              break;
            }
          }
        } else if (isUniqueBirdFound(bird)) {
          // No other birds present, select the first weight record for the bird
          int index = firstWeightIndexes.get(0);
          snarkRecList.get(index).setInclude(true);
        }
      }
    }
  }

  /**
   * KD-635: Allow user to allocate a weight reading to an individual bird when multiple birds are
   * present.
   *
   * @param snarkRecList a list of snark records for the night
   */
  private void assignBirdsToWeightRecords(List<SnarkRecordEntity> snarkRecList) {
    // prep all birds' visits for the night
    Map<String, List<Integer[]>> allBirdsVisits = getAllBirdsVisits(snarkRecList);
    List<String> birdList = getBirdsListWithSameUhfId(snarkRecList);

    for (String currentBird : getFilteredBirdsList(snarkRecList)) {
      // prep current bird's visits and all other birds' visits
      List<Integer[]> currentBirdVisits = getBirdVisitBlocks(snarkRecList, currentBird);
      List<Integer[]> otherBirdsVisitsIndexes = getOtherBirdsVisits(currentBird, allBirdsVisits);

      // retrieve current bird visits && more than one birds are present
      if (!currentBirdVisits.isEmpty() && !otherBirdsVisitsIndexes.isEmpty()) {
        for (Integer[] current : currentBirdVisits) {
          for (Integer[] otherBirds : otherBirdsVisitsIndexes) {
            List<Integer[]> overlapList = new ArrayList<>();
            if (current[0] < otherBirds[0] && otherBirds[0] < current[1]) {
              Integer[] overlap = new Integer[2];
              overlap[0] = otherBirds[0];
              overlap[1] = current[1];
              overlapList.add(overlap);
            }

            if (current[0] < otherBirds[1] && otherBirds[1] < current[1]) {
              Integer[] overlap = new Integer[2];
              overlap[0] = current[0];
              overlap[1] = otherBirds[1];
              overlapList.add(overlap);
            }

            if (!overlapList.isEmpty()) {
              List<Integer> overlapStart = new ArrayList<>();
              List<Integer> overlapEnd = new ArrayList<>();
              for (Integer[] index : overlapList) {
                overlapStart.add(index[0]);
                overlapEnd.add(index[1]);
              }
              overlapStart.sort(Collections.reverseOrder());
              Collections.sort(overlapEnd);

              // locate the overlap (multi birds present) fragment of the snark record list
              List<SnarkRecordEntity> records = snarkRecList
                  .subList(overlapStart.get(0), overlapEnd.get(0) + 1);
              for (SnarkRecordEntity record : records) {
                if (Objects.equals(RECORD_TYPE_WEIGHT, record.getRecordType())) {
                  // allow user to choose a bird against the weight record
                  record.setPossibleBirdList(birdList);
                }
              }
            }
          }
        }
      }
    }
  }

  private Map<String, List<Integer[]>> getAllBirdsVisits(List<SnarkRecordEntity> snarkRecList) {
    Map<String, List<Integer[]>> allBirdsVisits = new HashMap<>();
    for (String birdId : getFilteredBirdsList(snarkRecList)) {
      List<Integer[]> birdVisits = getBirdVisitBlocks(snarkRecList, birdId);
      if (!birdVisits.isEmpty()) {
        allBirdsVisits.put(birdId, birdVisits);
      }
    }
    return allBirdsVisits;
  }

  private List<Integer[]> getOtherBirdsVisits(String currentBird,
      Map<String, List<Integer[]>> allBirdsVisits) {
    List<Integer[]> otherBirdsVisitsIndexes = new ArrayList<>();
    for (String otherBird : allBirdsVisits.keySet()) {
      if (!Objects.equals(otherBird, currentBird)) {
        otherBirdsVisitsIndexes.addAll(allBirdsVisits.get(otherBird));
      }
    }
    return otherBirdsVisitsIndexes;
  }

  /**
   * Retrieve all the visit (Arrival && Departure index) blocks for a bird in the night
   *
   * @param snarkRecList snark records for the night
   * @param birdId       bird ID
   * @return a list of all the visit (Arrival & Departure) indexes for the bird
   */
  private List<Integer[]> getBirdVisitBlocks(List<SnarkRecordEntity> snarkRecList, String birdId) {
    Integer[] index = new Integer[2];
    List<Integer[]> visitBlocks = new ArrayList<>();

    for (int i = 0; i < snarkRecList.size(); i++) {
      SnarkRecordEntity record = snarkRecList.get(i);
      String recordType = record.getRecordType();
      String snarkRecBirdId = record.getBirdID();

      if (Objects.equals(birdId, snarkRecBirdId)) {
        if (RECORD_TYPE_ARRIVAL.equals(recordType)) {
          // Only keep the first arrival if there are multiple arrivals for the same ID
          if (index[0] == null) {
            index[0] = i;
          }
        } else if (RECORD_TYPE_DEPARTURE.equals(recordType)) {
          index[1] = i;
        }

        if (index[0] != null && index[1] != null && (index[1] > index[0])) {
          visitBlocks.add(index);
          index = new Integer[2];
        }
      }
    }

    return visitBlocks;
  }

  /**
   * Retrieve all of the possible first weight record indexes from every visit in the night
   *
   * @param birdVisits   a bird's visits indexes for the night
   * @param snarkRecList a list of snark records for the night
   * @return a list of possible first weight record indexes
   */
  private List<Integer> getPossibleFirstWeightIndexes(List<Integer[]> birdVisits,
      List<SnarkRecordEntity> snarkRecList) {
    List<Integer> possibleFirstWeightIndexes = new ArrayList<>();

    for (Integer[] index : birdVisits) {
      boolean isFirstWeight = false;
      for (int i = index[0]; i <= index[1]; i++) {
        SnarkRecordEntity record = snarkRecList.get(i);
        if (Objects.equals(RECORD_TYPE_WEIGHT, record.getRecordType())) {
          if (!isFirstWeight) {
            isFirstWeight = true;
            possibleFirstWeightIndexes.add(i);
          } else {
            /* possible first weight records could be the ones right after other birds' departures
            if there are more than one birds present */
            SnarkRecordEntity previousRecord = snarkRecList.get(i - 1);
            if (Objects.equals(RECORD_TYPE_DEPARTURE, previousRecord.getRecordType())) {
              possibleFirstWeightIndexes.add(i);
            }
          }
        }
      }
    }

    return possibleFirstWeightIndexes;
  }

  /**
   * KD-605, KD-634: Snark data visit (a pair of Arrival & Departure) processing rules
   * <ul>
   *   <li>Always include a bird's first arrival/departure for the evening</li>
   *   <li>Always include a bird's last arrival/departure for the evening</li>
   *   <li>Always include arrival/departure blocks >= 5 minutes for a bird</li>
   *   <li>Deselect visits of duration less than 5 minutes, EXCEPT if:</li>
   *   <ul>
   *     <li>it is that bird’s first visit of the night,</li>
   *     <li>it is that bird’s last visit of the night,</li>
   *     <li>it overlaps with another bird’s visit.</li>
   *   </ul>
   * </ul>
   *
   * @param snarkRecList a list of snark records for the night
   */
  private void applyVisitRules(List<SnarkRecordEntity> snarkRecList) {
    // prep all birds' visits for the night
    Map<String, List<Integer[]>> allBirdsVisits = getAllBirdsVisits(snarkRecList);

    for (String birdId : getFilteredBirdsList(snarkRecList)) {
      if (isUniqueBirdFound(birdId)) {
        List<Integer[]> birdVisits = getBirdVisitBlocks(snarkRecList, birdId);
        int numOfVisits = birdVisits.size();
        // only one visit for the night, always include it
        if (numOfVisits == 1) {
          Integer[] index = birdVisits.get(0);
          snarkRecList.get(index[0]).setInclude(true);
          snarkRecList.get(index[1]).setInclude(true);
        } else if (numOfVisits == 2) {
          // two visits for the night
          birdVisits.forEach(index -> {
            snarkRecList.get(index[0]).setInclude(true);
            snarkRecList.get(index[1]).setInclude(true);
          });
        } else if (numOfVisits > 2) {
          // more than two visits for the night
          multipleVisits(birdId, snarkRecList, birdVisits, allBirdsVisits);
        }
      }
    }
  }

  /**
   * The Snark data visit rules on multiple visits (more than two visits) of a bird for the night
   * <li>Always select arrival/departure blocks >= 5 minutes for a bird</li>
   * <li>Deselect visits of duration less than 5 minutes, EXCEPT if:</li>
   * <ol>
   *   <li>it is that bird’s first visit of the night,</li>
   *   <li>it is that bird’s last visit of the night,</li>
   *   <li>it overlaps with another bird’s visit.</li>
   * </ol>
   *
   * @param currentBird    the bird with more than two visits for the night
   * @param snarkRecList   snark records for the night
   * @param birdVisits     all visits for the bird
   * @param allBirdsVisits a list of all visits for all of the birds present for the night
   */
  private void multipleVisits(String currentBird, List<SnarkRecordEntity> snarkRecList,
      List<Integer[]> birdVisits, Map<String, List<Integer[]>> allBirdsVisits) {
    // more than two visits for the night, always include the first and the last visit
    Integer[] firstVisit = birdVisits.get(0);
    Integer[] lastVisit = birdVisits.get(birdVisits.size() - 1);
    snarkRecList.get(firstVisit[0]).setInclude(true);
    snarkRecList.get(firstVisit[1]).setInclude(true);
    snarkRecList.get(lastVisit[0]).setInclude(true);
    snarkRecList.get(lastVisit[1]).setInclude(true);

    // prep all other birds' visits
    List<Integer[]> otherBirdsVisitsIndexes = getOtherBirdsVisits(currentBird, allBirdsVisits);

    // other visits except for the first and the last
    for (int i = 1; i < birdVisits.size() - 1; i++) {
      Integer[] visit = birdVisits.get(i);
      SnarkRecordEntity arrivalRecord = snarkRecList.get(visit[0]);
      SnarkRecordEntity departureRecord = snarkRecList.get(visit[1]);
      LocalDateTime arrival = toLocalDateTime(arrivalRecord.getDateTime());
      LocalDateTime departure = toLocalDateTime(departureRecord.getDateTime());
      Duration presentDuration = Duration.between(arrival, departure);

      // always select visits of duration more than 5 minutes
      if (presentDuration.compareTo(visitSelectionThreshold) >= 0) {
        arrivalRecord.setInclude(true);
        departureRecord.setInclude(true);
      } else {
        // visits of duration less than 5 minutes, check for the overlaps with other birds
        if (!otherBirdsVisitsIndexes.isEmpty()) {
          boolean overlap = false;
          Integer[] overlapIndex = new Integer[2];
          for (Integer[] otherBirdVisitIndex : otherBirdsVisitsIndexes) {
            if (otherBirdVisitIndex[0] < visit[0] && visit[0] < otherBirdVisitIndex[1]) {
              overlap = true;
              overlapIndex[0] = otherBirdVisitIndex[0];
              overlapIndex[1] = otherBirdVisitIndex[1];
              break;
            } else if (otherBirdVisitIndex[0] < visit[1] && visit[1] < otherBirdVisitIndex[1]) {
              overlap = true;
              overlapIndex[0] = otherBirdVisitIndex[0];
              overlapIndex[1] = otherBirdVisitIndex[1];
              break;
            }
          }
          // select visits if current bird's visit overlaps with another bird
          if (overlap) {
            snarkRecList.get(visit[0]).setInclude(true);
            snarkRecList.get(visit[1]).setInclude(true);
            snarkRecList.get(overlapIndex[0]).setInclude(true);
            snarkRecList.get(overlapIndex[1]).setInclude(true);
            break;
          }
        }
      }
    }
  }

  /**
   * KD-605, KD-634: Process chosen records and apply activity rules and display average weight
   * <ol>
   *   <li>visits of less than 10 min can be assigned “Passing by” by default</li>
   *   <li>visits of more than 10 min can be assigned “Feeding“ by default</li>
   * <ol/>
   *
   * @param selectedRecords the selected records
   * @return a list of eveningDTO to be displayed on the Snark Import screen
   */
  public EveningDTO processSelectedRecords(Date evening, List<SnarkRecordEntity> selectedRecords) {
    EveningDTO eveningDTO = new EveningDTO();
    eveningDTO.setDate(evening);
    eveningDTO.setSnarkRecordList(displayProcessedRecords(selectedRecords, evening));

    return eveningDTO;
  }

  private List<SnarkRecordEntity> displayProcessedRecords(List<SnarkRecordEntity> records,
      Date evening) {
    List<SnarkRecordEntity> list = new ArrayList<>();
    Map<String, List<Date[]>> addsByBird = getArrivalDepartureBlocks(records);

    for (String birdId : addsByBird.keySet()) {
      /*Do not process and display the records on the 2nd form of Snark Import screen if,
        1. user has not chosen a bird where there are multiple birds on the same UHF ID, or
        2. "Bird Detected" does not display any bird (UHF ID does not reconcile with any bird)*/
      if (isUniqueBirdFound(birdId) && !MORE_BIRDS_FOUND.equals(birdId)) {
        List<Date[]> eveningDates = addsByBird.get(birdId);
        for (Date[] dates : eveningDates) {
          LocalDateTime dateTime1 = toLocalDateTime(dates[0]);
          LocalDateTime dateTime2 = toLocalDateTime(dates[1]);
          Float weightInKgs;
          boolean belowThreshold = false;

          if (dateTime2 != null) {
            Duration presentDuration = Duration.between(dateTime1, dateTime2);
            belowThreshold = presentDuration.compareTo(presentThreshold) < 0;
          }

          if (AGGREGATION_METHOD_AVERAGE.equals(weightAggregationMethod)) {
            weightInKgs = getAverageWeightInKgs(birdId, records, dateTime1, dateTime2);
          } else if (AGGREGATION_METHOD_MEDIAN.equals(weightAggregationMethod)) {
            weightInKgs = getMedianWeightInKgs(birdId, records, dateTime1, dateTime2);
          } else {
            throw new RuntimeException(
                "Invalid weight aggregation method " + weightAggregationMethod);
          }

          list.add(addProcessedRecord(evening, birdId, dates, belowThreshold, weightInKgs));
        }
      }
    }

    if (!list.isEmpty()) {
      list.sort(Comparator.comparing(SnarkRecordEntity::getArriveDateTime));
    }

    return list;
  }

  private SnarkRecordEntity addProcessedRecord(Date evening, String birdId, Date[] dates,
      boolean belowThreshold, Float weight) {
    SnarkRecordEntity sr = new SnarkRecordEntity();

    // Activity values defined in RecordActivitySelect options list (values stored in database)
    if (belowThreshold) {
      sr.setActivity("Passing By");
    } else {
      sr.setActivity("Feeding");
    }

    sr.setDateTime(evening);
    sr.setBirdID(birdId);
    sr.setArriveDateTime(dates[0]);
    sr.setDepartDateTime(dates[1]);
    sr.setWeight(weight);

    return sr;
  }

  private LocalDateTime toLocalDateTime(Date date) {
    return Optional.ofNullable(date)
        .map(d -> new java.sql.Timestamp(d.getTime()).toLocalDateTime()).orElse(null);
  }

  private Date toDate(LocalDate local) {
    // TODO make timezone configurable as this is all about how to interpret the dates coming from the snark file
    if (local == null) {
      return null;
    }
    return Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  private Date toDate(LocalDateTime local) {
    // TODO make timezone configurable as this is all about how to interpret the dates coming from the snark file
    if (local == null) {
      return null;
    }
    return Date.from(local.atZone(ZoneId.systemDefault()).toInstant());
  }

  /**
   * KD-605, KD-634 Convert ContextualisedSfr to SnarkRecordEntity
   *
   * @param island island location
   * @param sfr    represents each snark record
   * @return SnarkRecordEntity record
   */
  private SnarkRecordEntity toSnarkRecord(String island, ContextualisedSfr sfr) {
    SnarkRecordEntity sr = new SnarkRecordEntity();
    List<String> possibleBirdList = new ArrayList<>();
    String birdId = null;
    Float weightInKgs = null;
    Integer weightQuality = null;
    Integer uhfId = null;
    Integer lockCount = null;

    // get birdID from Snark Arrival/Departure record
    if (sfr.getRecord() instanceof SnarkFileBirdRecord) {
      SnarkFileBirdRecord birdRecord = (SnarkFileBirdRecord) sfr.getRecord();
      uhfId = birdRecord.getUhfId();
      possibleBirdList = toBirdIdList(converter.convertUhfId(island, uhfId));
      int birdList = possibleBirdList.size();

      if (birdList == 0) {
        birdId = UNIQUE_BIRD_NOT_FOUND;
      } else if (birdList == 1) {
        birdId = possibleBirdList.get(0);
      } else {
        birdId = MORE_BIRDS_FOUND;
      }
    }

    // get weight and weight quality from Snark Weight record
    if (sfr.getRecord() instanceof SnarkFileWeightRecord) {
      weightInKgs =
          (float) ((SnarkFileWeightRecord) sfr.getRecord()).getWeight() / weightToKgRatio;
      weightQuality = ((SnarkFileWeightRecord) sfr.getRecord()).getQuality();
    }

    // get count from Lock records
    if (sfr.getRecord() instanceof SnarkFileLockRecord) {
      lockCount = ((SnarkFileLockRecord) sfr.getRecord()).getCount();
    }

    sr.setInclude(false);
    sr.setActivity(null);
    sr.setRecordType(sfr.getRecord().getRecordType());
    sr.setDateTime(toDate(sfr.getDateTime()));
    sr.setBirdID(birdId);
    sr.setPossibleBirdList(possibleBirdList);
    sr.setWeight(weightInKgs);
    sr.setWeightQuality(weightQuality);
    sr.setUhfId(uhfId);
    sr.setBirdCert("Definitely");
    sr.setLockCount(lockCount);

    return sr;
  }

  private List<String> toBirdIdList(List<BirdEntity> birdEntityList) {
    List<String> birdIdList = new ArrayList<>();
    birdEntityList.forEach(bird -> birdIdList.add(bird.getId()));
    return birdIdList;
  }

  private Float getMedianWeightInKgs(String bird, List<SnarkRecordEntity> records,
      LocalDateTime arrival, LocalDateTime departure) {
    List<Float> weights = getGoodWeights(bird, records, arrival, departure);
    Collections.sort(weights);
    float weight;
    if (weights.size() == 0) {
      return null;
    } else if (weights.size() % 2 == 0) {
      // the weights are even
      int highIndex = weights.size() / 2;
      float weightHigh = weights.get(highIndex);
      int lowIndex = weights.size() / 2 - 1;
      float weightLow = weights.get(lowIndex);
      weight = (weightHigh + weightLow) / 2;
    } else {
      int medianIndex = (weights.size() - 1) / 2;
      weight = weights.get(medianIndex);
    }
    return Precision.round(weight, 3);
  }

  private Float getAverageWeightInKgs(String bird, List<SnarkRecordEntity> records,
      LocalDateTime arrival, LocalDateTime departure) {
    List<Float> weights = getGoodWeights(bird, records, arrival, departure);
    if (weights.isEmpty()) {
      return null;
    }
    int count = Math.min(averageWeightSampleLimit, weights.size());
    Float sum = 0f;
    for (int i = 0; i < count; i++) {
      sum += weights.get(i);
    }
    float averageWeight = sum / (Math.min(averageWeightSampleLimit, count));
    return Precision.round(averageWeight, 3);
  }

  private List<Float> getGoodWeights(String bird, List<SnarkRecordEntity> records,
      LocalDateTime arrival, LocalDateTime departure) {
    List<SnarkRecordEntity> weightRecords = getGoodWeightRecords(bird, records, arrival, departure);
    List<Float> weights = new ArrayList<>();
    for (SnarkRecordEntity weightRecord : weightRecords) {
      weights.add(weightRecord.getWeight());
    }
    return weights;
  }

  private List<SnarkRecordEntity> getGoodWeightRecords(String bird, List<SnarkRecordEntity> records,
      LocalDateTime arrival, LocalDateTime departure) {
    List<SnarkRecordEntity> goodWeights = new ArrayList<>();
    for (SnarkRecordEntity record : records) {
      String recType = record.getRecordType();
      LocalDateTime dateTime = toLocalDateTime(record.getDateTime());
      boolean isWeightRecord = Objects.equals(recType, RECORD_TYPE_WEIGHT);
      boolean isOnOrAfterArrival = dateTime.isEqual(arrival) || dateTime.isAfter(arrival);
      boolean isOnOrBeforeDeparture =
          departure == null || dateTime.isEqual(departure) || dateTime.isBefore(departure);

      if (isWeightRecord && isOnOrAfterArrival && isOnOrBeforeDeparture) {
        // User can assign the weight record to a bird if there are more than one bird is present
        boolean isMultiBirdsAndChosen = bird.equals(record.getBirdID());
        boolean isOneBirdWeightRecord = record.getPossibleBirdList().isEmpty();

        if (isOneBirdWeightRecord || isMultiBirdsAndChosen) {
          goodWeights.add(record);
        }
      }
    }
    return goodWeights;
  }

  private List<ContextualisedSfr> toContextualised(List<SnarkFileRecord> records) {
    List<ContextualisedSfr> contextualised = new ArrayList<>();
    for (SnarkFileRecord record : records) {
      ContextualisedSfr contextualisedRecord = new ContextualisedSfr();
      contextualisedRecord.setRecord(record);
      contextualised.add(contextualisedRecord);
    }
    addDateTime(contextualised);
    addPresentBirds(contextualised);
    return contextualised;
  }

  private void addDateTime(List<ContextualisedSfr> records) {
    LocalDateTime lastTimeRecordDateTime = null;
    for (ContextualisedSfr record : records) {
      LocalDateTime localDateTime;
      if (record.getRecord().getRecordType().equals(RECORD_TYPE_TIME)) {
        localDateTime = ((SnarkFileTimeRecord) record.getRecord()).getDateTime();
        lastTimeRecordDateTime = localDateTime;
      } else if (record.getRecord() instanceof SnarkFileTimedRecord) {
        LocalTime localTime = ((SnarkFileTimedRecord) record.getRecord()).getTime();
        localDateTime = lastTimeRecordDateTime.toLocalDate().atTime(localTime);
      } else {
        // assume record gets date and time from time record
        localDateTime = lastTimeRecordDateTime;
      }
      record.setDateTime(localDateTime);
    }
  }

  private void addPresentBirds(List<ContextualisedSfr> records) {
    // TODO confirm assumption that when a bird departs they are not considered in the area anymore
    // TODO confirm assumption that there is no need to account for birds that arrive but don't depart
    // TODO confirm assumption the ignoring an arrival and departure based on the 10 min rules doesn't apply to present birds
    Map<Integer, LocalDateTime> currentPresentBirds = new HashMap<>();
    LocalDateTime lastDateTime = null;
    for (ContextualisedSfr record : records) {
      if (lastDateTime != null && !currentPresentBirds.isEmpty()) {
        LocalDate previousCutoffDate;
        if (record.getDateTime().toLocalTime().isAfter(eveningCutoff)) {
          previousCutoffDate = record.getDateTime().toLocalDate();
        } else {
          previousCutoffDate = record.getDateTime().toLocalDate().minusDays(1);
        }
        LocalDateTime previousCutoff = previousCutoffDate.atTime(eveningCutoff);
        if (!lastDateTime.isAfter(previousCutoff)) {
          // clear out the current present birds
          currentPresentBirds.clear();
        }
      }
      lastDateTime = record.getDateTime();

      if (record.getRecord().getRecordType().equals(RECORD_TYPE_ARRIVAL)) {
        currentPresentBirds
            .put(((SnarkFileBirdRecord) record.getRecord()).getUhfId(), record.getDateTime());
      } else if (record.getRecord().getRecordType().equals(RECORD_TYPE_DEPARTURE)) {
        currentPresentBirds.remove(((SnarkFileBirdRecord) record.getRecord()).getUhfId());
      }

      Set<Integer> presentBirds = new HashSet<>(currentPresentBirds.keySet());
      record.setPresentBirds(presentBirds);
    }
  }

  private Map<LocalDate, List<ContextualisedSfr>> getByEvening(List<ContextualisedSfr> records) {
    Map<LocalDate, List<ContextualisedSfr>> byEvening = new HashMap<>();
    LocalDate currentEvening = null;
    for (ContextualisedSfr record : records) {
      LocalDate evening = getEvening(record.getDateTime());
      if (!evening.equals(currentEvening)) {
        byEvening.put(evening, new ArrayList<>());
        currentEvening = evening;
      }
      byEvening.get(evening).add(record);
    }
    return byEvening;
  }

  private Map<String, List<Date[]>> getArrivalDepartureBlocks(List<SnarkRecordEntity> records) {
    Map<String, List<Date[]>> blocks = new LinkedHashMap<>(records.size(), 1f);
    for (String birdId : getFilteredBirdsList(records)) {
      List<Date[]> birdBlocks = getArrivalDepartureBlocks(birdId, records);
      blocks.put(birdId, birdBlocks);
    }
    return blocks;
  }

  /**
   * Get Arrival and Departure dates blocks for a bird
   *
   * @param birdId  bird ID
   * @param records selected bird list
   * @return Arrival and Departure dates blocks
   */
  private List<Date[]> getArrivalDepartureBlocks(String birdId, List<SnarkRecordEntity> records) {
    List<Date[]> blocks = new ArrayList<>();
    LocalDateTime lastDeparture = null;
    Date[] currentBlock = null;

    for (SnarkRecordEntity record : records) {
      String recType = record.getRecordType();
      String bird = record.getBirdID();

      if (Objects.equals(recType, RECORD_TYPE_ARRIVAL)) {
        if (Objects.equals(bird, birdId)) {
          LocalDateTime arrival = toLocalDateTime(record.getDateTime());
          boolean belowThreshold = false;

          if (lastDeparture != null) {
            Duration absentDuration = Duration.between(lastDeparture, arrival);
            belowThreshold = absentDuration.compareTo(absentThreshold) < 0;
          }

          if (!belowThreshold) {
            // this is a new arrival so add the last block
            if (currentBlock != null) {
              // if departure is null then just set to last departure
              if (currentBlock[1] == null) {
                currentBlock[1] = toDate(lastDeparture);
              }
              blocks.add(currentBlock);
            }
            currentBlock = new Date[2];
            currentBlock[0] = toDate(arrival);
          } else {
            // clear the departure
            if (currentBlock != null) {
              currentBlock[1] = null;
            }
          }
        }
      } else if (Objects.equals(recType, RECORD_TYPE_DEPARTURE)) {
        if (Objects.equals(bird, birdId)) {
          Date departure = record.getDateTime();
          if (currentBlock != null) {
            currentBlock[1] = departure;
            lastDeparture = toLocalDateTime(departure);
          }
        }
      }
    }

    if (currentBlock != null) {
      blocks.add(currentBlock);
    }

    return blocks;
  }

  /**
   * KD-605 Get a birdId list without duplicates from a list of snark records
   *
   * @param records a list of snark records
   * @return a list of birdIds without duplicates
   */
  private List<String> getFilteredBirdsList(List<SnarkRecordEntity> records) {
    List<String> birds = new ArrayList<>();
    for (SnarkRecordEntity record : records) {
      Optional.ofNullable(record.getBirdID()).ifPresent(birds::add);
    }
    return birds.stream().distinct().collect(Collectors.toList());
  }

  /**
   * KD-635 Get a bird list without duplicates. If there are more than one bird on the same UHF ID,
   * then extract the possible IDS as well
   *
   * @param records a list of snark records
   * @return a list of birdIds without duplicates
   */
  private List<String> getBirdsListWithSameUhfId(List<SnarkRecordEntity> records) {
    List<String> birds = new ArrayList<>();
    for (SnarkRecordEntity record : records) {
      String birdId = record.getBirdID();
      if (MORE_BIRDS_FOUND.equals(birdId)) {
        birds.addAll(record.getPossibleBirdList());
      } else {
        birds.add(birdId);
      }
    }
    return birds.stream().distinct().collect(Collectors.toList());
  }

  private LocalDate getEvening(LocalDateTime localDateTime) {
    LocalDate evening;
    if (localDateTime.toLocalTime().isAfter(eveningCutoff)) {
      // date of evening is same day when time is after cut off
      evening = localDateTime.toLocalDate();
    } else {
      // date of evening is day before when time is same or before cut off
      evening = localDateTime.toLocalDate().minus(1, ChronoUnit.DAYS);
    }
    return evening;
  }

  public boolean isUniqueBirdFound(String birdId) {
    return !Objects.equals(UNIQUE_BIRD_NOT_FOUND, birdId);
  }

  public void setAbsentThreshold(Duration absentThreshold) {
    this.absentThreshold = absentThreshold;
  }

  public void setAverageWeightSampleLimit(Integer averageWeightSampleLimit) {
    this.averageWeightSampleLimit = averageWeightSampleLimit;
  }

  public void setConverter(BirdIdConverter converter) {
    this.converter = converter;
  }

  public void setEveningCutoff(LocalTime eveningCutoff) {
    this.eveningCutoff = eveningCutoff;
  }

  public void setMinimumWeightQualityThreshold(Integer minimumWeightQualityThreshold) {
    this.minimumWeightQualityThreshold = minimumWeightQualityThreshold;
  }

  public void setPresentThreshold(Duration presentThreshold) {
    this.presentThreshold = presentThreshold;
  }

  public void setReaderYearOffset(Integer readerYearOffset) {
    this.readerYearOffset = readerYearOffset;
  }

  public void setVisitSelectionThreshold(Duration visitSelectionThreshold) {
    this.visitSelectionThreshold = visitSelectionThreshold;
  }

  public void setWeightAggregationMethod(String weightAggregationMethod) {
    this.weightAggregationMethod = weightAggregationMethod;
  }

  public void setWeightToKgRatio(Integer weightToKgRatio) {
    this.weightToKgRatio = weightToKgRatio;
  }
}

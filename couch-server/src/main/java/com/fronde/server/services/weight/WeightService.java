package com.fronde.server.services.weight;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.EggMeasurementsEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.bird.BirdCriteria;
import com.fronde.server.services.bird.BirdRepository;
import com.fronde.server.services.bird.BirdSearchDTO;
import com.fronde.server.services.record.RecordRepository;
import com.fronde.server.services.setting.SettingService;
import com.fronde.server.services.validation.ValidationMessage;
import com.fronde.server.services.weight.strategy.MinInWindowWeightStrategy;
import com.fronde.server.services.weight.strategy.WeightStrategy;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class WeightService {

  public static String COLOR_MALE_REF_DATA = "rgba(73,185,255,1)";
  public static String COLOR_FEMALE_REF_DATA = "rgba(255,51,153,1)";

  @Autowired
  protected RecordRepository recordRepository;

  @Autowired
  protected BirdRepository birdRepository;

  @Autowired
  protected WeightRepository weightRepository;

  @Autowired
  protected SettingService settingService;

  private Date alignDate(Date dt) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(dt);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTime();
  }

  public List<WeightSummaryDTO> findByBirdID(String birdID, Date fromDate, Date toDate,
      WeightStrategy strategy) {

    // Find the bird so we get the hatch date. There should be a bird at this point; if not let
    // this throw the exception.
    BirdEntity bird = birdRepository.findById(birdID).get();

    // Find the records.
    List<RecordEntity> recordList = recordRepository.findWeightRecordsByBirdID(birdID);

    // Filter the records.
    List<RecordEntity> filteredRecords = recordList
        .stream()
        .filter(record -> record.getDateTime() != null)
        .filter(record -> bird.getDateHatched() == null || !record.getDateTime()
            .before(bird.getDateHatched()))
        .filter(record -> record.getWeight() != null && record.getWeight().getWeight() != null)
        .collect(Collectors.toList());

    // Sort by date/time.
    filteredRecords.sort(Comparator.comparing(RecordEntity::getDateTime));

    // Send to the strategy.
    return strategy.process(bird, filteredRecords);

  }

  public Response<List<EggWeightDTO>> getEggWeights(String birdID) {
    // Retrieve the bird to get the hatch dates. There should be a bird, but let this throw errors if there isn't.
    Optional<BirdEntity> optBird = birdRepository.findById(birdID);

    if (!optBird.isPresent()) {
      return buildResponse("Bird cannot be found");
    }

    BirdEntity birdEntity = optBird.get();

    if (birdEntity.getDateLaid() == null) {
      return buildResponse("Cannot generate egg weight graph for bird without a 'Date Laid' set");
    }

    // Retrieve the egg weights for the bird.
    List<RecordEntity> weightRecords = recordRepository
        .findEggWeights(birdID, birdEntity.getDateLaid(), birdEntity.getDateHatched());

    // Filter to protect against any bad records.
    weightRecords = weightRecords.stream()
        .filter(record -> record.getDateTime() != null && record.getWeight() != null
            && record.getWeight().getWeight() != null)
        .collect(Collectors.toList());

    if (weightRecords.isEmpty()) {
      return buildResponse("There are no Egg weight measurements for this bird");
    }

    List<EggWeightDTO> eggWeights = new LinkedList<>();
    LocalDateTime laidDate = birdEntity.getDateLaid().toInstant()
        .atZone(ZoneId.of("Pacific/Auckland")).toLocalDateTime();

    for (RecordEntity re : weightRecords) {
      EggWeightDTO dto = new EggWeightDTO();
      dto.setRecordID(re.getId());
      dto.setWeightInGrams(re.getWeight().getWeight() * 1000);

      // Calculate age in days (or part thereof).
      LocalDateTime recordDate = re.getDateTime().toInstant().atZone(ZoneId.of("Pacific/Auckland"))
          .toLocalDateTime();
      dto.setAgeInDays(ChronoUnit.MINUTES.between(laidDate, recordDate) / (float) (24 * 60));
      eggWeights.add(dto);
    }

    return new Response(eggWeights);
  }

  public <T> Response<T> buildResponse(String... messages) {
    Response<T> response = new Response();
    for (String message : messages) {
      ValidationMessage validationMessage = new ValidationMessage();
      validationMessage.setMessageText(message);
      response.getMessages().add(validationMessage);
    }
    return response;
  }

  public Response<List<EggReferencePoint>> getEggReferenceData(String birdID) {
    // Retrieve the bird to get the hatch dates. There should be a bird, but let this throw errors if there isn't.
    BirdEntity birdEntity = birdRepository.findById(birdID).get();

    // Use the bird's coefficient if present. Default to the standard setting if not.
    Float coefficient = null;

    EggMeasurementsEntity measurements = birdEntity.getEggMeasurements();

    if(measurements != null && measurements.getFwCoefficientX104() != null) {
      coefficient = birdEntity.getEggMeasurements().getFwCoefficientX104();
    }
    else {
      coefficient = ((Double) settingService.getValue("DEFAULT_FRESH_WEIGHT_COEFFICIENT"))
          .floatValue();
    }

    if (measurements == null || measurements.getEggWidth() == null || measurements.getEggLength() == null) {
      return buildResponse("Egg Width and Length must be set to display the Predicted Weights");
    }

    // Determine the initial weight.
    float calculatedInitialWeight = (float) (coefficient / 10000 * birdEntity.getEggMeasurements().getEggLength() * Math
        .pow(birdEntity.getEggMeasurements().getEggWidth(), 2));

    // Retrieve reference points. These are stores as the cumulative percentage loss by day, so we need to calculate
    // the actual expected weight.
    List<EggReferencePoint> refData = weightRepository.getEggReferenceData().getReferencePoints();

    for (EggReferencePoint point : refData) {
      point.setPredictedWeight(
          calculatedInitialWeight - (point.getPredictedCumulativeWeightLossPercentage() / 100
              * calculatedInitialWeight));
    }

    return new Response(refData);
  }

  public WeightReferenceData getReferenceData() {
    return weightRepository.getReferenceData();
  }


  /**
   * Get the egg weights.
   *
   * @param criteria The bird search criteria.
   * @return The data sets for egg weights.
   */
  public Collection<LegendItem> multiEggWeightGraph(BirdCriteria criteria) {
    List<LegendItem> legendItems = new LinkedList<>();

    // Ensure that the search includes all birds.
    criteria.setPageNumber(0);
    criteria.setPageSize(Integer.MAX_VALUE);

    // Execute the search.
    List<BirdSearchDTO> dtos = this.birdRepository.findSearchDTOByCriteria(criteria).getResults();
    List<String> birdIDs = dtos.stream().map(birdDTO -> birdDTO.getBirdID())
        .collect(Collectors.toList());

    // Get weights;
    Map<String, Dataset> data = this.weightRepository.getEggWeights(birdIDs);

    // Get the reference data.
    for (BirdSearchDTO dto : dtos) {
      LegendItem currentLegendItem = new LegendItem();
      currentLegendItem.setLabel(dto.getBirdName());
      currentLegendItem.setBirdID(dto.getBirdID());

      legendItems.add(currentLegendItem);

      // Add weight data.
      if (data.get(dto.getBirdID()) != null) {
        currentLegendItem.getDatasets().add(data.get(dto.getBirdID()));
        currentLegendItem.setType(currentLegendItem.getType() | 0x01);
      } else {
        // Disable the bird if they have no real weight measurements.
        currentLegendItem.setHidden(true);
      }

      Response<List<EggReferencePoint>> eggRefData = this.getEggReferenceData(dto.getBirdID());
      if (eggRefData.getModel() != null) {
        Dataset ds = new Dataset();
        ds.setLabel(dto.getBirdName() + " (Predicted)");
        ds.setBirdID(dto.getBirdID());
        ds.setBorderDash(new int[]{5, 5});

        for (EggReferencePoint refPoint : eggRefData.getModel()) {
          ds.getData().add(new Point(refPoint.getAgeInDays(), refPoint.getPredictedWeight()));
        }

        // Add this item.
        currentLegendItem.getDatasets().add(ds);
        currentLegendItem.setType(currentLegendItem.getType() | 0x02);

      }
    }

    legendItems.sort(Comparator.comparing(legendItem -> legendItem.getLabel()));

    return legendItems;
  }

  public Collection<LegendItem> multiWeightGraph(BirdCriteria criteria, String type) {
    LinkedList<LegendItem> legendItems = new LinkedList<>();

    // Ensure that the search includes all birds.
    criteria.setPageNumber(0);
    criteria.setPageSize(Integer.MAX_VALUE);

    // Execute the search.
    List<BirdSearchDTO> dtos = this.birdRepository.findSearchDTOByCriteria(criteria).getResults();

    // Extract the IDs.
    List<String> birdIDs = dtos.stream().map(birdDTO -> birdDTO.getBirdID())
        .collect(Collectors.toList());

    // Get weights;
    Map<String, Dataset> data = this.weightRepository.getBirdWeights(birdIDs);

    for (BirdSearchDTO dto : dtos) {
      LegendItem li = new LegendItem();
      li.setLabel(dto.getBirdName());
      li.setBirdID(dto.getBirdID());
      li.setBorderWidth(3);
      Dataset ds = data.get(dto.getBirdID());
      if (ds != null) {
        ds = processDataset(ds, type);
        li.getDatasets().add(ds);
        li.setType(1);
      } else {
        li.setType(0);
      }
      legendItems.add(li);
    }

    legendItems.sort(Comparator.comparing(legendItem -> legendItem.getLabel()));

    // Get the reference data.
    WeightReferenceData refData = this.getReferenceData();

    Map<String, Object> settings = settingService
        .get(List.of("WEIGHT_GRAPH_PC_FROM_MEAN_HIGH", "WEIGHT_GRAPH_PC_FROM_MEAN_LOW"));

    Dataset female = new Dataset();
    female.setLabel("Female Mean");
    female.setData(refData.getFemaleReferenceData());
    female.setBorderColor(COLOR_FEMALE_REF_DATA);
    female.setBorderWidth(1);
    female.setReferenceData(true);
    legendItems.add(0, toLegendItem(female).withReferenceData(true).withType(1));

    legendItems.add(1, toLegendItem(
        generateDataset("Female Mean", (Integer) settings.get("WEIGHT_GRAPH_PC_FROM_MEAN_HIGH"),
            female, COLOR_FEMALE_REF_DATA, new int[]{5, 5}, 1)).withReferenceData(true)
        .withType(1));
    legendItems.add(2, toLegendItem(
        generateDataset("Female Mean", (Integer) settings.get("WEIGHT_GRAPH_PC_FROM_MEAN_LOW"),
            female, COLOR_FEMALE_REF_DATA, new int[]{2, 8}, 1)).withReferenceData(true)
        .withType(1));

    Dataset male = new Dataset();
    male.setLabel("Male Mean");
    male.setData(refData.getMaleReferenceData());
    male.setBorderColor(COLOR_MALE_REF_DATA);
    male.setBorderWidth(1);
    male.setReferenceData(true);
    legendItems.add(3, toLegendItem(male).withReferenceData(true).withType(1));

    legendItems.add(4, toLegendItem(
        generateDataset("Male Mean", (Integer) settings.get("WEIGHT_GRAPH_PC_FROM_MEAN_HIGH"), male,
            COLOR_MALE_REF_DATA, new int[]{5, 5}, 1)).withReferenceData(true).withType(1));
    legendItems.add(5, toLegendItem(
        generateDataset("Male Mean", (Integer) settings.get("WEIGHT_GRAPH_PC_FROM_MEAN_LOW"), male,
            COLOR_MALE_REF_DATA, new int[]{2, 8}, 1)).withReferenceData(true).withType(1));

    return legendItems;
  }

  //todo new method ***** Dataset in LegendItem has PointDate
  public Collection<LegendItem> multiWeightGraphWithDate(BirdCriteria criteria, WeightStrategy strategy)
      throws ParseException {
    LinkedList<LegendItem> legendItems = new LinkedList<>();
    // Ensure that the search includes all birds.
    criteria.setPageNumber(0);
    criteria.setPageSize(Integer.MAX_VALUE);
    // Execute the search.
    List<BirdSearchDTO> dtos = this.birdRepository.findSearchDTOByCriteria(criteria).getResults();
    // Extract the IDs.
    List<String> birdIDs = dtos.stream().map(birdDTO -> birdDTO.getBirdID())
        .collect(Collectors.toList());
    // Get weights;
    Map<String, Dataset> data = this.weightRepository.getBirdWeightsWithDate(birdIDs, strategy);
    for (BirdSearchDTO dto : dtos) {
      //todo set for each bird
      LegendItem li = new LegendItem();
      li.setLabel(dto.getBirdName());
      li.setBirdID(dto.getBirdID());
      li.setBorderWidth(3);
      //todo dataset for each bird
      Dataset ds = data.get(dto.getBirdID());
      if (ds != null) {
        li.getDatasets().add(ds);
        li.setType(1);
      } else {
        li.setType(0);
      }
      legendItems.add(li);
    }

    legendItems.sort(Comparator.comparing(legendItem -> legendItem.getLabel()));

    return legendItems;
  }


  private LegendItem toLegendItem(Dataset ds) {
    LegendItem li = new LegendItem();
    li.setLabel(ds.getLabel());
    li.setBirdID(ds.getBirdID());
    li.setBorderWidth(ds.getBorderWidth());
    li.setBorderDash(ds.getBorderDash());
    li.setBorderColor(ds.getBorderColor());
    li.getDatasets().add(ds);
    return li;
  }

  private Dataset generateDataset(String label, int factor, Dataset sourceData, String color,
      int[] borderStyle, int borderWidth) {
    Dataset ds = new Dataset();
    ds.setLabel(String.format("%s (%d%%)", label, factor));
    float pc = (100 + factor) / 100f;
    ds.setData(
        sourceData.getData().stream().map(point -> new Point(point.getX(), point.getY() * pc))
            .collect(Collectors.toList()));
    ds.setBorderColor(color);
    ds.setBorderDash(borderStyle);
    ds.setBorderWidth(borderWidth);
    ds.setReferenceData(true);
    return ds;
  }

  public Collection<LegendItem> multiWeightDeltaGraphWithDate(BirdCriteria criteria, WeightStrategy strategy)
      throws ParseException {
    LinkedList<LegendItem> legendItems = new LinkedList<>();

    // Ensure that the search includes all birds.
    criteria.setPageNumber(0);
    criteria.setPageSize(Integer.MAX_VALUE);

    // Execute the search.
    List<BirdSearchDTO> dtos = this.birdRepository.findSearchDTOByCriteria(criteria).getResults();

    // Extract the IDs.
    List<String> birdIDs = dtos.stream().map(birdDTO -> birdDTO.getBirdID())
        .collect(Collectors.toList());

    // Get weights with Dates including original data.points
    Map<String, Dataset> data = this.weightRepository.getBirdWeightsWithDate(birdIDs, strategy);

    for (BirdSearchDTO dto : dtos) {
      LegendItem li = new LegendItem();
      li.setLabel(dto.getBirdName());
      li.setBirdID(dto.getBirdID());
      li.setBorderWidth(3);
      //todo set dataset for each bird
      Dataset ds = data.get(dto.getBirdID());
      if (ds != null) {
        Dataset target = new Dataset(ds, false);
        // Create the delta dataset.
        for (int i = 1; i < ds.getData().size(); i++) {
          Point p1 = ds.getData().get(i - 1);
          Point p2 = ds.getData().get(i);
          PointDate pX = ds.getDataDate().get(i);

          float dateDiffDays = p2.getX() - p1.getX();
          if (dateDiffDays > 0) {
            float dailyWeightChange = (p2.getY() - p1.getY()) / dateDiffDays;
            float dailyWeightChangePercentage = dailyWeightChange / p1.getY() * 100;
            target.getDataDate().add(new PointDate(pX.getX(), dailyWeightChangePercentage));
          }
        }
        //todo target should have dates
        li.getDatasets().add(target);
        li.setType(1);
      } else {
        li.setType(0);
      }
      legendItems.add(li);
    }
    legendItems.sort(Comparator.comparing(legendItem -> legendItem.getLabel()));
    return legendItems;
  }

  public Collection<LegendItem> multiWeightDeltaGraph(BirdCriteria criteria, String type) {
    LinkedList<LegendItem> legendItems = new LinkedList<>();

    // Ensure that the search includes all birds.
    criteria.setPageNumber(0);
    criteria.setPageSize(Integer.MAX_VALUE);

    // Execute the search.
    List<BirdSearchDTO> dtos = this.birdRepository.findSearchDTOByCriteria(criteria).getResults();

    // Extract the IDs.
    List<String> birdIDs = dtos.stream().map(birdDTO -> birdDTO.getBirdID())
        .collect(Collectors.toList());

    // Get weights;
    Map<String, Dataset> data = this.weightRepository.getBirdWeights(birdIDs);

    for (BirdSearchDTO dto : dtos) {
      LegendItem li = new LegendItem();
      li.setLabel(dto.getBirdName());
      li.setBirdID(dto.getBirdID());
      li.setBorderWidth(3);
      Dataset ds = data.get(dto.getBirdID());

      if (ds != null) {
        ds = processDataset(ds, type);

        Dataset target = new Dataset(ds, false);

        // Create the delta dataset.
        for (int i = 1; i < ds.getData().size(); i++) {
          Point p1 = ds.getData().get(i - 1);
          Point p2 = ds.getData().get(i);

          float dateDiffDays = p2.getX() - p1.getX();
          if (dateDiffDays > 0) {
            float dailyWeightChange = (p2.getY() - p1.getY()) / dateDiffDays;
            float dailyWeightChangePercentage = dailyWeightChange / p1.getY() * 100;
            target.getData().add(new Point(p2.getX(), dailyWeightChangePercentage));
          }
        }

        li.getDatasets().add(target);
        li.setType(1);
      } else {
        li.setType(0);
      }
      legendItems.add(li);
    }

    legendItems.sort(Comparator.comparing(legendItem -> legendItem.getLabel()));

    // Get the reference data.
    WeightReferenceData refData = this.weightRepository.getReferenceData();

    legendItems.add(avgDataSet("Avg 90%", "rgba(255,255,0,1)", "+1", refData.getFemaleDelta90pc(),
        refData.getMaleDelta90pc()));
    legendItems.add(avgDataSet("Avg 80%", "rgba(255,255,0,0.7)", "+1", refData.getFemaleDelta80pc(),
        refData.getMaleDelta80pc()));
    legendItems.add(avgDataSet("Avg 50%", "rgba(255,255,0,0.4)", "+1", refData.getFemaleDelta50pc(),
        refData.getMaleDelta50pc()));
    legendItems.add(avgDataSet("Avg 20%", "rgba(255,255,0,0.1)", "+1", refData.getFemaleDelta20pc(),
        refData.getMaleDelta20pc()));
    legendItems.add(
        avgDataSet("Avg 10%", "rgba(255,255,255,1)", false, refData.getFemaleDelta10pc(),
            refData.getMaleDelta10pc()));

    return legendItems;
  }

  private LegendItem avgDataSet(String label, String color, Object fill, List<Point> ds1,
      List<Point> ds2) {
    Map<Float, Point> datapoints = new HashMap<>();
    ds1.forEach(p -> datapoints.put(p.getX(), p));

    ds2.forEach(p -> {
      Point current = datapoints.get(p.getX());
      if (current != null) {
        current.setY((current.getY() + p.getY()) / 2f);
      } else {
        datapoints.put(p.getX(), p);
      }
    });

    LinkedList<Point> data = new LinkedList<>(datapoints.values());
    data.sort(Comparator.comparing(Point::getX));

    Dataset ds = new Dataset();
    ds.setLabel(label);
    ds.setReferenceData(true);
    ds.setBorderColor("#000000");
    ds.setFill(fill);
    ds.setBackgroundColor(color);
    ds.setData(data);

    return toLegendItem(ds).withReferenceData(true).withType(3).withBackgroundColor(color)
        .withBorderColor("#000000");
  }



  private Dataset processDataset(Dataset ds, String type) {
    if (type == null || "".equals(type) || "RAW".equals(type)) {
      return ds;
    }
    if ("MIN".equals(type)) {
      Object windowRange = settingService
          .findById(MinInWindowWeightStrategy.WEIGHT_DELTA_ROLLING_MIN_WINDOW_HRS).getValue();

      if (windowRange != null && windowRange instanceof Integer
          && ((Integer) windowRange).intValue() > 0) {
        float windowSizeMillis = ((Integer) windowRange).intValue() * 60 * 60 * 1000;
        TreeMap<Float, Point> weightMap = new TreeMap<>();

        ds.getData().forEach(data -> {
          weightMap.put(data.getX(), new Point(data.getX(), data.getY()));
        });

        Float start = weightMap.firstKey();
        while (start != null) {
          // Get the window across which we want to look for the minimum.
          Float windowEnd = start + (windowSizeMillis / (24 * 60 * 60 * 1000));
          SortedMap<Float, Point> window = weightMap.subMap(start, true, windowEnd, false);

          if (window.size() == 1) {
            // Only one item in the window, we will keep this and move to processing the next entry.
            start = weightMap.higherKey(start);
          } else {
            // Multiple items in the window. We want to keep the minimum record only. The others can be discarded.

            // Find the minimum.
            Point min = window.values().stream().min(Comparator.comparing(Point::getY)).get();

            // Get a list of the items we want to discard.
            List<Point> toDiscard = window.values().stream().filter(v -> v != min)
                .collect(Collectors.toList());

            // Discard the items we don't want.
            toDiscard.forEach(item -> window.remove(item.getX(), item));

            // Resume processing considering the item we kept as the start of the next window.
            start = min.getX();
          }
        }

        List<Point> weightList = weightMap.values().stream().collect(Collectors.toList());
        Dataset resultDataset = new Dataset(ds, false);
        resultDataset.setData(weightList);

        return resultDataset;
      }
    }
    return ds;
  }
}



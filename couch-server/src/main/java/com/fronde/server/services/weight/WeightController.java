package com.fronde.server.services.weight;

import com.fronde.server.domain.response.Response;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import com.fronde.server.services.bird.BirdCriteria;
import com.fronde.server.services.setting.SettingService;
import com.fronde.server.services.weight.strategy.MinInWindowWeightStrategy;
import com.fronde.server.services.weight.strategy.RawWeightStrategy;
import com.fronde.server.services.weight.strategy.WeightStrategy;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@RequestMapping("/api/weight")
public class WeightController {

  @Autowired
  protected WeightService service;

  @Autowired
  protected SettingService settingService;

    @RequestMapping(value = "/byBirdID", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.BIRD_VIEW)
  public List<WeightSummaryDTO> search(
      @RequestParam(name = "strategy", required = false) String strategyName,
      @RequestParam("birdID") String birdID,
      @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fromDate,
      @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date toDate) {
    if (strategyName == null) {
      strategyName = "raw";
    }

    WeightStrategy strategy;
    switch (strategyName) {
      case "min":
        Object windowRange = settingService
            .findById(MinInWindowWeightStrategy.WEIGHT_DELTA_ROLLING_MIN_WINDOW_HRS).getValue();
        if (windowRange != null && windowRange instanceof Integer
            && ((Integer) windowRange).intValue() > 0) {
          strategy = new MinInWindowWeightStrategy(((Integer) windowRange).intValue());
        } else {
          throw new RuntimeException(
              "Unable to create MinInWindowWeightStrategy windowRange not available ");
        }
        break;
      case "raw":
        strategy = new RawWeightStrategy();
        break;
      default:
        throw new RuntimeException("Unknown strategy: " + strategyName);
    }
    return service.findByBirdID(birdID, fromDate, toDate, strategy);
  }

  private WeightStrategy getStrategy(String strategyName){
    WeightStrategy strategy;
    if (strategyName == null) {
      strategyName = "raw";
    }
    strategyName = strategyName.toLowerCase();
    switch (strategyName) {
      case "min":
        Object windowRange = settingService
            .findById(MinInWindowWeightStrategy.WEIGHT_DELTA_ROLLING_MIN_WINDOW_HRS).getValue();
        if (windowRange != null && windowRange instanceof Integer
            && ((Integer) windowRange).intValue() > 0) {
          strategy = new MinInWindowWeightStrategy(((Integer) windowRange).intValue());
        } else {
          throw new RuntimeException(
              "Unable to create MinInWindowWeightStrategy windowRange not available ");
        }
        break;
      case "raw":
        strategy = new RawWeightStrategy();
        break;
      default:
        throw new RuntimeException("Unknown strategy: " + strategyName);
    }
    return strategy;
  }

  @RequestMapping(value = "/eggWeights/{birdID}", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.BIRD_VIEW)
  public Response<List<EggWeightDTO>> getEggWeights(@PathVariable(value = "birdID") String birdID) {
    return service.getEggWeights(birdID);
  }

  @RequestMapping("/eggReferenceData/{birdID}")
  @ResponseBody
  @CheckPermission(Permission.BIRD_VIEW)
  public Response<List<EggReferencePoint>> getEggReferenceData(
      @PathVariable(value = "birdID") String birdID) {
    return service.getEggReferenceData(birdID);
  }


  @RequestMapping("/referenceData")
  @ResponseBody
  @CheckPermission(Permission.BIRD_VIEW)
  public WeightReferenceData getReferenceData() {
    return service.getReferenceData();
  }

  @RequestMapping("/multiWeightGraph")
  @ResponseBody
  @CheckPermission(Permission.BIRD_VIEW)
  public Collection<LegendItem> multiWeightGraph(BirdCriteria criteria, String type) {
    return service.multiWeightGraph(criteria, type);
  }

  //todo return multibird chart dataset with date *********************
  @RequestMapping("/multiWeightGraphWithDate")
  @ResponseBody
  @CheckPermission(Permission.BIRD_VIEW)
  public Collection<LegendItem> multiWeightGraphWithDate(BirdCriteria criteria, String type)
      throws ParseException {
    WeightStrategy strategy = getStrategy(type);
    return service.multiWeightGraphWithDate(criteria, strategy);
  }

  @RequestMapping("/multiWeightDeltaGraphWithDate")
  @ResponseBody
  @CheckPermission(Permission.BIRD_VIEW)
  public Collection<LegendItem> multiWeightDeltaGraphWithDate(BirdCriteria criteria, String type)
      throws ParseException {
    WeightStrategy strategy = getStrategy(type);
      return service.multiWeightDeltaGraphWithDate(criteria, strategy);
  }

  @RequestMapping("/multiEggWeightGraph")
  @ResponseBody
  @CheckPermission(Permission.BIRD_VIEW)
  public Collection<LegendItem> multiEggWeightGraph(BirdCriteria criteria) {
    return service.multiEggWeightGraph(criteria);
  }

  @RequestMapping("/multiWeightDeltaGraph")
  @ResponseBody
  @CheckPermission(Permission.BIRD_VIEW)
  public Collection<LegendItem> multiWeightDeltaGraph(BirdCriteria criteria, String type) {
    return service.multiWeightDeltaGraph(criteria, type);
  }
}

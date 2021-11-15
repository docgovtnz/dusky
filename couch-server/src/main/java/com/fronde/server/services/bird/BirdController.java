package com.fronde.server.services.bird;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.TransmitterEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import com.fronde.server.services.options.BirdSummaryDTO;
import com.fronde.server.services.record.BirdTransmitterHistoryDTO;
import com.fronde.server.services.record.DatedMeasureDetailDTO;
import java.util.Date;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/bird")
public class BirdController extends BirdBaseController {

  @RequestMapping(value = "/searchDTO", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.BIRD_VIEW)
  public PagedResponse<BirdSearchDTO> findSearchDTOByCriteria(@RequestBody BirdCriteria criteria) {
    return service.findSearchDTOByCriteria(criteria);
  }

  @RequestMapping(value = "/summaryDTO", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.BIRD_VIEW)
  public List<BirdSummaryDTO> findSummaryDTOByCriteria(@RequestBody BirdSummaryCriteria criteria) {
    return service.findBirdSummaries(criteria);
  }

  @RequestMapping(value = "/{birdID}/currentMeasureDetail", method = RequestMethod.GET)
  @CheckPermission(Permission.BIRD_VIEW)
  public DatedMeasureDetailDTO getCurrentMeasureDetail(@PathVariable("birdID") String birdID) {
    return service.getCurrentMeasureDetail(birdID);
  }

  @RequestMapping(value = "/{birdID}/measureDetailHistory", method = RequestMethod.GET)
  @CheckPermission(Permission.BIRD_VIEW)
  public List<DatedMeasureDetailDTO> getMeasureDetailHistory(
      @PathVariable("birdID") String birdID) {
    return service.getMeasureDetailHistory(birdID);
  }

  @RequestMapping(value = "/locations", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.BIRD_VIEW)
  public List<BirdLocationDTO> findBirdLocations(@RequestBody BirdLocationCriteria criteria) {
    return service.findBirdLocations(criteria);
  }

  @RequestMapping(value = "/lifestages", method = RequestMethod.POST)
  @ResponseBody
  public List<LifeStageDTO> getLifeStages(@RequestBody String[] birdIDs) {
    return service.getLifeStages(birdIDs);
  }

  @RequestMapping(value = "/birdByTransmitter", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.BIRD_VIEW)
  public BirdEntity findBirdIDByTransmitter(@RequestParam("island") String island,
      @RequestParam("channel") int channel,
      @RequestParam(name = "sex", required = false) String sex) {
    return service.findBirdIDByTransmitter(island, channel, sex);
  }

  @RequestMapping(value = "/{birdID}/currentBand", method = RequestMethod.GET)
  @ResponseBody
  public CurrentBandDTO getCurrentBand(@PathVariable("birdID") String birdID) {
    return service.getCurrentBand(birdID);
  }

  @RequestMapping(value = "/{birdID}/currentChip", method = RequestMethod.GET)
  @ResponseBody
  public CurrentChipDTO getCurrentChip(@PathVariable("birdID") String birdID) {
    return service.getCurrentChip(birdID);
  }

  @RequestMapping(value = "/{birdID}/currentTransmitter", method = RequestMethod.GET)
  @ResponseBody
  public TransmitterEntity getCurrentTransmitter(@PathVariable("birdID") String birdID) {
    return service.getCurrentTransmitter(birdID);
  }

  @RequestMapping(value = "/{birdID}/currentTransmitter/deployedDate", method = RequestMethod.GET)
  @ResponseBody
  public Date getCurrentTransmitterDeployedDate(@PathVariable("birdID") String birdID) {
    return service.getCurrentTransmitterDeployedDate(birdID);
  }

  @RequestMapping(value = "/{birdID}/currentTransmitter/expiryDate", method = RequestMethod.GET)
  @ResponseBody
  public Date getCurrentTransmitterExpiryDate(@PathVariable("birdID") String birdID) {
    return service.getCurrentTransmitterExpiryDate(birdID);
  }

  @RequestMapping(value = "/{birdID}/transmitterHistory", method = RequestMethod.GET)
  @ResponseBody
  public List<BirdTransmitterHistoryDTO> getTransmitterChangeHistory(
      @PathVariable("birdID") String birdID) {
    return service.getTransmitterHistory(birdID);
  }

  @RequestMapping(value = "/{birdID}/ageClass", method = RequestMethod.GET)
  @ResponseBody
  public String getAgeClass(@PathVariable("birdID") String birdID) {
    return service.getAgeClass(birdID);
  }

  @RequestMapping(value = "/{birdID}/milestone", method = RequestMethod.GET)
  @ResponseBody
  public String getMilestone(@PathVariable("birdID") String birdID) {
    return service.getMilestone(birdID);
  }

  @RequestMapping(value = "/{birdID}/mortality", method = RequestMethod.GET)
  @ResponseBody
  public String getMortality(@PathVariable("birdID") String birdID) {
    return service.getMortality(birdID);
  }

  @RequestMapping(value = "/{birdID}/ageInDays", method = RequestMethod.GET)
  @ResponseBody
  public Integer getAgeInDays(@PathVariable("birdID") String birdID) {
    return service.getAgeInDays(birdID);
  }

  @RequestMapping(value = "/{birdID}/name", method = RequestMethod.GET)
  @ResponseBody
  public String getName(@PathVariable("birdID") String birdID) {
    return service.getName(birdID);
  }

  @RequestMapping(value = "/{birdID}/lifestage", method = RequestMethod.GET)
  @ResponseBody
  public LifeStageDTO getLifeStage(@PathVariable("birdID") String birdID) {
    return service.getLifeStage(birdID);
  }

}

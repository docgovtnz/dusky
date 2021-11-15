package com.fronde.server.services.record;

import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import com.fronde.server.services.options.BandsAndChipsDTO;
import com.fronde.server.services.options.CurrentTransmitterInfoDTO;
import com.fronde.server.services.options.TransmitterBirdHistoryDTO2;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/record")
public class RecordController extends RecordBaseController {

  @RequestMapping(value = "/searchDTO", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.RECORD_VIEW)
  public PagedResponse<RecordSearchDTO> findSearchDTOByCriteria(
      @RequestBody RecordCriteria criteria) {
    return service.findSearchDTOByCriteria(criteria);
  }

  @RequestMapping(value = "/identification", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.RECORD_VIEW)
  public BandsAndChipsDTO findIdInfoByBirdId(@RequestBody String birdId) {
    return service.findIdInfoByBirdId(birdId);
  }

  @RequestMapping(value = "/bandhistory", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.RECORD_VIEW)
  public List<BandsAndChipsDTO> findBandHistoryByBirdId(@RequestBody String birdId) {
    return service.findBandHistoryByBirdId(birdId);
  }

  @RequestMapping(value = "/chiphistory", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.RECORD_VIEW)
  public List<BandsAndChipsDTO> findChipHistoryByBirdId(@RequestBody String birdId) {
    return service.findChipHistoryByBirdId(birdId);
  }

  @RequestMapping(value = "/currentTransmitter", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.RECORD_VIEW)
  public CurrentTransmitterInfoDTO findCurrentTransmitterInfoByBirdId(@RequestBody String birdId) {
    return service.findCurrentTransmitterInfoByBirdId(birdId);
  }

  @RequestMapping(value = "/transmitterBirdHistory", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.RECORD_VIEW)
  public List<TransmitterBirdHistoryDTO2> getTransmitterBirdHistory(@RequestBody String txID) {
    return service.getTransmitterBirdHistory(txID);
  }
}

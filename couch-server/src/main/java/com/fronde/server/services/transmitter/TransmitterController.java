package com.fronde.server.services.transmitter;

import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import com.fronde.server.services.options.TransmitterDTO;
import com.fronde.server.services.options.TransmitterSearchDTO;
import com.fronde.server.services.record.TransmitterBirdHistoryDTO;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/transmitter")
public class TransmitterController extends TransmitterBaseController {

  @Autowired
  TransmitterService transmitterService;

  @RequestMapping(value = "/transmitterDto", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.TRANSMITTER_VIEW)
  public TransmitterDTO findTransmitterDTOByTxId(@RequestBody String txid) {
    return transmitterService.findTransmitterDTOByTxId(txid);
  }

  @RequestMapping(value = "/searchDTO", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.TRANSMITTER_VIEW)
  public PagedResponse<TransmitterSearchDTO> findSearchDTOByCriteria(
      @RequestBody TransmitterCriteria criteria) {
    return transmitterService.findSearchDTOByCriteria(criteria);
  }

  @RequestMapping(value = "/{id}/deployedDateTime", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.TRANSMITTER_VIEW)
  public Date getDeployedDateTime(@PathVariable String id) {
    return transmitterService.getDeployedDateTime(id);
  }

  @RequestMapping(value = "/{id}/expiryDate", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Date> getDeployedDateTime(@PathVariable String id,
      @RequestParam(required = false) Integer lifeExpectancy,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date deployedDateTime) {
    if (lifeExpectancy != null && deployedDateTime != null) {
      // don't allow both as there is no point
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } else if (lifeExpectancy == null && deployedDateTime == null) {
      return ResponseEntity.ok(transmitterService.getExpiryDate(id));
    } else if (lifeExpectancy != null) {
      return ResponseEntity.ok(transmitterService.calculateExpiryDate(id, lifeExpectancy));
    } else {
      return ResponseEntity.ok(transmitterService.calculateExpiryDate(id, deployedDateTime));
    }
  }

  @RequestMapping(value = "/{id}/birdHistory", method = RequestMethod.GET)
  @ResponseBody
  public List<TransmitterBirdHistoryDTO> getBirdHistory(@PathVariable String id) {
    return transmitterService.getBirdHistory(id);
  }

}

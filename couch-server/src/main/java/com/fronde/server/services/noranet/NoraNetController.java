package com.fronde.server.services.noranet;

import com.fronde.server.domain.NoraNetDetectionEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

/**
 * @version 1.0
 * @date 14/07/2021
 */

@RestController
@RequestMapping("/api/noranet")
public class NoraNetController extends NoraNetBaseController {

  @RequestMapping(value = "/data", consumes = {"application/json"}, method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.NORANET_PROCESS)
  public ResponseEntity<String> processNoraNetData(@RequestBody NoraNetRequest noraNetRequest) {
    return service.processNoraNetRequest(noraNetRequest);
  }

  @RequestMapping(value = "/searchDTO", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.NORANET_VIEW)
  public PagedResponse<NoraNetSearchDTO> findSearchDTOByCriteria(@RequestBody NoraNetCriteria criteria) {
    return service.findSearchDTOByCriteria(criteria);
  }

  @RequestMapping(value = "/searchUndetectedDTO", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.NORANET_VIEW)
  public List<NoraNetSearchUndetectedDTO> findUndetectedDTOByCriteria(@RequestBody NoraNetCriteria criteria) {
    return service.findUndetectedDTOByCriteria(criteria);
  }

  // Basic structure ready for KD-836
  @RequestMapping(value = "/searchStationDTO", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.NORANET_VIEW)
  public PagedResponse<NoraNetSearchStationDTO> findStandardDTOByCriteria(@RequestBody NoraNetCriteria criteria) {
    return service.findStationDTOByCriteria(criteria);
  }

  @RequestMapping(value = "/searchSnarkDTO", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.NORANET_VIEW)
  public PagedResponse<NoraNetSearchSnarkDTO> findSnarkDTOByCriteria(@RequestBody NoraNetCriteria criteria) {
    return service.findSnarkDTOByCriteria(criteria);
  }

  @RequestMapping(value = "/detected/{docId}/{uhfId}", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.NORANET_VIEW)
  public NoraNetDetectionEntity findDetectedById(@PathVariable(value = "docId") String docId,
      @PathVariable(value = "uhfId") Integer uhfId) {
    NoraNetDetectionEntity entity = service.findDetectedById(docId, uhfId);
    if (entity == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return entity;
  }

}

package com.fronde.server.services.sample;

import com.fronde.server.domain.RecordEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import com.fronde.server.services.record.RecordService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/sample")
public class SampleController extends SampleBaseController {

  @Autowired
  protected RecordService recordService;

  @RequestMapping(value = "/searchDTO", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.SAMPLE_VIEW)
  public PagedResponse<SampleSearchDTO> findSearchDTOByCriteria(
      @RequestBody SampleCriteria criteria) {
    return service.findSearchDTOByCriteria(criteria);
  }

  @RequestMapping(value = "/{id}/record", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.SAMPLE_VIEW)
  public RecordEntity findRecordBySampleID(@PathVariable String id) {
    return recordService.findRecordBySampleID(id);
  }

  @RequestMapping(value = "/haematologyTests/stats", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.SAMPLE_VIEW)
  public List<TestStatsDTO> getHaematologyTestsStats(@RequestParam("type") String type) {
    return service.getHaematologyTestsStats(type);
  }

  @RequestMapping(value = "/haematologyTests/ranks", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.SAMPLE_VIEW)
  public List<ResultRankDTO> getHaematologyTestsRanks(@RequestBody RanksRequest request) {
    return service.getHaematologyTestsRanks(request.getSampleType(), request.getResults());
  }

  @RequestMapping(value = "/chemistryAssays/stats", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.SAMPLE_VIEW)
  public List<TestStatsDTO> getChemistryAssaysStats(@RequestParam("type") String type) {
    return service.getChemistryAssaysStats(type);
  }

  @RequestMapping(value = "/chemistryAssays/ranks", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.SAMPLE_VIEW)
  public List<ResultRankDTO> getChemistryAssaysRanks(@RequestBody RanksRequest request) {
    return service.getChemistryAssaysRanks(request.getSampleType(), request.getResults());
  }
}

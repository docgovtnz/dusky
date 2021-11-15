package com.fronde.server.services.latestweightreport;

import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import com.fronde.server.services.bird.BirdCriteria;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report/latestweight")
public class LatestWeightReportController {

  @Autowired
  protected LatestWeightReportService service;

  @RequestMapping(value = "/report", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.LATEST_WEIGHT_REPORT_VIEW)
  public PagedResponse<LatestWeightReportDTO> search(@RequestBody BirdCriteria criteria) {
    return service.generateReport(criteria);
  }

  @RequestMapping(value = "/export", method = RequestMethod.GET)
  @CheckPermission(Permission.LATEST_WEIGHT_REPORT_VIEW)
  public void export(BirdCriteria criteria, HttpServletResponse response) {
    service.export(criteria, response);
  }
}

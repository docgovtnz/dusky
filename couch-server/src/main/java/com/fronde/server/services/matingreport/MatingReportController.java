package com.fronde.server.services.matingreport;

import com.fronde.server.domain.response.Response;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report/mating")
public class MatingReportController {

  @Autowired
  protected MatingReportService service;

  @RequestMapping(value = "/report", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.MATING_REPORT_VIEW)
  public Response<List<MatingReportDTO>> search(@RequestBody MatingReportCriteria criteria) {
    return service.generateReport(criteria);
  }

  @RequestMapping(value = "/export", method = RequestMethod.GET)
  @CheckPermission(Permission.MATING_REPORT_VIEW)
  public void export(MatingReportCriteria criteria, HttpServletResponse response) {
    service.export(criteria, response);
  }

}

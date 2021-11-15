package com.fronde.server.services.checksheetreport;

import com.fronde.server.domain.response.Response;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report/checksheet")
public class ChecksheetReportController {

  @Autowired
  protected ChecksheetReportService service;

  @RequestMapping(value = "/report", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.CHECKSHEET_REPORT_VIEW)
  public Response<ChecksheetReport> search(@RequestBody ChecksheetReportCriteria criteria) {
    return service.generateReport(criteria);
  }

  @RequestMapping(value = "/export", method = RequestMethod.GET)
  @CheckPermission(Permission.CHECKSHEET_REPORT_VIEW)
  public void export(ChecksheetReportCriteria criteria, HttpServletResponse response) {
    service.export(criteria, response);
  }

}

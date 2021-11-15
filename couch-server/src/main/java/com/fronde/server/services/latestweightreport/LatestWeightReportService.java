package com.fronde.server.services.latestweightreport;

import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.bird.BirdCriteria;
import com.fronde.server.utils.CSVExportUtils;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LatestWeightReportService {

  @Autowired
  protected LatestWeightReportRepository repository;

  @Autowired
  protected CSVExportUtils exportUtils;

  public PagedResponse<LatestWeightReportDTO> generateReport(BirdCriteria criteria) {
    return repository.generateReport(criteria);
  }

  public void export(BirdCriteria criteria, HttpServletResponse response) {
    List<String> header = Arrays.asList(
        "Bird Name", "Latest Weight", "Latest Date", "Previous Weight",
        "Previous Date");
    List<String> props = Arrays.asList(
        "birdName", "latestWeight", "latestDate", "previousWeight", "previousDate");
    criteria.setPageNumber(1);
    criteria.setPageSize(Integer.MAX_VALUE);
    PagedResponse<LatestWeightReportDTO> reportResponse = this.generateReport(criteria);
    if (reportResponse.getResults() == null) {
      response.setStatus(500);
      return;
    }
    List<LatestWeightReportDTO> report = reportResponse.getResults();

    exportUtils.export(response, report, header, props, "latestweightreport");
  }
}

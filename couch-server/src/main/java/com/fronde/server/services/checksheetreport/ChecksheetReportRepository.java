package com.fronde.server.services.checksheetreport;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public interface ChecksheetReportRepository {

  List<ChecksheetDTO> generateReport(ChecksheetReportCriteria criteria);

  List<ChecksheetDTO> getAllBirds(ChecksheetReportCriteria criteria);
}

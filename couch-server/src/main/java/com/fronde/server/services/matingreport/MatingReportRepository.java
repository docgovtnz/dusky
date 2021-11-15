package com.fronde.server.services.matingreport;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public interface MatingReportRepository {

  List<MatingReportDTO> generateReport(MatingReportCriteria criteria);

}

package com.fronde.server.services.latestweightreport;

import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.bird.BirdCriteria;
import org.springframework.stereotype.Component;

@Component
public interface LatestWeightReportRepository {

  PagedResponse<LatestWeightReportDTO> generateReport(BirdCriteria criteria);
}

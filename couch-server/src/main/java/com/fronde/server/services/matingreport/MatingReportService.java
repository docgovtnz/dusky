package com.fronde.server.services.matingreport;

import com.fronde.server.domain.response.Response;
import com.fronde.server.services.validation.ValidationMessage;
import com.fronde.server.utils.CSVExportUtils;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MatingReportService {

  @Autowired
  protected MatingReportRepository repository;

  @Autowired
  protected CSVExportUtils exportUtils;

  /**
   * Generates the report. The report is fairly complex due to challenges implementing certain
   * functions within Couchbase including: * Timezone conversion - on Windows timezone conversion
   * does not work by default. It is likely that additional configuration may make this work, but it
   * was decided to implement in Java in the immediate term. * Non-key joins - ideally we would join
   * to configuration, but the join would be most logical using a non-key join (join based on
   * RecordType and Reason). Instead we supplement the data from Couchbase with configuration
   * information from an XML file. The XML file should later be moved to the DB for simpler
   * maintenance.
   *
   * @param criteria
   * @return
   */
  public Response<List<MatingReportDTO>> generateReport(MatingReportCriteria criteria) {

    Response<List<MatingReportDTO>> response = new Response<>();

    // Basic validations.
    validateNotNull(criteria.getFromDate(), "First Date", response);
    validateNotNull(criteria.getToDate(), "Last Date", response);

    // Validate date order.
    if (criteria.getFromDate() != null && criteria.getToDate() != null && criteria.getFromDate()
        .after(criteria.getToDate())) {
      ValidationMessage msg = new ValidationMessage();
      msg.setMessageText("First Date must be before Last Date");
      response.getMessages().add(msg);
    }

    if (response.getMessages().size() > 0) {
      return response;
    }

    // Get the records.
    List<MatingReportDTO> results = this.repository.generateReport(criteria);

    response.setModel(results);

    return response;
  }

  /**
   * Helper method to generate validation error messages.
   *
   * @param item      The value of the field.
   * @param fieldName The name of the field (used in the message).
   * @param response  The response to add the validation mesasge to.
   */
  private void validateNotNull(Object item, String fieldName,
      Response<List<MatingReportDTO>> response) {
    if (item == null || (item instanceof String && StringUtils.isEmpty(item))) {
      ValidationMessage msg = new ValidationMessage();
      msg.setMessageText(fieldName + " is required.");
      response.getMessages().add(msg);
    }
  }

  public void export(MatingReportCriteria criteria, HttpServletResponse response) {
    List<String> header = Arrays.asList(
        "Date/Time", "Island", "Female", "Female Mating Count", "Male", "Duration",
        "Quality");
    List<String> props = Arrays.asList(
        "dateTime", "island", "femaleBirdName", "femaleMatingCount", "maleBirdName",
        "duration", "quality");
    Response<List<MatingReportDTO>> reportResponse = this.generateReport(criteria);
    if (reportResponse.getModel() == null) {
      response.setStatus(500);
      return;
    }
    List<MatingReportDTO> report = reportResponse.getModel();

    exportUtils.export(response, report, header, props, "matingreport");
  }

}


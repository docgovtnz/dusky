package com.fronde.server.services.checksheetreport;

import com.fronde.server.domain.response.Response;
import com.fronde.server.services.checksheetreport.config.ChecksheetConfig;
import com.fronde.server.services.checksheetreport.config.ChecksheetElement;
import com.fronde.server.services.validation.ValidationMessage;
import com.fronde.server.utils.CSVExportUtils;
import com.fronde.server.utils.XmlUtils;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ChecksheetReportService {

  @Autowired
  protected ChecksheetReportRepository repository;

  @Autowired
  protected CSVExportUtils exportUtils;

  private ChecksheetConfig checksheetConfig;

  public synchronized ChecksheetConfig getConfig() {
    if (checksheetConfig == null) {
      try {
        // Load the map from XML.
        String resourceName = "/checksheet-config.xml";
        InputStream resourceAsStream = ChecksheetConfig.class.getResourceAsStream(resourceName);
        String xml = IOUtils.toString(resourceAsStream);
        this.checksheetConfig = XmlUtils.convertFromString(ChecksheetConfig.class, xml);

      } catch (Exception ex) {
        throw new RuntimeException("Unable to load checksheet configuration", ex);
      }
    }
    return checksheetConfig;
  }


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
  public Response<ChecksheetReport> generateReport(ChecksheetReportCriteria criteria) {

    Response<ChecksheetReport> response = new Response<>();

    // Basic validations.
    validateNotNull(criteria.getFromDate(), "First Date", response);
    validateNotNull(criteria.getToDate(), "Last Date", response);
    validateNotNull(criteria.getNumDays(), "Show Day Frequency", response);
    validateNotNull(criteria.getIsland(), "Island", response);

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
    List<ChecksheetDTO> results = this.repository.generateReport(criteria);

    // Convert the date to a local timezone.
    LocalDate fromDate = toLocalDate(criteria.getFromDate());

    // Pre-process the items
    // 1. Filter out items that don't have a mapping.
    // 2. Set the interaction type.
    // 3. Align the date to the periods as per the reporting criteria.
    List<ChecksheetDTO> preprocessedList = new LinkedList<>();
    for (ChecksheetDTO dto : results) {
      ChecksheetElement configElement = getConfig().get(dto.getRecordType(), dto.getReason());
      if (configElement != null) {
        preprocessedList.add(dto);
        dto.setInteractionType(configElement.getChecksheetSymbol());
        dto.deriveAlignedDate(fromDate, criteria.getNumDays());
      }
    }

    // Aggregate the results by bird and period.
    HashMap<String, ChecksheetRecord> aggregatedResults = new HashMap<>();
    for (ChecksheetDTO dto : preprocessedList) {
      String dateFormatted = dto.getAlignedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

      // Get the current bird's information.
      ChecksheetRecord current = aggregatedResults.get(dto.getBirdID());

      if (current == null) {
        // If the bird doesn't already exist, add it in.
        current = new ChecksheetRecord();
        current.setBirdID(dto.getBirdID());
        current.setBirdName(dto.getBirdName());
        current.getInteractionsByDate().put(dateFormatted, dto.getInteractionType());
        aggregatedResults.put(dto.getBirdID(), current);
      } else {
        // The bird exists, check for an existing interaction and choose the highest priority interaction for
        // the report.
        String currentInteraction = current.getInteractionsByDate().get(dateFormatted);
        if (currentInteraction == null) {
          current.getInteractionsByDate().put(dateFormatted, dto.getInteractionType());
        } else {
          current.getInteractionsByDate()
              .put(dateFormatted, getBestInteraction(currentInteraction, dto.getInteractionType()));
        }
      }
    }

    // Add birds that don't have any records.
    List<ChecksheetDTO> allBirds = repository.getAllBirds(criteria);
    allBirds.stream().forEach(cs -> aggregatedResults
        .putIfAbsent(cs.getBirdID(), new ChecksheetRecord(cs.getBirdID(), cs.getBirdName())));

    // Sort the results by bird name.
    List<ChecksheetRecord> resultsList = aggregatedResults.values().stream()
        .collect(Collectors.toList());
    resultsList.sort(Comparator.comparing(ChecksheetRecord::getBirdName));

    // Determine the date headers.
    List<String> dateHeaders = new LinkedList<>();
    LocalDate toDate = toLocalDate(criteria.getToDate());
    LocalDate workingDate = LocalDate.from(fromDate);

    while (!workingDate.isAfter(toDate)) {
      dateHeaders.add(workingDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
      workingDate = workingDate.plusDays(criteria.getNumDays());
    }

    // Build the report.
    ChecksheetReport report = new ChecksheetReport();
    report.setDateColumns(dateHeaders.toArray(new String[0]));
    report.setRecords(resultsList);

    response.setModel(report);
    return response;
  }

  /**
   * Helper method to generate validation error messages.
   *
   * @param item      The value of the field.
   * @param fieldName The name of the field (used in the message).
   * @param response  The response to add the validation mesasge to.
   */
  private void validateNotNull(Object item, String fieldName, Response<ChecksheetReport> response) {
    if (item == null || (item instanceof String && StringUtils.isEmpty(item))) {
      ValidationMessage msg = new ValidationMessage();
      msg.setMessageText(fieldName + " is required.");
      response.getMessages().add(msg);
    }
  }


  /**
   * Determines the highest priority interaction "symbol" by checking against the configuration.
   *
   * @param value1 The first symbol.
   * @param value2 The second symbol.
   * @return The highest priority symbol.
   */
  private String getBestInteraction(String value1, String value2) {
    Integer priority1 = getConfig().getChecksheetPriority().get(value1);
    Integer priority2 = getConfig().getChecksheetPriority().get(value2);
    if (priority1 != null && priority2 != null) {
      return priority1 > priority2 ? value1 : value2;
    } else if (priority1 != null && priority2 == null) {
      return value2;
    } else {
      return priority2 == null ? "" : value2;
    }
  }

  /**
   * Align the date to the start of a period.
   *
   * @param startDate    The date from which the report starts.
   * @param date         The date to align.
   * @param daysInPeriod The number of days in each period.
   * @return The date, aligned to the start of a period.
   */
  private LocalDate align(LocalDate startDate, Date date, int daysInPeriod) {
    return startDate.plusDays(
        startDate.until(toLocalDate(date), ChronoUnit.DAYS) / daysInPeriod * daysInPeriod);
  }

  private LocalDate toLocalDate(Date dt) {
    return dt.toInstant().atZone(ZoneId.of("Pacific/Auckland")).toLocalDate();
  }

  public void export(ChecksheetReportCriteria criteria, HttpServletResponse response) {
    Response<ChecksheetReport> reportResponse = this.generateReport(criteria);
    if (reportResponse.getModel() == null) {
      response.setStatus(500);
      return;
    }
    ChecksheetReport report = reportResponse.getModel();

    // Assemble the headers.
    String[] header = new String[report.getDateColumns().length + 1];
    header[0] = "Bird Name";
    System.arraycopy(report.getDateColumns(), 0, header, 1, report.getDateColumns().length);
    List<String> headerList = Arrays.asList(header);

    // Re-map the report data. We're almost in the right format but need to add the bird name.
    List<Map<String, String>> reportData = new LinkedList<>();
    for (ChecksheetRecord r : report.getRecords()) {
      Map<String, String> data = r.getInteractionsByDate();
      data.put("Bird Name", r.getBirdName());
      reportData.add(data);
    }

    exportUtils.export(response, reportData, headerList, headerList, "checksheetreport");
  }

}


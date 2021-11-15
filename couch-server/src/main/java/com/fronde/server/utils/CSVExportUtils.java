package com.fronde.server.utils;

import com.fronde.server.domain.response.PagedResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CSVExportUtils {

  private static final Logger logger = LoggerFactory.getLogger(CSVExportUtils.class);

  @Value("${export.http.contentType}")
  private String contentType;

  @Value("${export.filename.formatString}")
  private String filenameFormatString;

  @Value("${export.delimiter}")
  private char delimiter;

  @Value("${export.date.formatString}")
  private String dateformatString;

  // set at the maximum rows for excel minus 1 for the header row
  // excel 2007 onwards has a maximum of 1048576 rows
  // https://support.office.com/en-us/article/excel-specifications-and-limits-1672b34d-7043-467e-8e27-269d656771c3#ID0EBABAAA=Office_2007
  @Value("${export.data.maxRows:1048575}")
  private Integer maxRows;

  public Integer getMaxRows() {
    return maxRows;
  }

  public <T> void export(HttpServletResponse response, PagedResponse<T> pr, List<String> header,
      List<String> props, String type) {
    export(response, pr.getResults(), header, props, type);
  }

  public <T> void export(HttpServletResponse response, List<T> list, List<String> header,
      List<String> props, String type) {
    // Wrap all logic in a try catch
    // Errors that occur from here will be written into the file
    // Errors before this point won't produce a file and user will be directed to the default error page whatever that may be
    CSVPrinter cp = null;
    try {
      cp = startExport(response, header, type);
      appendToExport(cp, list, props);
    } catch (Exception e) {
      appendErrorToExport(cp, e);
    } finally {
      completeExport(cp);
    }
  }

  public CSVPrinter startExport(HttpServletResponse response, List<String> header, String type)
      throws IOException {
    response.setContentType(contentType);
    response.setHeader("content-disposition",
        "attachment; filename=\"" + String.format(filenameFormatString, type, new Date()) + "\"");
    CSVPrinter cp = new CSVPrinter(new OutputStreamWriter(response.getOutputStream()),
        CSVFormat.EXCEL.withDelimiter(delimiter));
    // write out the headers
    cp.printRecord(header);
    cp.flush();
    return cp;
  }

  public void completeExport(CSVPrinter cp) {
    try {
      cp.close();
    } catch (Exception e3) {
      logger.warn("Exception '" + e3 + "' encountered while closing the export output stream");
      logger.debug("Exception detail", e3);
    }
  }

  public void appendErrorToExport(CSVPrinter cp, Exception e1) {
    try {
      logger
          .error("Exception '" + e1 + "' encountered while retrieving the rest of the results", e1);
      if (cp != null) {
        cp.printRecords(
            "An error occurred while retrieving the rest of the results. Please try the export again ("
                + e1 + ")");
      }
    } catch (Exception e2) {
      logger.error("Exception '" + e2 + "' encountered while handling exception '" + e1 + "'", e2);
    }
  }

  public <T> void appendToExport(CSVPrinter cp, List<T> list, List<String> props)
      throws IOException {
    int recordCount = 0;
    for (T i : list) {
      List r = new ArrayList();
      // if the element is a map then just reference the property by key
      if (i instanceof Map) {
        for (String p : props) {
          Object pv = ((Map) i).get(p);
          // if type is a date then format specially
          pv = formatDateIfRequired(pv);
          r.add(pv);
        }
      } else {
        // otherwise use bean wrapper to get out the property value
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(i);
        for (String p : props) {
          Object pv = null;
          try {
            pv = bw.getPropertyValue(p);
          } catch (NotReadablePropertyException e) {
            logger.error(
                "Exception '" + e + "' encountered while getting property value '" + pv + "'", e);
            pv = e.getMessage();
          }
          // if type is a date then format specially
          pv = formatDateIfRequired(pv);
          r.add(pv);
        }
      }
      recordCount++;
      cp.printRecord(r);
    }
    cp.flush();
  }

  private Object formatDateIfRequired(Object o) {
    if (o instanceof Date) {
      return String.format(dateformatString, o);
    } else {
      return o;
    }
  }

}

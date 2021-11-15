package com.fronde.server.services.snarkactivity;

import com.fronde.server.domain.SnarkActivityEntity;
import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.utils.ServiceUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SnarkActivityService extends SnarkActivityBaseService {

  // the maximum number of birds to be exported per result
  @Value("${snarkactivity.export.maxBirds:10}")
  protected Integer exportMaxBirds;

  // the number of results to be exported per batch
  @Value("${snarkactivity.export.pageSize:2500}")
  protected Integer exportPageSize;

  @Override
  public DeleteByIdCheckDTO deleteByIdCheck(String docId) {
    DeleteByIdCheckDTO dto = new DeleteByIdCheckDTO();
    dto.setId(docId);
    // we can't delete something that doesn't exist
    dto.setDeleteOk(docId != null && !repository.hasReferences(docId));
    return dto;
  }

  public PagedResponse<SnarkActivitySearchDTO> findSearchDTOByCriteria(
      SnarkActivityCriteria criteria) {
    return repository.findSearchDTOByCriteria(criteria);
  }

  public void export(SnarkActivityCriteria criteria, HttpServletResponse response) {
    // list the properties to be copied from the search dto to the map directly
    List<String> dtoProps = Arrays.asList(
        "activityType", "locationName", "date", "trackActivity", "sticks", "grubbing",
        "boom", "ching", "skraak", "matingSign");

    // list the headers and properties making space for at least maxBirds
    List<String> header = new ArrayList<>();
    header.addAll(Arrays.asList(
        "Activity Type", "Location", "Date", "Track Activity", "Sticks", "Grubbing",
        "Boom", "Ching", "Skraak", "Mating Sign"));
    List<String> props = new ArrayList<>();
    props.addAll(dtoProps);
    // add columns up until the maximum birds
    for (int i = 1; i <= exportMaxBirds; i++) {
      header.add("Bird " + i);
      props.add("bird" + i);
    }

    // write out the csv a page at a time so as not to place too much on couchbase and the server
    CSVPrinter cp = null;
    try {
      cp = exportUtils.startExport(response, header, "snarkactivity");
      int pageCount = exportUtils.getMaxRows() % exportPageSize;
      for (int page = 1; page <= pageCount; page++) {
        criteria.setPageSize(exportPageSize);
        criteria.setPageNumber(page);

        PagedResponse<SnarkActivitySearchDTO> pr = this.findSearchDTOByCriteria(criteria);
        if (pr.getResults().size() == 0) {
          break;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (SnarkActivitySearchDTO dto : pr.getResults()) {
          Map<String, Object> map = new HashMap<>();
          for (String key : dtoProps) {
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(dto);
            map.put(key, bw.getPropertyValue(key));
          }
          if (dto.getBirds() != null) {
            int birdCount = dto.getBirds().size();
            for (int i = 1; i <= Math.min(birdCount, exportMaxBirds); i++) {
              map.put("bird" + i, dto.getBirds().get(i - 1).getName());
            }
          }
          list.add(map);
        }

        exportUtils.appendToExport(cp, list, props);
      }
    } catch (Exception e) {
      exportUtils.appendErrorToExport(cp, e);
    } finally {
      exportUtils.completeExport(cp);
    }
  }

  public void saveWithThrow(SnarkActivityEntity entity) {
    ServiceUtils.throwIfRequired(save(entity), "SnarkActivity", entity.getId());
  }

}


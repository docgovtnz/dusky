package com.fronde.server.services.feedout;

import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.domain.response.PagedResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class FeedOutService extends FeedOutBaseService {

  @Override
  public DeleteByIdCheckDTO deleteByIdCheck(String docId) {
    DeleteByIdCheckDTO result = new DeleteByIdCheckDTO();
    result.setId(docId);
    // feed outs are top level independent entities so can always be deleted
    // we can't delete something that does not exist
    result.setDeleteOk(docId != null);
    return result;
  }

  public void export(FeedOutCriteria criteria, HttpServletResponse response) {
    List<String> header = new ArrayList<String>();
    header.addAll(Arrays.asList("Location", "Date out", "Date in"));
    List<String> props = new ArrayList<String>();
    props.addAll(Arrays.asList("locationName", "dateOut", "dateIn"));
    criteria.setPageSize(exportUtils.getMaxRows());
    criteria.setPageNumber(1);
    PagedResponse<FeedOutSearchDTO> pr = this.findSearchDTOByCriteria(criteria);
    List list = new ArrayList();
    List<String> birdsHeader = new ArrayList<String>();
    List<String> birdsProps = new ArrayList<String>();
    List<String> tallysHeader = new ArrayList<String>();
    List<String> tallysProps = new ArrayList<String>();
    for (FeedOutSearchDTO fo : pr.getResults()) {
      Map map = new HashMap<String, Object>();
      map.put("locationName", fo.getLocationName());
      map.put("dateOut", fo.getDateOut());
      map.put("dateIn", fo.getDateIn());
      if (fo.getTargetBirdList() != null) {
        int index = 1;
        for (TargetBirdSearchDTO tb : fo.getTargetBirdList()) {
          String key = "bird" + index;
          map.put(key, tb.getBirdName());
          if (!birdsProps.contains(key)) {
            birdsProps.add(key);
            birdsHeader.add("Bird " + index);
          }
          index++;
        }
      }
      if (fo.getFoodTallyList() != null) {
        for (FoodTallySearchDTO ft : fo.getFoodTallyList()) {
          if (ft.getOut() != null || ft.getIn() != null) {
            String outKey = ft.getName() + "out";
            map.put(outKey, ft.getOut());
            if (!tallysProps.contains(outKey)) {
              tallysProps.add(outKey);
              tallysHeader.add(ft.getName() + " out");
            }
            String inKey = ft.getName() + "in";
            map.put(inKey, ft.getIn());
            if (!tallysProps.contains(inKey)) {
              tallysProps.add(inKey);
              tallysHeader.add(ft.getName() + " in");
            }
            String consumedKey = ft.getName() + "consumed";
            map.put(consumedKey, ft.getConsumed());
            if (!tallysProps.contains(consumedKey)) {
              tallysProps.add(consumedKey);
              tallysHeader.add(ft.getName() + " consumed");
            }
          }
        }
      }
      list.add(map);
    }
    header.addAll(birdsHeader);
    props.addAll(birdsProps);
    header.addAll(tallysHeader);
    props.addAll(tallysProps);
    exportUtils.export(response, list, header, props, "feedout");
  }

  public PagedResponse<FeedOutSearchDTO> findSearchDTOByCriteria(FeedOutCriteria criteria) {
    return repository.findSearchDTOByCriteria(criteria);
  }

}

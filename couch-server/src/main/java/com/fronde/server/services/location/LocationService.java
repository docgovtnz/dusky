package com.fronde.server.services.location;

import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.feedout.FeedOutService;
import com.fronde.server.services.record.RecordService;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationService extends LocationBaseService {

  @Autowired
  protected RecordService recordService;
  @Autowired
  protected FeedOutService feedOutService;

  @Override
  public DeleteByIdCheckDTO deleteByIdCheck(String docId) {
    DeleteByIdCheckDTO dto = new DeleteByIdCheckDTO();
    dto.setId(docId);
    // we can't delete something that doesn't exist
    dto.setDeleteOk(docId != null && !repository.hasReferences(docId));
    return dto;
  }

  public PagedResponse<LocationSearchDTO> findSearchDTOByCriteria(LocationCriteria criteria) {
    return repository.findSearchDTOByCriteria(criteria);
  }

  public void export(LocationCriteria criteria, HttpServletResponse response) {
    List<String> header = Arrays.asList(
        "Location Name", "Island", "Location Type", "Easting", "Northing", "Bird",
        "Active");
    List<String> props = Arrays.asList(
        "locationName", "island", "locationType", "easting", "northing", "birdName",
        "active");
    criteria.setPageSize(exportUtils.getMaxRows());
    criteria.setPageNumber(1);
    PagedResponse<LocationSearchDTO> pr = this.findSearchDTOByCriteria(criteria);
    exportUtils.export(response, pr, header, props, "location");
  }

  public PagedResponse<String> getCurrentEggs(String locationID) {
    return repository.getCurrentEggs(locationID);
  }

  public PagedResponse<String> getCurrentChicks(String locationID) {
    return repository.getCurrentChicks(locationID);
  }

  public Integer getNextClutchOrder(String locationID) {
    return repository.getNextClutchOrder(locationID);
  }

  public String getClutch(String locationID) {
    return repository.getClutch(locationID);
  }

  /**
   * Returns true if the specified location name is unique for the specified location (via
   * locationID). locationID can be null in the case of this being a new location.
   *
   * @param locationID
   * @param locationName
   * @return
   */
  public boolean isUniqueName(String locationID, String locationName) {
    boolean exists = repository.existsByNameExcluding(locationName, locationID);
    return !exists;
  }

}

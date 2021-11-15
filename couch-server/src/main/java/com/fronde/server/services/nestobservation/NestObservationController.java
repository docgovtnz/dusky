package com.fronde.server.services.nestobservation;

import com.fronde.server.domain.NestObservationEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import com.fronde.server.services.bird.BirdService;
import com.fronde.server.services.record.RecordService;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/nestobservation")
public class NestObservationController extends NestObservationBaseController {

  @Autowired
  private RecordService recordService;

  @Autowired
  private BirdService birdService;

  @RequestMapping(value = "/searchDTO", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.NESTOBSERVATION_VIEW)
  public PagedResponse<NestObservationSearchDTO> findSearchDTOByCriteria(
      @RequestBody NestObservationCriteria criteria) {
    PagedResponse<NestObservationSearchDTO> results = service.findSearchDTOByCriteria(criteria);
    return results;
  }

  @RequestMapping(value = "/{docId}/eggRecords", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.NESTOBSERVATION_VIEW)
  public List<RecordEntity> getEggRecords(@PathVariable(value = "docId") String docId) {
    NestObservationEntity nestObservation = service.findById(docId);

    List<RecordEntity> records = new LinkedList<>();
    if (nestObservation.getEggRecordReferenceList() != null) {
      nestObservation.getEggRecordReferenceList().stream().filter(ref -> ref.getRecordID() != null)
          .forEach(ref -> records.add(recordService.findById(ref.getRecordID())));
    }
    records.sort(Comparator
        .comparing(record -> birdService.findById(record.getBirdID()).getBirdName().toLowerCase()));

    return records;
  }

  @RequestMapping(value = "/{docId}/chickRecords", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.NESTOBSERVATION_VIEW)
  public List<RecordEntity> getChickRecords(@PathVariable(value = "docId") String docId) {
    NestObservationEntity nestObservation = service.findById(docId);

    List<RecordEntity> records = new LinkedList<>();
    if (nestObservation.getChickRecordReferenceList() != null) {
      nestObservation.getChickRecordReferenceList().stream()
          .filter(ref -> ref.getRecordID() != null)
          .forEach(ref -> records.add(recordService.findById(ref.getRecordID())));
    }
    records.sort(Comparator
        .comparing(record -> birdService.findById(record.getBirdID()).getBirdName().toLowerCase()));

    return records;
  }

  @RequestMapping(value = "", method = RequestMethod.GET, params = "recordID")
  @ResponseBody
  @CheckPermission(Permission.NESTOBSERVATION_VIEW)
  public NestObservationEntity findByRecordID(@RequestParam("recordID") String recordID) {
    NestObservationEntity no = service.findByRecordID(recordID);
    return no;
  }

}

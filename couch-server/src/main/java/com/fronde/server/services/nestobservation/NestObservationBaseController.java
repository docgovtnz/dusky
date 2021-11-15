package com.fronde.server.services.nestobservation;

import com.fronde.server.domain.NestObservationEntity;
import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

public abstract class NestObservationBaseController {

  @Autowired
  protected NestObservationService service;

  @RequestMapping(value = "/{docId}", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.NESTOBSERVATION_VIEW)
  public NestObservationEntity findById(@PathVariable(value = "docId") String docId) {
    NestObservationEntity entity = service.findById(docId);
    if (entity == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return entity;
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.NESTOBSERVATION_VIEW)
  public PagedResponse<NestObservationEntity> search(
      @RequestBody NestObservationCriteria criteria) {
    return service.search(criteria);
  }

  @RequestMapping(value = "/", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.NESTOBSERVATION_EDIT)
  public Response<NestObservationEntity> save(@RequestBody NestObservationEntity entity) {
    return service.save(entity);
  }

  @RequestMapping(value = "/{docId}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @CheckPermission(Permission.NESTOBSERVATION_DELETE)
  public void deleteById(@PathVariable(value = "docId") String docId) {
    service.deleteById(docId);
  }

  @RequestMapping(value = "/{docId}/check", method = RequestMethod.DELETE)
  @ResponseBody
  @CheckPermission(Permission.NESTOBSERVATION_VIEW)
  public DeleteByIdCheckDTO deleteByIdCheck(@PathVariable(value = "docId") String docId) {
    return service.deleteByIdCheck(docId);
  }

  @RequestMapping(value = "/export", method = RequestMethod.GET)
  @CheckPermission(Permission.NESTOBSERVATION_VIEW)
  public void export(NestObservationCriteria criteria, HttpServletResponse response) {
    service.export(criteria, response);
  }

}

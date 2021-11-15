package com.fronde.server.services.bird;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.EggMeasurementsEntity;
import com.fronde.server.domain.EmbryoMeasurementsEntity;
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

public abstract class BirdBaseController {

  @Autowired
  protected BirdService service;

  @RequestMapping(value = "/{docId}", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.BIRD_VIEW)
  public BirdEntity findById(@PathVariable(value = "docId") String docId) {
    BirdEntity entity = service.findById(docId);
    if (entity == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    if (entity.getEggMeasurements() == null) {
      entity.setEggMeasurements(new EggMeasurementsEntity());
    }
    if (entity.getEmbryoMeasurements() == null) {
      entity.setEmbryoMeasurements(new EmbryoMeasurementsEntity());
    }
    return entity;
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.BIRD_VIEW)
  public PagedResponse<BirdEntity> search(@RequestBody BirdCriteria criteria) {
    return service.search(criteria);
  }

  @RequestMapping(value = "/", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.BIRD_EDIT)
  public Response<BirdEntity> save(@RequestBody BirdEntity entity) {
    return service.save(entity);
  }

  @RequestMapping(value = "/{docId}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @CheckPermission(Permission.BIRD_DELETE)
  public void deleteById(@PathVariable(value = "docId") String docId) {
    service.deleteById(docId);
  }

  @RequestMapping(value = "/{docId}/check", method = RequestMethod.DELETE)
  @ResponseBody
  @CheckPermission(Permission.BIRD_VIEW)
  public DeleteByIdCheckDTO deleteByIdCheck(@PathVariable(value = "docId") String docId) {
    return service.deleteByIdCheck(docId);
  }

  @RequestMapping(value = "/export", method = RequestMethod.GET)
  @CheckPermission(Permission.BIRD_VIEW)
  public void export(BirdCriteria criteria, HttpServletResponse response) {
    service.export(criteria, response);
  }

}

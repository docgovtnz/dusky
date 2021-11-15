package com.fronde.server.services.noranet;

import com.fronde.server.domain.NoraNetEntity;
import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import javax.servlet.http.HttpServletResponse;

/**
 * @version 1.0
 * @date 14/07/2021
 */

public abstract class NoraNetBaseController {

  @Autowired
  protected NoraNetService service;

  @RequestMapping(value = "/", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.NORANET_EDIT)
  public Response<NoraNetEntity> save(@RequestBody NoraNetEntity entity) {
    return service.save(entity);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.NORANET_VIEW)
  public PagedResponse<NoraNetEntity> search(@RequestBody NoraNetCriteria criteria) {
    return service.search(criteria);
  }

  @RequestMapping(value = "/{docId}", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.NORANET_VIEW)
  public NoraNetEntity findById(@PathVariable(value = "docId") String docId) {
    NoraNetEntity entity = service.findById(docId);
    if (entity == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return entity;
  }

  @RequestMapping(value = "/{docId}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @CheckPermission(Permission.NORANET_DELETE)
  public void deleteById(@PathVariable(value = "docId") String docId) {
    service.deleteById(docId);
  }

  @RequestMapping(value = "/{docId}/check", method = RequestMethod.DELETE)
  @ResponseBody
  @CheckPermission(Permission.NORANET_VIEW)
  public DeleteByIdCheckDTO deleteByIdCheck(@PathVariable(value = "docId") String docId) {
    return service.deleteByIdCheck(docId);
  }

  @RequestMapping(value = "/export", method = RequestMethod.GET)
  @CheckPermission(Permission.RECORD_VIEW)
  public void export(@RequestBody NoraNetCriteria criteria, HttpServletResponse response) {
    service.export(criteria, response);
  }

}

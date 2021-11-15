package com.fronde.server.services.revision;

import com.fronde.server.domain.RevisionList;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@RequestMapping("/api/revision")
public class RevisionController {

  @Autowired
  protected RevisionService service;


  @RequestMapping(value = "/{entityId}", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public RevisionList findRevisionList(@PathVariable(value = "entityId") String entityId) {
    return service.findRevisionList(entityId);
  }
}

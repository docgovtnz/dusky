package com.fronde.server.services.noraneterror;

import com.fronde.server.domain.response.Response;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @version 1.0
 */

@RestController
@RequestMapping("/api/noraneterror")
public class NoraNetErrorController extends NoraNetErrorBaseController {

  @RequestMapping(value = "/report", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.NORANET_ERRORS)
  public Response<List<NoraNetErrorReportDTO>> findSearchDTOByCriteria(@RequestBody NoraNetErrorCriteria criteria) {
    return service.findSearchDTOByCriteria(criteria);
  }

}

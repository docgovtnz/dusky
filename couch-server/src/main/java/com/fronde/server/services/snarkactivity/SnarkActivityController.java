package com.fronde.server.services.snarkactivity;

import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/snarkactivity")
public class SnarkActivityController extends SnarkActivityBaseController {

  @RequestMapping(value = "/searchDTO", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.SNARKACTIVITY_VIEW)
  public PagedResponse<SnarkActivitySearchDTO> findSearchDTOByCriteria(
      @RequestBody SnarkActivityCriteria criteria) {
    return service.findSearchDTOByCriteria(criteria);
  }

}

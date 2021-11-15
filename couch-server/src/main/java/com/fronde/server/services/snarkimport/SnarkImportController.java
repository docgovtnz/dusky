package com.fronde.server.services.snarkimport;

import com.fronde.server.domain.SnarkImportEntity;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/snarkimport")
public class SnarkImportController extends SnarkImportBaseController {

  @RequestMapping(value = "/import", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.SNARKIMPORT_EDIT)
  public Response<SnarkImportEntity> importSnark(
      @RequestBody SnarkImportRequest snarkImportRequest) {
    return service.importSnark(snarkImportRequest);
  }

  @RequestMapping(value = "/include", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.SNARKIMPORT_EDIT)
  public List<EveningDTO> includeSelectedSnark(@RequestBody SnarkProcessRequest request) {
    return service.includeSelectedRecords(request);
  }

  @RequestMapping(value = "/check", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.SNARKIMPORT_VIEW)
  public SnarkCheckResultDTO checkSnark(@RequestBody SnarkCheckRequestDTO request) {
    return service.checkSnark(request);
  }

}

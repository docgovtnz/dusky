package com.fronde.server.services.txmortality;

import com.fronde.server.domain.TxMortalityEntity;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/txmortality")
public class TxMortalityController extends TxMortalityBaseController {

  @RequestMapping(value = "/all", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.TXMORTALITY_VIEW)
  public List<TxMortalityEntity> getAllTxMortalityOptions() {
    return service.getAllTxMortalityOptions();
  }

}

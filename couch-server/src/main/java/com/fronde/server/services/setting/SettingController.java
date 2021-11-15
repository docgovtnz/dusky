package com.fronde.server.services.setting;

import com.fronde.server.domain.response.Response;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import com.fronde.server.services.authorization.PublicAPI;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/setting")
public class SettingController extends SettingBaseController {

  @RequestMapping(method = RequestMethod.PUT)
  @ResponseBody
  @CheckPermission(value = Permission.SETTING_EDIT)
  public Response<Map<String, Object>> put(@RequestBody Map<String, Object> settings) {
    return service.put(settings);
  }

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(value = Permission.SETTING_VIEW)
  public Map<String, Object> get(@RequestParam(value = "id") List<String> ids) {
    return service.get(ids);
  }

  @RequestMapping(value = "/{docId}/value", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(value = Permission.SETTING_VIEW)
  public Object getValue(@PathVariable(value = "docId") String docId) {
    return service.getValue(docId);
  }

  @RequestMapping(value = "/all", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(value = Permission.SETTING_VIEW)
  public Object getAll() {
    return service.getAll();
  }

  @RequestMapping(value = "/password/{type}", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.COUCHBASE_PASSWORD_VIEW)
  public PasswordDTO getPassword(@PathVariable(value = "type") String type) {
    return service.getPassword(type);
  }

  @RequestMapping(value = "/helpLink", method = RequestMethod.GET)
  @ResponseBody
  @PublicAPI
  public String getHelpLink() {
    return service.getHelpLink();
  }

}

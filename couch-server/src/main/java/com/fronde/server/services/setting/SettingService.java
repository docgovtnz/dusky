package com.fronde.server.services.setting;

import com.fronde.server.domain.SettingEntity;
import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.validation.ValidationMessage;
import com.fronde.server.utils.ServiceUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

@Component
public class SettingService extends SettingBaseService {

  @Value("${couchbase.bucket.password}")
  protected String couchbaseBucketPassword;

  @Value("${couchbase.admin.password}")
  protected String couchbaseAdminPassword;

  @Value("${application.mode}")
  protected String serverMode;


  @Override
  public DeleteByIdCheckDTO deleteByIdCheck(String docId) {
    // TODO figure out what is valid here
    return null;
  }

  @Override
  public SettingEntity findById(String docId) {
    return repository.findById(docId).orElse(null);
  }

  public Response<Map<String, Object>> put(Map<String, Object> settings) {
    Map<String, Object> responseMap = new HashMap<>();
    List<ValidationMessage> messages = new ArrayList<>();
    for (String key : settings.keySet()) {
      Object value = settings.get(key);
      SettingEntity s = this.findById(key);
      if (s != null) {
        s.setValue(settings.get(key));
        Response response = this.save(s);
        if (response.getMessages() != null && !response.getMessages().isEmpty()) {
          messages.addAll(response.getMessages());
        } else {
          responseMap.put(key, value);
        }
      } else {
        ValidationMessage message = new ValidationMessage();
        message.setKey(null);
        message.setPropertyName(null);
        message.setMessageText("Setting " + key + " does not exist");
        messages.add(message);
      }
    }
    Response<Map<String, Object>> response = new Response<>();
    response.setModel(responseMap);
    response.setMessages(messages);
    return response;
  }

  public Map<String, Object> get(List<String> idList) {
    Map<String, Object> map = new HashMap<>();
    idList.forEach(id -> {
      SettingEntity setting = this.findById(id);
      Object value = setting != null ? setting.getValue() : null;
      map.put(id, value);
    });
    return map;
  }

  public Object getValue(String id) {
    return get(Collections.singletonList(id)).get(id);
  }

  public Object getAll() {
    SettingCriteria criteria = new SettingCriteria();
    criteria.setPageNumber(1);
    criteria.setPageSize(Integer.MAX_VALUE);
    PagedResponse<SettingEntity> response = search(criteria);
    return toMap(response.getResults());
  }

  private Map<String, Object> toMap(List<SettingEntity> list) {
    Map<String, Object> map = new HashMap<>();
    for (SettingEntity s : list) {
      map.put(s.getId(), s.getValue());
    }
    return map;
  }

  public PasswordDTO getPassword(@PathVariable(value = "type") String type) {

    if (!"Client".equals(serverMode)) {
      throw new AccessDeniedException("Can only display passwords when in Client Mode.");
    }

    switch (type) {
      case "COUCHBASE":
        PasswordDTO password = new PasswordDTO();
        password.setPassword(couchbaseAdminPassword);
        return password;
      case "BUCKET":
        PasswordDTO password2 = new PasswordDTO();
        password2.setPassword(couchbaseBucketPassword);
        return password2;
    }
    return null;
  }

  public String getHelpLink() {
    return (String) getValue("HELP");
  }

  public void saveWithThrow(SettingEntity entity) {
    ServiceUtils.throwIfRequired(save(entity), "Setting", entity.getId());
  }

}

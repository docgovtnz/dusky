package com.fronde.server.utils;

import com.fronde.server.domain.BaseEntity;
import com.fronde.server.services.authentication.MyUser;
import java.util.Date;

public class BaseEntityUtils {

  public static void setModifiedFields(BaseEntity entity) {
    MyUser user = SecurityUtils.currentUser();
    String personIdOrNull = user != null ? user.getPersonId() : null;
    Date modifiedTime = new Date();
    entity.setModifiedByPersonId(personIdOrNull);
    entity.setModifiedTime(modifiedTime);
  }

}

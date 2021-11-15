package com.fronde.server.utils;

import com.fronde.server.domain.response.Response;
import com.fronde.server.services.validation.ValidationMessage;

public class ServiceUtils {

  public static void throwIfRequired(Response<?> response, String entityName, String id) {
    if (!response.getMessages().isEmpty()) {
      String string = "";
      boolean first = true;
      for (ValidationMessage message : response.getMessages()) {
        if (first) {
          first = false;
        } else {
          string += ",";
        }
        string += "\"" + message.getMessageText() + "\"";
      }
      throw new RuntimeException(
          "Unable to save " + entityName + " (" + id + ") due to validation errors (" + string
              + ")");
    }
  }

}

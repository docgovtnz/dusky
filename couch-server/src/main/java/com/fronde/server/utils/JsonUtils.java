package com.fronde.server.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class JsonUtils {

  public String toJson(Object object) {
    try {
      String json = null;
      if (object != null) {
        ObjectMapper mapper = new ObjectMapper(); // Move to class field if this becomes a performance issue
        json = mapper.writeValueAsString(object);
      }
      return json;
    } catch (JsonProcessingException ex) {
      throw new RuntimeException(ex);
    }
  }

  public String toJsonPretty(Object object) {
    try {
      String json = null;
      if (object != null) {
        ObjectMapper mapper = new ObjectMapper(); // Move to class field if this becomes a performance issue
        json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
      }
      return json;
    } catch (JsonProcessingException ex) {
      throw new RuntimeException(ex);
    }
  }

  public <T> T toObject(String json, Class<T> objectClass) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      ObjectReader objectReader = mapper.readerFor(objectClass);
      T object = objectReader.readValue(json);
      return object;
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

}

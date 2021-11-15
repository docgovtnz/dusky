package com.fronde.server.services.meta;

import com.fronde.server.services.meta.domain.MetaClass;
import com.fronde.server.utils.XmlUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

@Component
public class MetaDataService {

  private final Map<String, MetaClass> metaClassMap = new HashMap<>();


  public MetaClass getMetaClass(String entityClassName) {
    MetaClass metaClass = metaClassMap.get(entityClassName);
    if (metaClass == null) {
      metaClass = loadMetaClass(entityClassName);
      metaClassMap.put(entityClassName, metaClass);
    }
    return metaClass;
  }

  private MetaClass loadMetaClass(String entityClassName) {
    try {
      String resourceName = "/meta/" + entityClassName + ".meta.xml";
      InputStream resourceAsStream = MetaDataService.class.getResourceAsStream(resourceName);

      if (resourceAsStream != null) {
        String xml = IOUtils.toString(resourceAsStream);
        MetaClass metaClass = XmlUtils.convertFromString(MetaClass.class, xml);
        return metaClass;
      } else {
        throw new RuntimeException("Unable to find meta class for " + entityClassName);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

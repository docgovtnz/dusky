package com.fronde.server.services.validation;

import com.fronde.server.domain.BaseEntity;
import com.fronde.server.services.meta.MetaDataService;
import com.fronde.server.services.meta.domain.MetaClass;
import com.fronde.server.services.validation.message.MessageTemplateLoader;
import com.fronde.server.services.validation.message.MessageTemplateMap;
import com.fronde.server.utils.XmlUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@Component
public class ValidationService {

  @Autowired
  private ApplicationContext appContext;

  @Autowired
  protected MetaDataService metaDataService;

  private final Map<String, DataValidationClass> dataValidationClassMap = new HashMap<>();
  private MessageTemplateMap messageTemplateMap;

  public MessageTemplateMap getMessageTemplateMap() {
    if (messageTemplateMap == null) {
      MessageTemplateLoader messageTemplateLoader = new MessageTemplateLoader();
      messageTemplateMap = messageTemplateLoader.getMessageTemplateMap();
    }
    return messageTemplateMap;
  }

  public DataValidationClass getDataValidationClass(String entityClassName) {
    try {

      DataValidationClass dataValidationClass = dataValidationClassMap.get(entityClassName);
      if (dataValidationClass == null) {
        String resourceName = "/validation/" + entityClassName + ".validation.xml";
        InputStream resourceAsStream = ValidationService.class.getResourceAsStream(resourceName);

        if (resourceAsStream != null) {
          String xml = IOUtils.toString(resourceAsStream);
          dataValidationClass = XmlUtils.convertFromString(DataValidationClass.class, xml);
          dataValidationClassMap.put(entityClassName, dataValidationClass);
        } else {
          // TODO: eventually we should throw this exception, but for now we will just return null
          //throw new RuntimeException("Unable to find validation rules for " + entityClassName);
        }
      }

      return dataValidationClass;
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public List<ValidationMessage> validateEntity(BaseEntity baseEntity) {
    ValidationAppContext.set(appContext);
    MetaClass metaClass = metaDataService.getMetaClass(baseEntity.getClass().getSimpleName());
    DataValidationClass dataValidationClass = getDataValidationClass(
        baseEntity.getClass().getSimpleName());

    List<ValidationMessage> messageList;
    if (dataValidationClass != null) {
      messageList = dataValidationClass.validate(baseEntity, metaClass, getMessageTemplateMap());
    } else {
      // TODO: this shouldn't be needed once everything has validation rules (see above)
      messageList = new ArrayList<>();
    }

    return messageList;
  }

}

package com.fronde.server.services.validation.message;

import com.fronde.server.utils.XmlUtils;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

@Component
public class MessageTemplateLoader {

  private MessageTemplateMap messageTemplateMap;

  public MessageTemplateLoader() {
  }

  public MessageTemplateMap getMessageTemplateMap() {
    try {
      if (messageTemplateMap == null) {
        String xml = IOUtils.toString(
            MessageTemplateLoader.class.getResourceAsStream("/validation/ValidationMessages.xml"));
        messageTemplateMap = XmlUtils.convertFromString(MessageTemplateMap.class, xml);
      }
      return messageTemplateMap;
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }


}

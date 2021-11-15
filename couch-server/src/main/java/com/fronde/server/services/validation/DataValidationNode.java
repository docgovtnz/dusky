package com.fronde.server.services.validation;

import com.fronde.server.services.meta.domain.MetaClass;
import com.fronde.server.services.validation.message.MessageTemplateMap;
import java.util.List;
import org.springframework.beans.BeanWrapper;

public interface DataValidationNode {

  void validateNode(Object rootObject, BeanWrapper currentObjectWrapper, MetaClass metaClass,
      MessageTemplateMap messageTemplateMap, List<ValidationMessage> messageList);
}

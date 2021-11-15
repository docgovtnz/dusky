package com.fronde.server.services.validation;

import com.fronde.server.services.meta.domain.MetaProperty;
import com.fronde.server.services.validation.message.MessageTemplate;
import com.fronde.server.services.validation.message.RuleMessageMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationResultFactory {

  private final MetaProperty metaProperty;
  private RuleMessageMap ruleMessageMap;

  private final List<ValidationMessage> messageList;
  private final Map<String, ValidationMessage> messageMap = new HashMap<>();

  public ValidationResultFactory(MetaProperty metaProperty, List<ValidationMessage> messageList) {
    this.metaProperty = metaProperty;
    this.messageList = messageList;
  }

  public void setRuleMessageMap(RuleMessageMap ruleMessageMap) {
    this.ruleMessageMap = ruleMessageMap;
  }

  public ResultWrapper addResult(String key) {

    MessageTemplate messageTemplate = this.ruleMessageMap.getMap().get(key);

    ValidationMessage message = messageMap.get(key);
    if (message == null) {
      message = new ValidationMessage();
      message.setKey(messageTemplate.getKey());
      message.setMessageText(messageTemplate.getMessageText());
      message.setPropertyName(metaProperty.getName());

      messageList.add(message);
      messageMap.put(key, message);
    }

    return new ResultWrapper(metaProperty.getLabel(), message);
  }

  public static class ResultWrapper {

    private final ValidationMessage message;

    ResultWrapper(String propertyLabel, ValidationMessage message) {
      this.message = message;
      with("propertyName", "'" + propertyLabel + "'");
    }

    public ResultWrapper with(String name, Object value) {
      message.getMessageParameters().put(name, value);
      this.updateMessageText();
      return this;
    }

    private void updateMessageText() {
      message.getMessageParameters().keySet().forEach(parameterKey -> {

        Object parameterValue = message.getMessageParameters().get(parameterKey);
        String parameterText = parameterValue != null ? parameterValue.toString() : "";

        String searchMarker = "{" + parameterKey + "}";
        message.setMessageText(message.getMessageText().replace(searchMarker, parameterText));
      });
    }
  }
}

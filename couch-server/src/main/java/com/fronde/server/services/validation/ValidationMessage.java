package com.fronde.server.services.validation;

import java.util.HashMap;
import java.util.Map;

public class ValidationMessage {

  private String key;
  private String messageText;
  private String propertyName;
  private Map<String, Object> messageParameters;

  public ValidationMessage() {
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getMessageText() {
    return messageText;
  }

  public void setMessageText(String messageText) {
    this.messageText = messageText;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  public Map<String, Object> getMessageParameters() {
    if (messageParameters == null) {
      messageParameters = new HashMap<>();
    }
    return messageParameters;
  }

  public void setMessageParameters(Map<String, Object> messageParameters) {
    this.messageParameters = messageParameters;
  }

  @Override
  public String toString() {
    return "ValidationMessage{" +
        "key='" + key + '\'' +
        ", messageText='" + messageText + '\'' +
        ", propertyName='" + propertyName + '\'' +
        '}';
  }
}

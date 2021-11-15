package com.fronde.server.domain.response;

import com.fronde.server.services.validation.ValidationMessage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AbstractResponse<T> {

  private List<ValidationMessage> messages = new ArrayList<>();

  public List<ValidationMessage> getMessages() {
    return messages;
  }

  public void setMessages(List<ValidationMessage> messages) {
    this.messages = messages;
  }
}

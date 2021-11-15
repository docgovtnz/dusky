package com.fronde.server.services.validation.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class RuleMessageMap {

  @XmlAttribute
  private String name;

  @XmlElements({
      @XmlElement(name = "message", type = MessageTemplate.class)
  })
  private List<MessageTemplate> messageTemplateList;

  @XmlTransient
  private Map<String, MessageTemplate> map;

  public RuleMessageMap() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonIgnore
  public List<MessageTemplate> getMessageTemplateList() {
    return messageTemplateList;
  }

  public void setMessageTemplateList(List<MessageTemplate> messageTemplateList) {
    this.messageTemplateList = messageTemplateList;
  }

  public Map<String, MessageTemplate> getMap() {
    if (map == null) {
      map = new HashMap<>();
      messageTemplateList.forEach(messageTemplate -> {
        map.put(messageTemplate.getKey(), messageTemplate);
      });
    }
    return map;
  }

  public void setMap(Map<String, MessageTemplate> map) {
    this.map = map;
  }
}

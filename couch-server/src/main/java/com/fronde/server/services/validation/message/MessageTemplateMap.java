package com.fronde.server.services.validation.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "MessageTemplateMap")
@XmlAccessorType(XmlAccessType.FIELD)
public class MessageTemplateMap {

  @XmlElements({
      @XmlElement(name = "Rule", type = RuleMessageMap.class)
  })
  private List<RuleMessageMap> ruleMessageList;

  @XmlTransient
  private Map<String, RuleMessageMap> map;

  public MessageTemplateMap() {
  }

  @JsonIgnore
  public List<RuleMessageMap> getRuleMessageList() {
    return ruleMessageList;
  }

  public void setRuleMessageList(List<RuleMessageMap> ruleMessageList) {
    this.ruleMessageList = ruleMessageList;
  }

  public Map<String, RuleMessageMap> getMap() {
    if (map == null) {
      map = new HashMap<>();
      getRuleMessageList().forEach(ruleMessageMap -> {
        map.put(ruleMessageMap.getName(), ruleMessageMap);
      });
    }
    return map;
  }

  public void setMap(Map<String, RuleMessageMap> map) {
    this.map = map;
  }
}

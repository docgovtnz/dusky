package com.fronde.server.services.meta.domain;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class DisplayGroup {

  @XmlElements({
      @XmlElement(name = "DisplayProperty", type = DisplayProperty.class),
  })
  List<DisplayProperty> properties;

  public DisplayGroup() {
  }

  public List<DisplayProperty> getProperties() {
    return properties;
  }

  public void setProperties(List<DisplayProperty> properties) {
    this.properties = properties;
  }
}

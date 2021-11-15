package com.fronde.server.services.meta.domain;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class DisplayModel {

  @XmlAttribute
  private String layoutType;

  @XmlElements({
      @XmlElement(name = "DisplayGroup", type = DisplayGroup.class),
  })
  List<DisplayGroup> groups;

  public DisplayModel() {
  }

  public String getLayoutType() {
    return layoutType;
  }

  public void setLayoutType(String layoutType) {
    this.layoutType = layoutType;
  }

  public List<DisplayGroup> getGroups() {
    return groups;
  }

  public void setGroups(List<DisplayGroup> groups) {
    this.groups = groups;
  }
}

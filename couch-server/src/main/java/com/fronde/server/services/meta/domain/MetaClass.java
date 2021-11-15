package com.fronde.server.services.meta.domain;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Class")
@XmlAccessorType(XmlAccessType.FIELD)
public class MetaClass {

  @XmlAttribute
  private String name;

  @XmlElements({
      @XmlElement(name = "Property", type = MetaProperty.class),
  })
  private List<MetaProperty> metaProperties;


  @XmlElements({
      @XmlElement(name = "DisplayModel", type = DisplayModel.class),
  })
  private DisplayModel displayModel;

  public MetaClass() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<MetaProperty> getMetaProperties() {
    return metaProperties;
  }

  public void setMetaProperties(List<MetaProperty> metaProperties) {
    this.metaProperties = metaProperties;
  }

  public DisplayModel getDisplayModel() {
    return displayModel;
  }

  public void setDisplayModel(DisplayModel displayModel) {
    this.displayModel = displayModel;
  }

  public MetaProperty findMetaProperty(String propertyName) {
    MetaProperty metaProperty = null;
    for (MetaProperty nextProperty : getMetaProperties()) {
      if (nextProperty.getName().equals(propertyName)) {
        metaProperty = nextProperty;
        break;
      }
    }

    if (metaProperty != null) {
      return metaProperty;
    } else {
      throw new RuntimeException("Unable to find metaProperty for " + propertyName);
    }
  }
}

package com.fronde.server.services.meta.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class MetaProperty {

  @XmlAttribute
  private String name;

  @XmlAttribute
  private String label;

  @XmlAttribute
  private String javaType;

  @XmlAttribute
  private String jsonType;

  @XmlElements({
      @XmlElement(name = "Class", type = MetaClass.class),
  })
  private MetaClass metaClass;

  public MetaProperty() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getJavaType() {
    return javaType;
  }

  public void setJavaType(String javaType) {
    this.javaType = javaType;
  }

  public String getJsonType() {
    return jsonType;
  }

  public void setJsonType(String jsonType) {
    this.jsonType = jsonType;
  }

  public MetaClass getMetaClass() {
    return metaClass;
  }

  public void setMetaClass(MetaClass metaClass) {
    this.metaClass = metaClass;
  }
}

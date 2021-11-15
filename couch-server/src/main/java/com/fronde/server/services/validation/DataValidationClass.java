package com.fronde.server.services.validation;

import com.fronde.server.services.meta.domain.MetaClass;
import com.fronde.server.services.validation.message.MessageTemplateMap;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;


@XmlRootElement(name = "Class")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataValidationClass implements DataValidationNode {

  @XmlAttribute
  private String targetClassName;

  @XmlAttribute
  private String context;

  @XmlElements({
      @XmlElement(name = "Class", type = DataValidationClass.class),
      @XmlElement(name = "Property", type = DataValidationProperty.class)
  })
  private List<DataValidationNode> nodeList;

  public DataValidationClass() {
  }

  public String getTargetClassName() {
    return targetClassName;
  }

  public void setTargetClassName(String targetClassName) {
    this.targetClassName = targetClassName;
  }

  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public List<DataValidationNode> getNodeList() {
    if (nodeList == null) {
      nodeList = new ArrayList<>();
    }
    return nodeList;
  }

  public List<ValidationMessage> validate(Object rootObject, MetaClass metaClass,
      MessageTemplateMap messageTemplateMap) {

    BeanWrapper currentObjectWrapper = new BeanWrapperImpl(rootObject);
    List<ValidationMessage> messageList = new ArrayList<>();

    validateNode(rootObject, currentObjectWrapper, metaClass, messageTemplateMap, messageList);

    return messageList;
  }


  public void validateNode(Object rootObject, BeanWrapper currentObjectWrapper, MetaClass metaClass,
      MessageTemplateMap messageTemplateMap, List<ValidationMessage> messageList) {
    getNodeList().forEach(node -> {
      node.validateNode(rootObject, currentObjectWrapper, metaClass, messageTemplateMap,
          messageList);
    });
  }

}

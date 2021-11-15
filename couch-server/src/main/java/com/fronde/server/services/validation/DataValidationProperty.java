package com.fronde.server.services.validation;

import com.fronde.server.services.meta.domain.MetaClass;
import com.fronde.server.services.meta.domain.MetaProperty;
import com.fronde.server.services.validation.message.MessageTemplateMap;
import com.fronde.server.services.validation.message.RuleMessageMap;
import com.fronde.server.services.validation.rule.AbstractRule;
import com.fronde.server.services.validation.rule.BirdUniqueHouseIDRule;
import com.fronde.server.services.validation.rule.BirdUniqueNameRule;
import com.fronde.server.services.validation.rule.CaptureRecordRule;
import com.fronde.server.services.validation.rule.CheckmateDataRule;
import com.fronde.server.services.validation.rule.ChemistryAssayValidationRule;
import com.fronde.server.services.validation.rule.DateFutureRule;
import com.fronde.server.services.validation.rule.DatePastRule;
import com.fronde.server.services.validation.rule.EastingAndNorthingRule;
import com.fronde.server.services.validation.rule.EastingIslandBoundsRule;
import com.fronde.server.services.validation.rule.EastingRule;
import com.fronde.server.services.validation.rule.EmailRule;
import com.fronde.server.services.validation.rule.FeedOutRule;
import com.fronde.server.services.validation.rule.HaematologyTestValidationRule;
import com.fronde.server.services.validation.rule.HandRaiseMedicationRule;
import com.fronde.server.services.validation.rule.IslandMustExistRule;
import com.fronde.server.services.validation.rule.IslandUniqueIdRule;
import com.fronde.server.services.validation.rule.IslandUniqueNameRule;
import com.fronde.server.services.validation.rule.TxMortalityUniqueNameRule;
import com.fronde.server.services.validation.rule.LocationTypeTargetBirdRule;
import com.fronde.server.services.validation.rule.LocationUniqueNameRule;
import com.fronde.server.services.validation.rule.MicrobiologyAndParasitologyTestValidationRule;
import com.fronde.server.services.validation.rule.MotherTripListValidationRule;
import com.fronde.server.services.validation.rule.NestChickValidationRule;
import com.fronde.server.services.validation.rule.NestEggValidationRule;
import com.fronde.server.services.validation.rule.NestTypeMandatoryClutchRule;
import com.fronde.server.services.validation.rule.NoraNetActivityRule;
import com.fronde.server.services.validation.rule.NoraNetCheckMateRule;
import com.fronde.server.services.validation.rule.NorthingRule;
import com.fronde.server.services.validation.rule.NumberRangeRule;
import com.fronde.server.services.validation.rule.ObservationRoleRequiredRule;
import com.fronde.server.services.validation.rule.ObservationTimeValidationRule;
import com.fronde.server.services.validation.rule.ObserverRequiredRule;
import com.fronde.server.services.validation.rule.PersonHasAccountEnabled;
import com.fronde.server.services.validation.rule.PersonUniqueNameRule;
import com.fronde.server.services.validation.rule.PersonUniqueUsernameRule;
import com.fronde.server.services.validation.rule.RelativeHumidityRule;
import com.fronde.server.services.validation.rule.RequiredRule;
import com.fronde.server.services.validation.rule.SpermMeasureValidationRule;
import com.fronde.server.services.validation.rule.StationValidRule;
import com.fronde.server.services.validation.rule.TransferRecordIslandRule;
import com.fronde.server.services.validation.rule.TransferRecordLocationIDRule;
import com.fronde.server.services.validation.rule.TransferRecordToIslandRule;
import com.fronde.server.services.validation.rule.TransferRecordToLocationIDRule;
import com.fronde.server.services.validation.rule.TransmitterUhfIdRule;
import com.fronde.server.services.validation.rule.TransmitterUniqueTxIdRule;
import com.fronde.server.services.validation.rule.UsernameRule;
import com.fronde.server.services.validation.rule.YearRangeRule;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class DataValidationProperty implements DataValidationNode {

  @XmlAttribute
  private String name;

  @XmlElements({
      @XmlElement(name = "RequiredRule", type = RequiredRule.class),
      @XmlElement(name = "DateFutureRule", type = DateFutureRule.class),
      @XmlElement(name = "DatePastRule", type = DatePastRule.class),
      @XmlElement(name = "EmailRule", type = EmailRule.class),
      @XmlElement(name = "NumberRangeRule", type = NumberRangeRule.class),
      @XmlElement(name = "PersonHasAccountEnabled", type = PersonHasAccountEnabled.class),
      @XmlElement(name = "TransferRecordIslandRule", type = TransferRecordIslandRule.class),
      @XmlElement(name = "TransferRecordLocationIDRule", type = TransferRecordLocationIDRule.class),
      @XmlElement(name = "CheckmateDataRule", type = CheckmateDataRule.class),
      @XmlElement(name = "TransferRecordToIslandRule", type = TransferRecordToIslandRule.class),
      @XmlElement(name = "TransferRecordToLocationIDRule", type = TransferRecordToLocationIDRule.class),
      @XmlElement(name = "ObserverRequiredRule", type = ObserverRequiredRule.class),
      @XmlElement(name = "ObservationRoleRequiredRule", type = ObservationRoleRequiredRule.class),
      @XmlElement(name = "NestEggValidationRule", type = NestEggValidationRule.class),
      @XmlElement(name = "NestChickValidationRule", type = NestChickValidationRule.class),
      @XmlElement(name = "MotherTripListValidationRule", type = MotherTripListValidationRule.class),
      @XmlElement(name = "ObservationTimeValidationRule", type = ObservationTimeValidationRule.class),
      @XmlElement(name = "EastingRule", type = EastingRule.class),
      @XmlElement(name = "NorthingRule", type = NorthingRule.class),
      @XmlElement(name = "EastingAndNorthingRule", type = EastingAndNorthingRule.class),
      @XmlElement(name = "EastingIslandBoundsRule", type = EastingIslandBoundsRule.class),
      @XmlElement(name = "LocationTypeTargetBirdRule", type = LocationTypeTargetBirdRule.class),
      @XmlElement(name = "NestTypeMandatoryClutchRule", type = NestTypeMandatoryClutchRule.class),
      @XmlElement(name = "BirdUniqueNameRule", type = BirdUniqueNameRule.class),
      @XmlElement(name = "UsernameRule", type = UsernameRule.class),
      @XmlElement(name = "HaematologyTestValidationRule", type = HaematologyTestValidationRule.class),
      @XmlElement(name = "ChemistryAssayValidationRule", type = ChemistryAssayValidationRule.class),
      @XmlElement(name = "MicrobiologyAndParasitologyTestValidationRule", type = MicrobiologyAndParasitologyTestValidationRule.class),
      @XmlElement(name = "SpermMeasureValidationRule", type = SpermMeasureValidationRule.class),
      @XmlElement(name = "FeedOutRule", type = FeedOutRule.class),
      @XmlElement(name = "CaptureRecordRule", type = CaptureRecordRule.class),
      @XmlElement(name = "HandRaiseMedicationRule", type = HandRaiseMedicationRule.class),
      @XmlElement(name = "BirdUniqueHouseIDRule", type = BirdUniqueHouseIDRule.class),
      @XmlElement(name = "PersonUniqueNameRule", type = PersonUniqueNameRule.class),
      @XmlElement(name = "PersonUniqueUsernameRule", type = PersonUniqueUsernameRule.class),
      @XmlElement(name = "LocationUniqueNameRule", type = LocationUniqueNameRule.class),
      @XmlElement(name = "TransmitterUniqueTxIdRule", type = TransmitterUniqueTxIdRule.class),
      @XmlElement(name = "TransmitterUhfIdRule", type = TransmitterUhfIdRule.class),
      @XmlElement(name = "IslandUniqueIdRule", type = IslandUniqueIdRule.class),
      @XmlElement(name = "IslandUniqueNameRule", type = IslandUniqueNameRule.class),
      @XmlElement(name = "TxMortalityUniqueNameRule", type = TxMortalityUniqueNameRule.class),
      @XmlElement(name = "NoraNetActivityRule", type = NoraNetActivityRule.class),
      @XmlElement(name = "NoraNetCheckMateRule", type = NoraNetCheckMateRule.class),
      @XmlElement(name = "IslandMustExistRule", type = IslandMustExistRule.class),
      @XmlElement(name = "StationValidRule", type = StationValidRule.class),
      @XmlElement(name = "RelativeHumidityRule", type = RelativeHumidityRule.class),
      @XmlElement(name = "YearRangeRule", type = YearRangeRule.class)
  })
  private List<AbstractRule> ruleList;

  @XmlElements({
      @XmlElement(name = "Class", type = DataValidationClass.class)
  })
  private DataValidationClass dataValidationClass;

  public DataValidationProperty() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<AbstractRule> getRuleList() {
    if (ruleList == null) {
      ruleList = new ArrayList<>();
    }
    return ruleList;
  }


  public void validateNode(Object rootObject, BeanWrapper currentObjectWrapper, MetaClass metaClass,
      MessageTemplateMap messageTemplateMap, List<ValidationMessage> messageList) {

    Object propertyValue = currentObjectWrapper.getPropertyValue(name);
    MetaProperty metaProperty = metaClass.findMetaProperty(name);
    ValidationResultFactory resultFactory = new ValidationResultFactory(metaProperty, messageList);

    getRuleList().forEach(rule -> {
      RuleMessageMap ruleMessageMap = messageTemplateMap.getMap()
          .get(rule.getClass().getSimpleName());
      resultFactory.setRuleMessageMap(ruleMessageMap);
      rule.validate(rootObject, propertyValue, resultFactory);
    });

    if (dataValidationClass != null && propertyValue != null) {
      if (propertyValue instanceof Collection) {
        for (Object item : (Collection) propertyValue) {
          BeanWrapper nextWrapper = new BeanWrapperImpl(item);
          MetaClass nextMetaClass = metaProperty.getMetaClass();
          dataValidationClass
              .validateNode(rootObject, nextWrapper, nextMetaClass, messageTemplateMap,
                  messageList);
        }
      } else {
        BeanWrapper nextWrapper = new BeanWrapperImpl(propertyValue);
        MetaClass nextMetaClass = metaProperty.getMetaClass();
        dataValidationClass
            .validateNode(rootObject, nextWrapper, nextMetaClass, messageTemplateMap, messageList);
      }
    }
  }

  public DataValidationClass getDataValidationClass() {
    return dataValidationClass;
  }

}

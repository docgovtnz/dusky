package com.fronde.server.services.validation.rule;

    import com.fronde.server.domain.TxMortalityEntity;
    import com.fronde.server.services.txmortality.TxMortalityService;
    import com.fronde.server.services.validation.ValidationAppContext;
    import com.fronde.server.services.validation.ValidationResultFactory;

public class TxMortalityUniqueNameRule extends AbstractRule<String> {

  @Override
  public void validate(Object object, String propertyValue,
      ValidationResultFactory resultFactory) {

    TxMortalityEntity mortality = (TxMortalityEntity) object;
    if (!isUniqueMortalityName(mortality.getId(), propertyValue)) {
      resultFactory.addResult("IsNameNotUnique");
    }
  }

  protected boolean isUniqueMortalityName(String metaID, String islandName) {
    TxMortalityService service = ValidationAppContext.get().getBean(TxMortalityService.class);
    return service.isUniqueName(metaID, islandName);
  }

}

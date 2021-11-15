import { ValidationService } from './validation.service';
import { MetaClass } from '../meta/domain/meta-class';
import { BaseEntity } from '../domain/base-entity';
import {
  FormGroup,
  FormBuilder,
  FormArray,
  Validators,
  FormControl,
} from '@angular/forms';
import { DateFutureRule } from './validators/date-future-rule';
import { DatePastRule } from './validators/date-past-rule';
import { EmailRule } from './validators/email-rule';
import { NumberRangeRule } from './validators/number-range-rule';

// TODO add ability to efficiently patch and existing form and rename to something appropriate
// at the moment editor components recreate the form from scratch using createForm which may not be so performant
export class BaseEntityFormFactory {
  constructor(
    private metaClass: MetaClass,
    private dataValidationClass: any,
    private fb: FormBuilder,
    private validationService: ValidationService
  ) {}

  private static findRuleNode(propertyName: string, nodeList: any[]): any {
    let node = null;
    for (const nextNode of nodeList) {
      if (nextNode.name === propertyName) {
        node = nextNode;
        break;
      }
    }
    return node;
  }

  public createForm(entity: BaseEntity): FormGroup {
    const form: FormGroup = this.fb.group({});
    this.recursiveCreateForm(
      entity,
      form,
      this.metaClass,
      this.dataValidationClass
    );
    return form;
  }

  private recursiveCreateForm(
    entity: BaseEntity,
    form: FormGroup,
    metaClass: MetaClass,
    dataValidationClass: any
  ): void {
    // find and set form level validators (ones attached to the form, not a property
    // ...todo...
    // form.setValidators(validators);

    // Populate all of the DisplayProperties (may not be all of the properties of a class)
    this.populateDisplayProperties(form, metaClass, dataValidationClass);

    // Loop through all of the class properties looking for any property that has a nested MetaClass and recursively
    // step down into it.
    for (const metaProperty of metaClass.metaProperties) {
      if (metaProperty.metaClass && metaProperty.metaClass.displayModel) {
        const layoutType = metaProperty.metaClass.displayModel.layoutType;
        // We only need to create child forms if the layoutType of the child form is using the new Form approach
        if (layoutType === 'Panel_NEW' || layoutType === 'Table') {
          // grab the metaClass from the property
          const nextMetaClass = metaProperty.metaClass;

          // find the next level node from the DataValidationClass
          let nextDataValidationClass = null;
          if (dataValidationClass !== null) {
            const node = dataValidationClass.nodeList.find(
              (i) => i.name === metaProperty.name
            );
            if (!node || !node.dataValidationClass) {
              console.log(
                'WARNING: no data validation class for property ' +
                  metaProperty.name
              );
            } else {
              nextDataValidationClass = node.dataValidationClass;
            }
          }
          (form as any)[metaProperty.name + 'Add'] = (
            nextEntity?
          ): FormGroup => {
            if (metaProperty.javaType.indexOf('List<') < 0) {
              // create the nextFormGroup (the child form)
              const nextForm: FormGroup = this.fb.group({});
              form.setControl(metaProperty.name, nextForm);
              // recursively set into the next level of the data structures
              if (nextEntity) {
                this.recursiveCreateForm(
                  nextEntity,
                  nextForm,
                  nextMetaClass,
                  nextDataValidationClass
                );
              } else {
                this.recursiveCreateForm(
                  {} as BaseEntity,
                  nextForm,
                  nextMetaClass,
                  nextDataValidationClass
                );
              }
              return nextForm;
            } else {
              // create the nextFormArray (the child form)
              const formArray: FormArray = this.fb.array([]);
              form.setControl(metaProperty.name, formArray);
              (formArray as any).addItem = (
                nextItem: any,
                originalIndex: number = null
              ): FormGroup => {
                const nextForm: FormGroup = this.fb.group({});
                // recursively set into the next level of the data structures
                if (!nextItem) {
                  nextItem = {};
                }
                // record the original index in a hidden field if there is one, otherwise set it to null
                // this lets us track if an item in the array corresponds to an existing item (using the original index to look it up)
                const originalIndexControl = new FormControl({
                  value: originalIndex,
                  disabled: true,
                });
                nextForm.setControl('_originalIndex', originalIndexControl);
                this.recursiveCreateForm(
                  nextItem,
                  nextForm,
                  nextMetaClass,
                  nextDataValidationClass
                );
                formArray.push(nextForm);
                // Set the intial value to work around a ExpressionChangedAfterItHasBeenCheckedError.
                // For whatever reason the valid status of the items added to the array
                // is only realised when rendering the input or html element backed by the item.
                // This works around this by forcing validation to be done earlier so the valid status
                // is correct when rendering parent html elements of the input.
                nextMetaClass.displayModel.groups.forEach((group) =>
                  group.properties.forEach((mp) => {
                    if (nextForm.controls[mp.name].value === null) {
                      nextForm.controls[mp.name].setValue(null);
                    }
                  })
                );
                return nextForm;
              };
              if (nextEntity) {
                nextEntity.forEach((item, i) => {
                  (formArray as any).addItem(item, i);
                });
              }
            }
          };
          (form as any)[metaProperty.name + 'Remove'] = () => {
            form.removeControl(metaProperty.name);
          };
          // If there is a nested child MetaClass, but the entity has no value for this property we don't want to create the sub-form
          // because patchValue() can't handle nulls
          const propertyValue = entity[metaProperty.name];
          if (propertyValue) {
            (form as any)[metaProperty.name + 'Add'](propertyValue);
          }
        }
      }
    }
  }

  private populateDisplayProperties(
    form: FormGroup,
    metaClass: MetaClass,
    dataValidationClass: any
  ) {
    for (const group of metaClass.displayModel.groups) {
      for (const displayProperty of group.properties) {
        const propertyName = displayProperty.name;
        form.setControl(propertyName, this.fb.control(null));

        if (dataValidationClass && dataValidationClass.nodeList) {
          this.populateValidators(form, dataValidationClass, propertyName);
        }
      }
    }
  }

  private populateValidators(
    form: FormGroup,
    dataValidationClass: any,
    propertyName: string
  ) {
    const ruleNode = BaseEntityFormFactory.findRuleNode(
      propertyName,
      dataValidationClass.nodeList
    );
    if (ruleNode) {
      const validators = [];
      const ruleList = ruleNode.ruleList;
      for (let j = 0; j < ruleList.length; j++) {
        const nextRule = ruleList[j];
        const validator = this.createValidator(nextRule);
        if (validator !== null) {
          validators.push(validator);
        }
      }

      form.controls[propertyName].setValidators(validators);
    }
  }

  private createValidator(ruleNode: any): any {
    let validator = null;
    if (ruleNode['RequiredRule']) {
      validator = Validators.required;
    } else if (ruleNode['DateFutureRule']) {
      validator = new DateFutureRule(this.validationService);
    } else if (ruleNode['DatePastRule']) {
      validator = new DatePastRule(this.validationService);
    } else if (ruleNode['EmailRule']) {
      validator = new EmailRule(this.validationService);
    } else if (ruleNode['YearRangeRule']) {
      const numberRangeRule = new NumberRangeRule(this.validationService);
      numberRangeRule.min = 1001;
      const dt = new Date();
      numberRangeRule.max = dt.getFullYear();
      numberRangeRule.wholeNumber = true;
      validator = numberRangeRule;
    } else if (ruleNode['NumberRangeRule']) {
      const numberRangeRule = new NumberRangeRule(this.validationService);
      numberRangeRule.min = ruleNode['NumberRangeRule'].min;
      numberRangeRule.max = ruleNode['NumberRangeRule'].max;
      numberRangeRule.wholeNumber = ruleNode['NumberRangeRule'].wholeNumber;
      validator = numberRangeRule;
    } else {
      console.log('Got unknown Rule of ' + JSON.stringify(ruleNode));
    }

    return validator;
  }
}

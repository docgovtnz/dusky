import { AbstractControl, ValidationErrors } from '@angular/forms';
import { AbstractRule } from './abstract-rule';
import { ValidationResultFactory } from './validation-result-factory';

export class NumberRangeRule extends AbstractRule {
  min: number;
  max: number;
  wholeNumber: boolean;

  validate(control: AbstractControl): ValidationErrors | null {
    const resultFactory = new ValidationResultFactory(
      'NumberRangeRule',
      this.validationService
    );

    // You can't just do "if(control.value)" because Zero gets treated as "falsey"
    if (control.value !== null) {
      const numberValue = Number(control.value);

      // can't use "if(this.min)" because Zero is false and would bypass the check
      if (this.min !== null) {
        if (numberValue < this.min) {
          resultFactory.addResultWithParams('LessThanMinValue', {
            min: this.min,
          });
        }
      }

      if (this.max && numberValue > this.max) {
        resultFactory.addResultWithParams('GreaterThanMaxValue', {
          max: this.max,
        });
      }

      if (this.wholeNumber) {
        if (numberValue !== Math.floor(numberValue)) {
          resultFactory.addResult('WholeNumber');
        }
      }
    }

    return resultFactory.validationErrors;
  }
}

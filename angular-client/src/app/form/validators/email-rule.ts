import { AbstractControl, ValidationErrors } from '@angular/forms';
import { AbstractRule } from './abstract-rule';
import { ValidationResultFactory } from './validation-result-factory';

export class EmailRule extends AbstractRule {
  validate(control: AbstractControl): ValidationErrors | null {
    const resultFactory = new ValidationResultFactory(
      'EmailRule',
      this.validationService
    );

    if (control.value) {
      if (control.value.indexOf('@') < 0) {
        resultFactory.addResult('InvalidEmail');
      }
    }

    return resultFactory.validationErrors;
  }
}

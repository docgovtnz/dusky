import { AbstractControl, ValidationErrors } from '@angular/forms';
import * as moment from 'moment';
import { AbstractRule } from './abstract-rule';
import { ValidationResultFactory } from './validation-result-factory';

export class DatePastRule extends AbstractRule {
  validate(control: AbstractControl): ValidationErrors | null {
    const resultFactory = new ValidationResultFactory(
      'DatePastRule',
      this.validationService
    );

    if (control.value) {
      if (!moment(control.value).isBefore(moment())) {
        resultFactory.addResult('DatePast');
      }
    }

    return resultFactory.validationErrors;
  }
}

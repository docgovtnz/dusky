import { AbstractControl, ValidationErrors } from '@angular/forms';
import * as moment from 'moment';
import { AbstractRule } from './abstract-rule';
import { ValidationResultFactory } from './validation-result-factory';

export class DateFutureRule extends AbstractRule {
  validate(control: AbstractControl): ValidationErrors | null {
    const resultFactory = new ValidationResultFactory(
      'DateFutureRule',
      this.validationService
    );

    if (control.value) {
      if (!moment(control.value).isAfter(moment())) {
        resultFactory.addResult('DateFuture');
      }
    }

    return resultFactory.validationErrors;
  }
}

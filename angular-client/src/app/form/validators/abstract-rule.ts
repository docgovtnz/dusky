import { ValidationService } from '../validation.service';
import { AbstractControl, ValidationErrors, Validator } from '@angular/forms';

export abstract class AbstractRule implements Validator {
  constructor(protected validationService: ValidationService) {}

  abstract validate(c: AbstractControl): ValidationErrors | null;
}

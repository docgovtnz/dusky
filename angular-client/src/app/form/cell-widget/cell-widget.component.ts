import { Component, Input } from '@angular/core';
import { AbstractControl } from '@angular/forms';

@Component({
  selector: 'app-cell-widget',
  templateUrl: './cell-widget.component.html',
})
export class CellWidgetComponent {
  @Input()
  myFormControl: AbstractControl;

  constructor() {}

  isRequired(): boolean {
    return hasRequiredField(this.myFormControl);
  }
}

// https://stackoverflow.com/questions/39819123/angular2-find-out-if-formcontrol-has-required-validator
export const hasRequiredField = (abstractControl: AbstractControl): boolean => {
  if (abstractControl.validator) {
    const validator = abstractControl.validator({} as AbstractControl);
    if (validator && validator.required) {
      return true;
    }
  }
  if (abstractControl['controls']) {
    for (const controlName in abstractControl['controls']) {
      if (abstractControl['controls'][controlName]) {
        if (hasRequiredField(abstractControl['controls'][controlName])) {
          return true;
        }
      }
    }
  }
  return false;
};

import { Component, Input, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-message-display',
  templateUrl: './message-display.component.html',
})
export class MessageDisplayComponent implements OnInit {
  @Input()
  myFormControl: FormControl;

  constructor() {}

  ngOnInit() {}

  getErrorMessages(): string[] {
    const errors = this.myFormControl.errors;
    const errorMessages = [];
    if (errors) {
      for (const nextKey of Object.keys(errors)) {
        if (nextKey === 'required') {
          errorMessages.push('Value is required');
        } else {
          errorMessages.push(errors[nextKey]);
        }
      }
    }
    return errorMessages;
  }
}

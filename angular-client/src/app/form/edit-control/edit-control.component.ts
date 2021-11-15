import { Component, forwardRef, Input, OnInit } from '@angular/core';
import {
  ControlValueAccessor,
  FormControl,
  NG_VALUE_ACCESSOR,
} from '@angular/forms';

@Component({
  selector: 'app-edit-control',
  templateUrl: './edit-control.component.html',
  styleUrls: ['./edit-control.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => EditControlComponent),
      multi: true,
    },
  ],
})
export class EditControlComponent implements OnInit, ControlValueAccessor {
  @Input()
  label: string;

  @Input()
  propertyName: string;

  @Input()
  isRequired: boolean;

  @Input()
  inputType: string;

  @Input()
  myFormControl: FormControl;

  value: any;

  // the method set in registerOnChange, it is just
  // a placeholder for a method that takes one parameter,
  // we use it to emit changes back to the form
  private propagateChange = (_: any) => {};

  constructor() {}

  ngOnInit() {}

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {}

  setDisabledState(isDisabled: boolean): void {}

  writeValue(obj: any): void {
    this.value = obj;
  }

  onValueChange(nextValue: any) {
    // There's some fiddly little timing behaviour here. If the form control is initialised with null then at this
    // for this change we don't want to propagate the change. This seems to be the best way of dealing with the user
    // tabbing into the field and not having the input field at this level lose it's pristine status.
    console.log(
      'On value changed: "' + this.value + '" -> "' + nextValue + '"'
    );
    if (this.value !== null) {
      this.propagateChange(this.value);
    }
  }
}

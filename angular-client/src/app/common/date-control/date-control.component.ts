import { Component, forwardRef, Input, OnInit, ViewChild } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import * as moment from 'moment';
import { BsDatepickerDirective } from 'ngx-bootstrap/datepicker';

@Component({
  selector: 'app-date-control',
  templateUrl: './date-control.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DateControlComponent),
      multi: true,
    },
  ],
})
export class DateControlComponent implements OnInit, ControlValueAccessor {
  @ViewChild(BsDatepickerDirective) datepicker: BsDatepickerDirective;

  private _dateValue: Date;
  private _dateInputValue: Date;

  @Input()
  id: string;

  @Input()
  editorCfg: string;

  @Input()
  disabled = false;

  @Input()
  autofocus = false;

  get dateValue(): Date {
    return this._dateValue;
  }

  set dateValue(value: Date) {
    if (!value) {
      if (this._dateValue) {
        // only set the values to null if the date time was changed.
        // this preserves any invalid input in the time input value
        this._dateValue = null;
        this._dateInputValue = null;
      }
    } else {
      this._dateValue = value;
      this._dateInputValue = moment(value).startOf('day').toDate();
    }
  }

  get dateInputValue(): Date {
    return this._dateInputValue;
  }

  set dateInputValue(value: Date) {
    if (value) {
      this._dateInputValue = value;
    } else {
      this._dateInputValue = null;
    }
    // if the date input is null then set the date to null
    // TODO consider whether setting the underlying model to the invalid date is a better approach
    // we shouldn't need to check validity of the time component but we do anyway
    if (!this._dateInputValue || isNaN(this._dateInputValue.getTime())) {
      this._dateValue = null;
    } else {
      // we have no time so set the date to the start of the day
      this._dateValue = moment(this._dateInputValue).startOf('day').toDate();
    }
    console.log(
      'Date set to ' + (this._dateValue ? this._dateValue.toISOString() : '')
    );
    this.onValueChange(this._dateValue);
  }

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

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  writeValue(obj: any): void {
    this.dateValue = obj;
  }

  onValueChange(nextValue: any) {
    this.propagateChange(nextValue);
  }

  onTab() {
    // hide the date picker as it gets in the way
    this.datepicker.hide();
  }
}

import { Component, forwardRef, Input, OnInit, ViewChild } from '@angular/core';
import {
  ControlValueAccessor,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
} from '@angular/forms';
import * as moment from 'moment';
import { BsDatepickerDirective } from 'ngx-bootstrap/datepicker';

@Component({
  selector: 'app-date-time-control',
  templateUrl: './date-time-control.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DateTimeControlComponent),
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => DateTimeControlComponent),
      multi: true,
    },
  ],
})
export class DateTimeControlComponent
  implements OnInit, ControlValueAccessor, Validator {
  @ViewChild(BsDatepickerDirective) datepicker: BsDatepickerDirective;

  private _dateTimeValue: Date;
  private _dateInputValue: Date;

  timeInputValue: string;

  @Input()
  id: string;

  @Input()
  editorCfg: string;

  @Input()
  disabled = false;

  @Input()
  autofocus = false;

  @Input()
  mandatoryTime = false;

  timeMissing = false;

  get dateTimeValue(): Date {
    return this._dateTimeValue;
  }

  set dateTimeValue(value: Date) {
    this.timeMissing = false;
    if (!value) {
      if (this._dateTimeValue) {
        // only set the values to null if the date time was changed.
        // this preserves any invalid input in the time input value
        this._dateTimeValue = null;
        this._dateInputValue = null;
        this.timeInputValue = null;
      }
    } else {
      this._dateTimeValue = value;
      this._dateInputValue = moment(value).startOf('day').toDate();
      this.timeInputValue = moment(value).format('HH:mm');
    }
  }

  get dateInputValue() {
    return this._dateInputValue;
  }

  set dateInputValue(value) {
    if (value) {
      this._dateInputValue = value;
    } else {
      this._dateInputValue = null;
    }
    this.refreshDateValue();
  }

  // TODO consider whether setting the underlying model to the invalid date is a better approach
  get dateInputValid(): boolean {
    const result =
      this._dateInputValue && !isNaN(this._dateInputValue.getTime());
    return result;
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
    this.dateTimeValue = obj;
  }

  onValueChange(nextValue: any) {
    this.propagateChange(nextValue);
  }

  onTimeInputBlur() {
    this.refreshDateValue();
  }

  private refreshDateValue() {
    // https://stackoverflow.com/questions/8318236/regex-pattern-for-hhmmss-time-string
    // For some reason this regex includes '?;' which means create a non-capturing group.
    // I'm pretty sure if this is ommitted the regex would still work.
    const timeRegex = /^([0-1]?\d|2[0-3])(?::([0-5]?\d))?(?::([0-5]?\d))?$/;
    // this regex matches against times like '1700' or '100' but not '10'
    const timeRegexWithoutColons = /^([0-1]?\d|2[0-3])(?:([0-5]\d))$/;

    // if the date input is null then set the date time to null and the time input to null
    // bsDatepicker sets value to an "invalid date" if an invalid date is entered
    if (!this.dateInputValid) {
      this._dateTimeValue = null;
      // set time input to null as this won't be done by way of the date time being updated. See code in dateTimeValue(value: Date) above
      this.timeInputValue = null;

      // Empty is a valid option (unless there is additional validation), so the time is not missing.
      this.timeMissing = false;
    } else if (!this.mandatoryTime && !this.timeInputValue) {
      // we have no time so set the date time to the start of the day
      // this will update the time input value by way of the date time being updated
      this._dateTimeValue = moment(this._dateInputValue)
        .startOf('day')
        .toDate();
      this.timeMissing = false;
    } else if (
      timeRegex.test(this.timeInputValue) ||
      timeRegexWithoutColons.test(this.timeInputValue)
    ) {
      // we have a valid time so we can set the date time value
      let timeOnlyValue = null;
      if (timeRegexWithoutColons.test(this.timeInputValue)) {
        let value = this.timeInputValue;
        if (value.length === 3) {
          value = '0' + value;
        }
        timeOnlyValue = moment(
          '1900-01-01T' + value,
          'YYYY-MM-DDTHHmm'
        ).toDate();
      } else {
        timeOnlyValue = moment(
          '1900-01-01T' + this.timeInputValue,
          'YYYY-MM-DDTHH:mm'
        ).toDate();
      }

      //let dateStr = moment(dateValue).format('MMMM Do YYYY, h:mm a');
      //console.log('Time   Valid: ' + value + " - " + dateStr);
      this._dateTimeValue = moment(this._dateInputValue)
        .startOf('day')
        .add(moment(timeOnlyValue).diff(moment(timeOnlyValue).startOf('day')))
        .toDate();
      // set time input value to formatted to give better feedback to use that what they entered was accepted
      this.timeInputValue = moment(timeOnlyValue).format('HH:mm');
      this.timeMissing = false;
    } else {
      // we don't have a value time so set date time to null
      //this._dateTimeValue = null;

      // Set the time to the start of the day. This prevents "Value is required", but we still
      // have our default "time is required" handling.
      this._dateTimeValue = moment(this._dateInputValue)
        .startOf('day')
        .toDate();
      this.timeMissing = true;
    }

    console.log('Date Time set to 1: ' + this.dateTimeValue);
    console.log(
      'Date Time set to 2: ' +
        (this.dateTimeValue ? this.dateTimeValue.toISOString() : '')
    );

    this.onValueChange(this._dateTimeValue);
  }

  onTab(event: any) {
    // ensure that the time input field gets enabled before tabbing to it
    if (event.target.value) {
      this.dateInputValue = moment(event.target.value, 'DD/MM/YYYY').toDate();
    }
    // hide the date picker as it gets in the way
    this.datepicker.hide();
  }

  public validate(): ValidationErrors | null {
    if (this.timeMissing && this.mandatoryTime) {
      return {
        timeMissing: 'Time is required',
      };
    } else {
      return null;
    }
  }
}

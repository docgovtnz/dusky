import * as moment from 'moment';
import { BsDatepickerDirective } from 'ngx-bootstrap/datepicker';

import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';

@Component({
  selector: 'app-date-time-editor',
  templateUrl: './date-time-editor.component.html',
})
export class DateTimeEditorComponent implements OnInit {
  @ViewChild(BsDatepickerDirective) datepicker: BsDatepickerDirective;

  private _dateTimeValue: Date;
  private _dateInputValue: Date;

  timeInputValue: string;

  @Input()
  id: string;

  @Input()
  disabled = false;

  @Input()
  autofocus = false;

  @Output()
  dateTimeValueChange = new EventEmitter<Date>();

  constructor() {}

  ngOnInit() {}

  get dateTimeValue(): Date {
    return this._dateTimeValue;
  }

  @Input()
  set dateTimeValue(value: Date) {
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

  get dateInputValue(): Date {
    return this._dateInputValue;
  }

  set dateInputValue(value: Date) {
    if (value) {
      this._dateInputValue = value;
    } else {
      this._dateInputValue = null;
    }
    this.refreshDateValue();
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
    if (!this._dateInputValue) {
      this._dateTimeValue = null;
      // set time input to null as this won't be done by way of the date time being updated. See code in dateTimeValue(value: Date) above
      this.timeInputValue = null;
    } else if (!this.timeInputValue) {
      // we have no time so set the date time to the start of the day
      // this will update the time input value by way of the date time being updated
      this._dateTimeValue = moment(this._dateInputValue)
        .startOf('day')
        .toDate();
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
    } else {
      // we don't have a value time so set date time to null
      // TODO we need to mark the time field as being invalid
      this._dateTimeValue = null;
    }
    console.log(
      'Date Time set to ' +
        (this._dateTimeValue ? this._dateTimeValue.toISOString() : '')
    );
    this.dateTimeValueChange.emit(this._dateTimeValue);
  }

  onTab(event: any) {
    // ensure that the time input field gets enabled before tabbing to it
    if (event.target.value) {
      this.dateInputValue = moment(event.target.value, 'DD/MM/YYYY').toDate();
    }
    // hide the date picker as it gets in the way
    this.datepicker.hide();
  }
}

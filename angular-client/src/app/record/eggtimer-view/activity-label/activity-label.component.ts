import { Component, Input, OnInit } from '@angular/core';
import * as moment from 'moment';

@Component({
  selector: 'app-activity-label',
  templateUrl: './activity-label.component.html',
})
export class ActivityLabelComponent implements OnInit {
  private _activityRawValue: number;

  activityTimeValue: string;

  constructor() {}

  ngOnInit() {}

  get activityRawValue(): number {
    return this._activityRawValue;
  }

  @Input()
  set activityRawValue(value: number) {
    this._activityRawValue = value;

    if (value && value > 0) {
      const totalTimeInMin = value * 10;
      this.activityTimeValue = moment
        .utc()
        .startOf('day')
        .add(totalTimeInMin, 'minutes')
        .format('H:mm');
    }
  }
}

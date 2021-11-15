import { isNullOrUndefined } from 'util';
import { Component, Input, OnInit } from '@angular/core';
import * as moment from 'moment';

@Component({
  selector: 'app-age-label',
  templateUrl: './age-label.component.html',
})
export class AgeLabelComponent implements OnInit {
  ageLabel: string;
  _ageInDays: number;

  constructor() {}

  ngOnInit() {}

  @Input()
  get ageInDays(): number {
    return this._ageInDays;
  }

  set ageInDays(value: number) {
    this._ageInDays = value;
    if (!isNullOrUndefined(value)) {
      const duration = moment.duration(value, 'days');
      if (duration.asYears() >= 4.5) {
        // this is an adult
        // display years, e.g. 4 years
        // note 4 years, 9 months will be displayed as 4 years
        this.ageLabel = duration.years() + ' years';
      } else if (duration.asYears() >= 1) {
        // this is a juvenille that is a year or older
        // display years to 1 decimal place, e.g. 1.4 years
        this.ageLabel = duration.asYears().toFixed(1) + ' years';
      } else if (duration.asDays() >= 150) {
        // this is a juvenille that is less that a year old
        // display months e.g. 4 months
        // note 3 months 27 days will be displayed as 3 months
        this.ageLabel = duration.months() + ' months';
      } else if (duration.asDays() === 1) {
        // this is a chick or egg that is exactly 1 day old
        // display as 1 day
        this.ageLabel = '1 day';
      } else {
        // this is a chick or egg that is not exactly 1 day old
        // display days, e.g. 5 days or 0 days
        this.ageLabel = duration.asDays() + ' days';
      }
    } else {
      this.ageLabel = '-';
    }
  }
}

import { Component, Input } from '@angular/core';
import * as moment from 'moment';

@Component({
  selector: 'app-expired-date-tick',
  templateUrl: './expired-date-tick.component.html',
})
export class ExpiredDateTickComponent {
  @Input()
  date: Date;

  get expired(): boolean {
    return this.date && moment(this.date).isBefore(moment().startOf('day'));
  }
}

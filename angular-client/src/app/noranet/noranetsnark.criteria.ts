import * as moment from 'moment';

import { NoraNetBaseCriteria } from './noranet-base.criteria';
import { Params } from '@angular/router';

export class NoraNetSnarkCriteria extends NoraNetBaseCriteria {
  stationId: string;

  populateFromParams(params: Params) {
    this.doPopulateFromParams(
      params,
      [],
      ['pageSize', 'pageNumber'],
      ['fromActivityDate', 'toActivityDate'],
      []
    );
  }

  toParams() {
    return this.doToParams(this);
  }

  constructor() {
    super();
    this.fromActivityDate = moment()
      .startOf('day')
      .subtract(1, 'days')
      .toDate();
  }
}

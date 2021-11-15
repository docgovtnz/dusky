import { Params } from '@angular/router';
import * as moment from 'moment';

import { NoraNetBaseCriteria } from './noranet-base.criteria';

export class NoraNetCriteria extends NoraNetBaseCriteria {
  birdIDs: any[];
  uhfId: number;

  populateFromParams(params: Params) {
    this.doPopulateFromParams(
      params,
      [],
      ['pageSize', 'pageNumber', 'uhfId'],
      ['fromActivityDate', 'toActivityDate'],
      ['birdIDs']
    );
  }

  toParams() {
    return this.doToParams(this);
  }

  constructor() {
    super();
    this.fromActivityDate = moment()
      .startOf('day')
      .subtract(7, 'days')
      .toDate();
  }
}

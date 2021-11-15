import { Params } from '@angular/router';

import { AbstractCriteria } from '../../domain/criteria/abstract-criteria';

export class ReportChecksheetCriteria extends AbstractCriteria {
  island: string;
  sex: string;
  fromDate: Date;
  toDate: Date;
  numDays: number;

  populateFromParams(params: Params) {
    this.doPopulateFromParams(
      params,
      [],
      ['pageSize', 'pageNumber'],
      ['fromDate', 'toDate'],
      []
    );
  }

  toParams() {
    return this.doToParams(this);
  }
}

import { Params } from '@angular/router';

import { AbstractCriteria } from '../../domain/criteria/abstract-criteria';

export class ReportMatingCriteria extends AbstractCriteria {
  island: string;
  fromDate: Date;
  toDate: Date;
  birdID: string;
  minimumQuality: number;

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

import { Params } from '@angular/router';

import { AbstractCriteria } from '../../domain/criteria/abstract-criteria';

export class ReportNoraNetErrorCriteria extends AbstractCriteria {
  fromDateProcessed: Date;
  toDateProcessed: Date;

  populateFromParams(params: Params) {
    this.doPopulateFromParams(
      params,
      [],
      ['pageSize', 'pageNumber'],
      ['fromDateProcessed', 'toDateProcessed'],
      []
    );
  }

  toParams() {
    return this.doToParams(this);
  }
}

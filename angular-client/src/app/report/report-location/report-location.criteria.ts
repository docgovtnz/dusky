import { Params } from '@angular/router';

import { AbstractCriteria } from '../../domain/criteria/abstract-criteria';

export class ReportLocationCriteria extends AbstractCriteria {
  island: string;
  queryDate: Date;
  showUnknown: boolean;
  showEgg: boolean;
  showChick: boolean;
  showJuvenile: boolean;
  showAdult: boolean;

  populateFromParams(params: Params) {
    this.doPopulateFromParams(
      params,
      [],
      ['pageSize', 'pageNumber'],
      ['queryDate', 'toDate'],
      []
    );
  }

  toParams() {
    return this.doToParams(this);
  }
}

import { Params } from '@angular/router';

import { AbstractCriteria } from '../domain/criteria/abstract-criteria';

export class NoraNetBaseCriteria extends AbstractCriteria {
  island: string;
  fromActivityDate: Date;
  toActivityDate: Date;

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
  }
}

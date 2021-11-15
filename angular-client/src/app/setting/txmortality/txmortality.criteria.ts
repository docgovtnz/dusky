import { Params } from '@angular/router';

import { AbstractCriteria } from '../../domain/criteria/abstract-criteria';

export class TxMortalityCriteria extends AbstractCriteria {
  name: string;

  populateFromParams(params: Params) {
    this.doPopulateFromParams(params, [], ['pageSize', 'pageNumber'], [], []);
  }

  toParams() {
    return this.doToParams(this);
  }
}

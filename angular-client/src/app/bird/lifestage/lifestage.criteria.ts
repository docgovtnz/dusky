import { AbstractCriteria } from '../../domain/criteria/abstract-criteria';
import { Params } from '@angular/router';

export class LifeStageCriteria extends AbstractCriteria {
  populateFromParams(params: Params) {
    this.doPopulateFromParams(params, [], ['pageSize', 'pageNumber'], [], []);
  }

  toParams() {
    return this.doToParams(this);
  }
}

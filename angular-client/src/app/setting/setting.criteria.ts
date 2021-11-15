import { AbstractCriteria } from '../domain/criteria/abstract-criteria';
import { Params } from '@angular/router';

export class SettingCriteria extends AbstractCriteria {
  name: string;

  populateFromParams(params: Params) {
    this.doPopulateFromParams(params, [], ['pageSize', 'pageNumber'], [], []);
  }

  toParams() {
    return this.doToParams(this);
  }
}

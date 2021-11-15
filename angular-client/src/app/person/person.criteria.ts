import { AbstractCriteria } from '../domain/criteria/abstract-criteria';
import { Params } from '@angular/router';

export class PersonCriteria extends AbstractCriteria {
  name: string;
  activeOnly: boolean;
  accountOnly: boolean;
  currentCapacity: string;
  personRole: string;

  populateFromParams(params: Params) {
    this.doPopulateFromParams(
      params,
      ['activeOnly', 'accountOnly'],
      ['pageSize', 'pageNumber'],
      [],
      []
    );
  }

  toParams() {
    return this.doToParams(this);
  }
}

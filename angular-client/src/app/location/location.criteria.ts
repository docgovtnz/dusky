import { AbstractCriteria } from '../domain/criteria/abstract-criteria';
import { Params } from '@angular/router';

export class LocationCriteria extends AbstractCriteria {
  locationIDs: string[];
  island: string;
  birdID: string;
  activeOnly: boolean;
  locationType: string;

  populateFromParams(params: Params) {
    this.doPopulateFromParams(
      params,
      ['activeOnly'],
      ['pageSize', 'pageNumber'],
      [],
      ['locationIDs']
    );
  }

  toParams() {
    return this.doToParams(this);
  }
}

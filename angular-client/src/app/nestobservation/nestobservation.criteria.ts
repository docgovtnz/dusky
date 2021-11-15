import { AbstractCriteria } from '../domain/criteria/abstract-criteria';
import { Params } from '@angular/router';

export class NestObservationCriteria extends AbstractCriteria {
  birdID: string;
  childBirdID: string;
  island: string;
  locationID: string;
  observerPersonID: string;
  fromDate: Date;
  toDate: Date;
  locationType: string;

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

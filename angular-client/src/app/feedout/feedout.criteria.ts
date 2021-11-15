import { AbstractCriteria } from '../domain/criteria/abstract-criteria';
import { Params } from '@angular/router';

export class FeedOutCriteria extends AbstractCriteria {
  island: string;
  locationID: string;
  birdID: string;
  sex: string;
  fromDate: Date;
  toDate: Date;
  food: string;

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

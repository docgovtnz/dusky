import { AbstractCriteria } from '../domain/criteria/abstract-criteria';
import { Params } from '@angular/router';

export class SnarkActivityCriteria extends AbstractCriteria {
  island: string;
  locationID: string;
  birdID: string;
  fromDate: Date;
  toDate: Date;
  includeTrackAndBowl: boolean;
  includeHopper: boolean;
  includeNest: boolean;
  includeRoost: boolean;

  populateFromParams(params: Params) {
    this.doPopulateFromParams(
      params,
      ['includeTrackAndBowl', 'includeHopper', 'includeNest', 'includeRoost'],
      ['pageSize', 'pageNumber'],
      ['fromDate', 'toDate'],
      []
    );
  }

  toParams() {
    return this.doToParams(this);
  }
}

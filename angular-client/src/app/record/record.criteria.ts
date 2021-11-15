import { AbstractCriteria } from '../domain/criteria/abstract-criteria';
import { Params } from '@angular/router';

export class RecordCriteria extends AbstractCriteria {
  birdIDs: string[];
  significantEventOnly: boolean;
  recordTypes: string[];
  reasons: string[];
  activity: string;
  withWeightOnly: boolean;
  island: string;
  locationID: string;
  observerPersonID: string;
  fromDate: Date;
  toDate: Date;

  populateFromParams(params: Params) {
    this.doPopulateFromParams(
      params,
      ['significantEventOnly', 'withWeightOnly'],
      ['pageSize', 'pageNumber'],
      ['fromDate', 'toDate'],
      ['birdIDs']
    );
  }

  toParams() {
    return this.doToParams(this);
  }
}

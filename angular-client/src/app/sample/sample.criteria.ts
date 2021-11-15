import { AbstractCriteria } from '../domain/criteria/abstract-criteria';
import { Params } from '@angular/router';

export class SampleCriteria extends AbstractCriteria {
  birdIDs: string[];
  ageClass: string;
  sex: string;
  collectionIsland: string;
  collectionLocationID: string;
  sampleTakenBy: string;
  showArchived: boolean;
  showNotArchived: boolean;
  sampleName: string;
  sampleCategory: string;
  container: string;
  sampleType: string;
  showResultsEntered: boolean;
  showResultsNotEntered: boolean;
  fromDate: Date;
  toDate: Date;

  populateFromParams(params: Params) {
    this.doPopulateFromParams(
      params,
      [
        'showArchived',
        'showNotArchived',
        'showResultsEntered',
        'showResultsNotEntered',
      ],
      ['pageSize', 'pageNumber'],
      ['fromDate', 'toDate'],
      ['birdIDs']
    );
  }

  toParams() {
    return this.doToParams(this);
  }
}

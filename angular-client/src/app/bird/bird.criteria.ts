import { AbstractCriteria } from '../domain/criteria/abstract-criteria';
import { Params } from '@angular/router';

export class BirdCriteria extends AbstractCriteria {
  birdNames: string[];
  sex: string;
  showAlive: boolean;
  showDead: boolean;
  ageClass: string;
  currentIsland: string;
  currentLocationID: string;
  transmitterGroup: string;
  excludeEgg: boolean;

  populateFromParams(params: Params) {
    this.doPopulateFromParams(
      params,
      ['showAlive', 'showDead'],
      ['pageSize', 'pageNumber'],
      [],
      ['birdNames']
    );
  }

  toParams() {
    return this.doToParams(this);
  }
}

import { AbstractCriteria } from '../domain/criteria/abstract-criteria';
import { Params } from '@angular/router';

export class TransmitterCriteria extends AbstractCriteria {
  txId: string;
  transmitterGroup: string;
  currentIsland: string;
  txMortalityTypes: string;
  rigging: string;
  channel: string;
  spareOnly: boolean;
  showDecommissioned: boolean;
  birdID: string;
  ageClass: string;
  aliveOnly: boolean;
  sex: string;
  uhfId: number;

  populateFromParams(params: Params) {
    this.doPopulateFromParams(
      params,
      ['spareOnly', 'showDecommissioned', 'aliveOnly'],
      ['pageSize', 'pageNumber', 'channel'],
      [],
      []
    );
  }

  toParams() {
    return this.doToParams(this);
  }
}

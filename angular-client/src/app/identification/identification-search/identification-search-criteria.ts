import { Params } from '@angular/router';

import { AbstractCriteria } from '../../domain/criteria/abstract-criteria';

export class IdSearchCriteria extends AbstractCriteria {
  bird: string;
  fromDate: Date;
  toDate: Date;
  sex: string;
  island: string;
  txId: string;
  transmitterGroup: string;
  mortType: string;
  microchip: string;
  band: string;
  ageClass: string;
  aliveOnly: boolean;
  latestOnly: boolean;
  channel: string;
  uhfId: number;

  populateFromParams(params: Params) {
    this.doPopulateFromParams(
      params,
      ['aliveOnly', 'latestOnly'],
      ['pageSize', 'pageNumber'],
      ['fromDate', 'toDate'],
      []
    );
  }

  toParams() {
    return this.doToParams(this);
  }
}

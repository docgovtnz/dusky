import { BaseEntity } from './base-entity';

export class TxMortalityEntity extends BaseEntity {
  activityBpm: number;
  hoursTilMort: number;
  mortalityBpm: number;
  name: string;
  normalBpm: number;
  oldTxMortId: number;

  constructor() {
    super();
    this.docType = 'TxMortality';
  }
}

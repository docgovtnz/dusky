import { BaseEntity } from './base-entity';

export class TransmitterEntity extends BaseEntity {
  channel: number;
  frequency: number;
  island: string;
  lastRecordId: string;
  lifeExpectancy: number;
  oldTxMortId: number;
  origLifeExpectancyWeeks: number;
  rigging: string;
  software: string;
  status: string;
  txFineTune: string;
  txId: string;
  txMortalityId: string;
  uhfId: number;

  constructor() {
    super();
    this.docType = 'Transmitter';
  }
}

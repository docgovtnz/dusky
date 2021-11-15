import { ObserverEntity } from './observer.entity';
import { BaseEntity } from './base-entity';

export class FledgedChickEntity extends BaseEntity {
  birdID: string;
  date: Date;
  observerList: ObserverEntity[];

  constructor() {
    super();
    this.docType = 'FledgedChick';
  }
}

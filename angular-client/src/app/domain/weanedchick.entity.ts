import { ObserverEntity } from './observer.entity';
import { BaseEntity } from './base-entity';

export class WeanedChickEntity extends BaseEntity {
  birdID: string;
  date: Date;
  observerList: ObserverEntity[];

  constructor() {
    super();
    this.docType = 'WeanedChick';
  }
}

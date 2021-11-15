import { ObserverEntity } from './observer.entity';
import { BaseEntity } from './base-entity';

export class NewEggEntity extends BaseEntity {
  birdID: string;
  clutchOrder: number;
  eggName: string;
  fwCoefficientX10P4: number;
  layDate: Date;
  layDateIsEstimate: boolean;
  eggLength: number;
  locationID: string;
  mother: string;
  eggWidth: number;
  observerList: ObserverEntity[];

  constructor() {
    super();
    this.docType = 'NewEgg';
  }
}

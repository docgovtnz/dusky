import { BaseEntity } from './base-entity';

export class HatchEntity extends BaseEntity {
  birdID: string;
  hatchDate: Date;

  constructor() {
    super();
    this.docType = 'Hatch';
  }
}

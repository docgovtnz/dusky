import { BaseEntity } from './base-entity';

export class LifeStageEntity extends BaseEntity {
  ageClass: string;
  birdID: string;
  changeType: string;
  dateTime: Date;
  recordID: string;

  constructor() {
    super();
    this.docType = 'LifeStage';
  }
}

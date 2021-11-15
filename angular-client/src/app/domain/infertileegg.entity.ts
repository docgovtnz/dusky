import { EggMeasurementsEntity } from './eggmeasurements.entity';
import { BaseEntity } from './base-entity';

export class InfertileEggEntity extends BaseEntity {
  birdID: string;
  eggMeasurements: EggMeasurementsEntity;

  constructor() {
    super();
    this.docType = 'InfertileEgg';
  }
}

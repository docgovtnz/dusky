import { EggMeasurementsEntity } from './eggmeasurements.entity';
import { EmbryoMeasurementsEntity } from './embryomeasurements.entity';
import { ObserverEntity } from './observer.entity';
import { BaseEntity } from './base-entity';

export class DeadEmbryoEntity extends BaseEntity {
  birdID: string;
  date: Date;
  eggMeasurements: EggMeasurementsEntity;
  embryoMeasurements: EmbryoMeasurementsEntity;
  observerList: ObserverEntity[];

  constructor() {
    super();
    this.docType = 'DeadEmbryo';
  }
}

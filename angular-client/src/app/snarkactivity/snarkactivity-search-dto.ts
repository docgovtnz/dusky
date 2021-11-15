/* eslint-disable max-classes-per-file */

import { BaseEntity } from '../domain/base-entity';

export class SnarkActivityEntity extends BaseEntity {
  snarkActivityId: string;
  activityType: string;
  date: Date;
  locationID: string;
  locationName: string;
  boom: boolean;
  ching: boolean;
  grubbing: number;
  matingSign: number;
  skraak: boolean;
  sticks: number;
  trackActivity: number;
  birds: Bird[];
}

class Bird {
  id: string;
  name: string;
}

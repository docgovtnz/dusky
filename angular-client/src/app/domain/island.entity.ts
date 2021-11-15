import { BaseEntity } from './base-entity';

export class IslandEntity extends BaseEntity {
  lowerEasting: number;
  lowerNorthing: number;
  magneticDeclination: number;
  magneticDeclinationAsOfYear: number;
  name: string;
  upperEasting: number;
  upperNorthing: number;
  islandId: number;

  constructor() {
    super();
    this.docType = 'Island';
  }
}

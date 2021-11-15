import { BaseEntity } from './base-entity';

export class FertileEggEntity extends BaseEntity {
  birdID: string;
  definiteFather: boolean;
  father: string;

  constructor() {
    super();
    this.docType = 'FertileEgg';
  }
}

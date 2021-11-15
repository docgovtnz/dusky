import { FoodTallyEntity } from './foodtally.entity';
import { TargetBirdEntity } from './targetbird.entity';
import { BaseEntity } from './base-entity';

export class FeedOutEntity extends BaseEntity {
  comments: string;
  dateIn: Date;
  dateOut: Date;
  feedId: string;
  locationID: string;
  newRecord: string;
  oldLocation: string;
  otherFood: string;
  otherIn: number;
  otherOut: number;
  foodTallyList: FoodTallyEntity[];
  targetBirdList: TargetBirdEntity[];

  constructor() {
    super();
    this.docType = 'FeedOut';
  }
}

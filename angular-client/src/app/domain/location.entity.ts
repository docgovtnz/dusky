import { NestDetailsEntity } from './nestdetails.entity';
import { BaseEntity } from './base-entity';

export class LocationEntity extends BaseEntity {
  active: boolean;
  birdID: string;
  captivityType: string;
  comments: string;
  easting: number;
  firstDate: Date;
  gpscount: number;
  island: string;
  locationName: string;
  locationType: string;
  mappingMethod: string;
  northing: number;
  nestDetails: NestDetailsEntity;

  constructor() {
    super();
    this.docType = 'Location';
  }
}

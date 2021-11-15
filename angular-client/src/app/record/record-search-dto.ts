export class RecordSearchDTO {
  recordID: string;
  dateTime: Date;
  birdID: string;
  birdName: string;
  recorder: string;
  island: string;
  locationID: string;
  locationName: string;
  locationEasting: number;
  locationNorthing: number;
  recordType: string;
  activity: string;
  easting: number;
  northing: number;
  reason: string;
  subReason: string;
  hasComment: boolean;
  hasSample: boolean;

  // this is something extra for Angular to track how to display this record data on the map
  mapFeatureType: string;

  constructor() {}
}

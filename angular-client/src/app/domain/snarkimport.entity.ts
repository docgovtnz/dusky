import { BaseEntity } from './base-entity';

export class SnarkImportEntity extends BaseEntity {
  activityType: string;
  island: string;
  locationID: string;
  observerPersonID: string;
  qualityOverride: number;
  snarkFileContent: string;
  snarkFileHash: string;
  snarkFileName: string;
  showLockRecords: boolean;

  constructor() {
    super();
    this.docType = 'SnarkImport';
  }
}

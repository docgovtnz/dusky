import { BloodSampleEntity } from './bloodsample.entity';

export class BloodSampleDetailEntity {
  totalBloodVolumeInMl: number;
  veinSite: string[];
  bloodSampleList?: BloodSampleEntity[];
}

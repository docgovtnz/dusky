import { BaseEntity } from '../../domain/base-entity';

import { NoraNetCmLongEntity } from './noranetcmlong.entity';
import { NoraNetCmShortEntity } from './noranetcmshort.entity';
import { NoraNetDetectionEntity } from './noranetdetection.entity';
import { NoraNetEggTimerEntity } from './noraneteggtimer.entity';
import { NoraNetStandardEntity } from './noranetstandard.entity';

export class NoraNetEntity extends BaseEntity {
  activityDate: Date;
  batteryVolts: number;
  cmLongList: NoraNetCmLongEntity[];
  cmShortList: NoraNetCmShortEntity[];
  dataVersion: number;
  detectionList: NoraNetDetectionEntity[];
  eggTimerList: NoraNetEggTimerEntity[];
  fileDate: Date;
  island: string;
  locationCode: number;
  recordCounts: string;
  standardList: NoraNetStandardEntity[];
  stationId: string;

  constructor() {
    super();
    this.docType = 'NoraNet';
  }
}

import { CheckmateDataEntity } from './checkmatedata.entity';

export class CheckmateEntity {
  battery1: number;
  battery2: number;
  last24hourActivity: number;
  last24hourActivity1: number;
  last24hourActivity2: number;
  dataCaptureType: string;
  pulseRate: string;
  recoveryid: string;
  checkmateDataList: CheckmateDataEntity[];
}

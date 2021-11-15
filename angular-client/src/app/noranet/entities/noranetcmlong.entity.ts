import { NoraNetBaseEntity } from './noranetbase.entity';
import { NoraNetCmFemaleEntity } from './noranetcmfemale.entity';

export class NoraNetCmLongEntity extends NoraNetBaseEntity {
  matingAge: number;
  cmHour: number;
  cmMinute: number;
  lastCmHour: number;
  lastCmMinute: number;
  cmFemaleList: NoraNetCmFemaleEntity[];
}

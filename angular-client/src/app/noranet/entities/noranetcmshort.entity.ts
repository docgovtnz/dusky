import { NoraNetBaseEntity } from './noranetbase.entity';

export class NoraNetCmShortEntity extends NoraNetBaseEntity {
  activity: number;
  batteryLife: number;
  matingAge: number;
  cmHour: number;
  cmMinute: number;
}

import { NoraNetBaseEntity } from './noranetbase.entity';

export class NoraNetEggTimerEntity extends NoraNetBaseEntity {
  activity: number;
  batteryLife: number;
  incubating: boolean;
  daysSinceChange: number;
}

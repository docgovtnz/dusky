import { NoraNetBaseEntity } from './noranetbase.entity';

export class NoraNetDetectionEntity extends NoraNetBaseEntity {
  category: string;
  pulseCount: number;
  peakTwitch: number;
  activity: number;
}

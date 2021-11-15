import { SnarkRecordEntity } from './snarkrecord.entity';
import { TrackAndBowlActivityEntity } from './trackandbowlactivity.entity';
import { BaseEntity } from './base-entity';

export class SnarkActivityEntity extends BaseEntity {
  activityType: string;
  comments: string;
  date: Date;
  locationID: string;
  observerPersonID: string;
  oldBooming: boolean;
  oldChinging: boolean;
  oldFightingSign: number;
  oldGrubbing: number;
  oldMatingSign: number;
  oldObserver: string;
  oldSkraaking: boolean;
  oldSticks: number;
  oldTAndB: string;
  oldTAndBRecId: string;
  oldTapeUsed: boolean;
  oldTbHopper: number;
  oldTimeRecorded: number;
  oldTrackActivity: number;
  tandBrecid: string;
  snarkRecordList: SnarkRecordEntity[];
  trackAndBowlActivity: TrackAndBowlActivityEntity;

  constructor() {
    super();
    this.docType = 'SnarkActivity';
  }
}

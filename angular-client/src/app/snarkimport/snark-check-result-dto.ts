import { EveningDTO } from './evening-dto';
import { MysteryWeightDTO } from './mystery-weight-dto';

export class SnarkCheckResultDTO {
  mysteryWeightList: MysteryWeightDTO[];
  eveningList: EveningDTO[];
  existingImport: boolean;
  someBirdsNotFound: boolean;
  island: string;
  showLockRecords: boolean;

  constructor() {}
}

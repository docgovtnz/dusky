import { SnarkImportEntity } from '../domain/snarkimport.entity';
import { EveningDTO } from './evening-dto';

export class SnarkImportRequest {
  entity: SnarkImportEntity;
  eveningList: EveningDTO[];
  constructor() {}
}

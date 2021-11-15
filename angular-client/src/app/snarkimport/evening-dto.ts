import { SnarkRecordEntity } from '../domain/snarkrecord.entity';

export class EveningDTO {
  date: Date;
  snarkRecordList: SnarkRecordEntity[];

  constructor() {}
}

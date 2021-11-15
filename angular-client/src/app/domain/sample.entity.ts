import { BloodSampleDetailEntity } from './bloodsampledetail.entity';
import { ChemistryAssayEntity } from './chemistryassay.entity';
import { HaematologyTestEntity } from './haematologytest.entity';
import { MicrobiologyAndParasitologyTestEntity } from './microbiologyandparasitologytest.entity';
import { OtherDetailEntity } from './otherdetail.entity';
import { SpermDetailEntity } from './spermdetail.entity';
import { SpermMeasureEntity } from './spermmeasure.entity';
import { SwabDetailEntity } from './swabdetail.entity';
import { BaseEntity } from './base-entity';

export class SampleEntity extends BaseEntity {
  archived: boolean;
  birdID: string;
  bloodChemistryComments: string;
  collectionDate: Date;
  collectionIsland: string;
  collectionLocationID: string;
  comments: string;
  container: string;
  currentIsland: string;
  haematologyComments: string;
  haemolysed: boolean;
  microbiologyAndParasitologyComments: string;
  sampleCategory: string;
  sampleName: string;
  sampleTakenBy: string;
  sampleType: string;
  smudgeCells: boolean;
  spermComments: string;
  storageConditions: string;
  storageMedium: string;
  reasonForSample: string;
  bloodDetail: BloodSampleDetailEntity;
  chemistryAssayList: ChemistryAssayEntity[];
  haematologyTestList: HaematologyTestEntity[];
  microbiologyAndParasitologyTestList: MicrobiologyAndParasitologyTestEntity[];
  otherDetail: OtherDetailEntity;
  spermDetail: SpermDetailEntity;
  spermMeasureList: SpermMeasureEntity[];
  swabDetail: SwabDetailEntity;

  constructor() {
    super();
    this.docType = 'Sample';
  }
}

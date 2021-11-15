export class SampleSearchDTO {
  sampleID: string;
  sampleName: string;
  birdID: string;
  birdName: string;
  sampleCategory: string;
  sampleType: string;
  sampleTakenByID: string;
  sampleTakenByName: string;
  collectionDate: Date;
  collectionIsland: string;
  collectionLocationID: string;
  collectionLocationName: string;
  resultsEntered: boolean;
  hasHaematologyResults: boolean;
  hasChemistryAssays: boolean;
  hasMicrobiologyAndParasitologyTests: boolean;
  hasSpermResults: boolean;
}

export class SwabSampleEntity {
  sampleID: string;
  sampleName: string;
  swabSite: string;
  storageMedium: string;
  container: string;
  storageConditions: string;
  quantity: number;
  sampleTakenBy: string;
  headerUniqueTestSwabSiteCount: number;
  microbiologyHeaderID: string;
  oldCaseNumber: string;
  oldComments: string;
  oldDateProcessed: Date;
  oldHeaderSwabSite: string;
  oldTestSwabSite: string;
  reasonForSample: string;
}

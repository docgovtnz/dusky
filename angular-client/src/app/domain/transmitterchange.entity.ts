export class TransmitterChangeEntity {
  addedTxFromLastRecordID: string;
  addedTxFromStatus: string;
  addedTxFromTxFineTune: string;
  harnessChangeOnly: boolean;
  hoursOff: number;
  hoursOn: number;
  newStatus: string;
  newTxFineTune: string;
  prox: boolean;
  proxOn: boolean;
  recoveryID: string;
  removed: boolean;
  removedTxFromLastRecordID: string;
  removedTxFromStatus: string;
  starttime: Date;
  txchangeid: string;
  txDocId: string;
  txFrom: string;
  txFromStatus: string;
  txId: string;
  txLifeExpectancyWeeks: number;
  txMortality: number;
  txTo: string;
  vhfHoursOn: number;
}

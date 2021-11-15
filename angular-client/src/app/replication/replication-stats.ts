export class ReplicationStats {
  periodEnding: Date;
  replicationId: string;
  source: string;
  target: string;
  docsRead: number;
  docsWritten: number;
  docWriteFailures: number;

  constructor() {}
}

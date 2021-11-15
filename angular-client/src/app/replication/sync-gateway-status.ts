import { FileActivity } from './file-activity';

export class SyncGatewayStatus {
  status: string;
  serverMode: string;
  connectionMap: any;
  fileActivity: FileActivity;

  constructor() {}
}

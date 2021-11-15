import { ReplicationStats } from './replication-stats';

export class ReplicationConnection {
  local: string;
  remote: string;

  localTaskId: string;
  remoteTaskId: string;

  localActivityList: ReplicationStats[];
  remoteActivityList: ReplicationStats[];

  constructor() {}
}

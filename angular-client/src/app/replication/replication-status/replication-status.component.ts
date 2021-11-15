import { Component, OnInit } from '@angular/core';
import { ReplicationService } from '../replication.service';
import { interval, Subscription } from 'rxjs';
import { startWith, switchMap } from 'rxjs/operators';
import { SyncGatewayStatus } from '../sync-gateway-status';

@Component({
  selector: 'app-replication-status',
  templateUrl: './replication-status.component.html',
})
export class ReplicationStatusComponent implements OnInit {
  private subscription: Subscription = new Subscription();

  syncGatewayStatus: SyncGatewayStatus;

  constructor(private replicationService: ReplicationService) {}

  ngOnInit() {
    this.restartPolling();
  }

  restartPolling() {
    this.subscription.unsubscribe();
    this.subscription = new Subscription();
    this.subscription.add(
      interval(15000)
        .pipe(
          startWith(0),
          switchMap(() => this.replicationService.getSyncGatewayStatus())
        )
        .subscribe((syncGatewayStatus) => {
          this.syncGatewayStatus = syncGatewayStatus;
        })
    );
  }

  getIconName() {
    let iconName = 'handshake-slash';
    if (this.syncGatewayStatus) {
      if (
        this.syncGatewayStatus.serverMode &&
        this.syncGatewayStatus.serverMode === 'Server'
      ) {
        iconName = 'server';
      } else if (
        this.syncGatewayStatus.status &&
        this.syncGatewayStatus.status === 'Ok'
      ) {
        const connectionMap = this.syncGatewayStatus.connectionMap;
        if (connectionMap && Object.keys(connectionMap).length > 0) {
          iconName = 'cloud';
        }
      }
    }
    return iconName;
  }
}

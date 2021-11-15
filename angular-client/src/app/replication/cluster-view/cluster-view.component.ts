import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ReplicationService } from '../replication.service';
import { LocalAddress } from '../local-address';
import { ConnectionRequest } from '../connection-request';
import { ReplicationViewComponent } from '../replication-view/replication-view.component';
import { ValidationMessage } from '../../domain/response/validation-message';
import { interval, Subscription } from 'rxjs';
import { finalize, startWith, switchMap } from 'rxjs/operators';
import { SyncGatewayStatus } from '../sync-gateway-status';
import { ReplicationChartComponent } from '../replication-chart/replication-chart.component';

@Component({
  selector: 'app-cluster-view',
  templateUrl: './cluster-view.component.html',
})
export class ClusterViewComponent implements OnInit, OnDestroy {
  @ViewChild('replicationView', { static: true })
  replicationView: ReplicationViewComponent = null;

  @ViewChild('replicationChart', { static: true })
  replicationChart: ReplicationChartComponent = null;

  localAddress: LocalAddress;
  connectionRequest = new ConnectionRequest();

  syncGatewayStatus: SyncGatewayStatus = null;

  operationalMode = null;
  disabledNames = ['Server'];

  messages: ValidationMessage[] = null;
  private subscription: Subscription = new Subscription();

  allowedTargetAddresses: string[];

  // stores whether or not we are waiting for a response from the server
  // this will be set to false when loaded by a successful getLocalAddress
  working = true;

  constructor(private replicationService: ReplicationService) {}

  ngOnInit() {
    this.replicationService.getLocalAddress().subscribe(
      (localAddress) => {
        this.working = false;
        this.localAddress = localAddress;
        this.updateMode();
        this.restartPolling();
      },
      () => this
    );
    this.replicationService
      .getAllowedTargetAddresses()
      .subscribe((atas) => (this.allowedTargetAddresses = atas));
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  updateMode() {
    if (this.localAddress) {
      this.operationalMode = this.localAddress.serverMode;
    } else {
      this.operationalMode = null;
    }

    if (this.operationalMode === 'Server') {
      this.disabledNames = ['Peer', 'Master', 'Client'];
    } else {
      // if operationalMode happens to be null we only disable Server also
      this.disabledNames = ['Server'];
    }
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
          this.replicationChart.updateChart();
        })
    );
  }

  onConnectToNode() {
    this.messages = null;
    this.working = true;
    this.replicationService
      .connectToNode(this.connectionRequest)
      .pipe(
        // use finalise to set working so it is unset on complete and on error
        finalize(() => (this.working = false))
      )
      .subscribe((response) => {
        this.messages = response.messages;
        this.restartPolling();
      });
  }

  onSelectedNameChange(mode: string) {
    this.messages = null;

    if (mode) {
      if (mode === 'Client') {
        // connect to AWS environment
        this.connectionRequest.connectToServer = null;
        this.working = true;
        this.replicationService
          .setMode('Client')
          .pipe(
            // use finalise to set working so it is unset on complete and on error
            finalize(() => (this.working = false))
          )
          .subscribe((response) => {
            this.messages = response.messages;
            this.restartPolling();
            this.operationalMode = mode;
          });
      } else if (mode === 'Peer') {
        // User enters address to connect to a local Master
        this.working = true;
        this.replicationService
          .setMode('Peer')
          .pipe(
            // use finalise to set working so it is unset on complete and on error
            finalize(() => (this.working = false))
          )
          .subscribe(() => {
            this.operationalMode = mode;
          });
      } else if (mode === 'Master') {
        // local Master will accept connections from Peers and connect to the AWS 'Server'
        this.connectionRequest.connectToServer = null;
        this.working = true;
        this.replicationService
          .setMode('Master')
          .pipe(
            // use finalise to set working so it is unset on complete and on error
            finalize(() => (this.working = false))
          )
          .subscribe((response) => {
            this.messages = response.messages;
            this.restartPolling();
            this.operationalMode = mode;
          });
      } else if (mode === 'Server') {
        this.working = false;
        this.operationalMode = mode;
        // Server mode should be locked by application properties, so this scenario is invalid, could throw error or ignore
      } else {
        this.working = false;
        throw new Error('Unknown operational mode: ' + mode);
      }
    }
  }

  isConnectionDisabled() {
    // TODO: ultimately setting to Client mode will automatically connect with AWS, but for now we'll allow manual connections
    const disabledDueToMode = !(this.operationalMode === 'Peer');
    let disabledDueToConnection = false;
    if (this.syncGatewayStatus && this.syncGatewayStatus.connectionMap) {
      disabledDueToConnection =
        Object.keys(this.syncGatewayStatus.connectionMap).length > 0;
    }
    return disabledDueToMode || disabledDueToConnection;
  }
}

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClusterViewComponent } from './cluster-view/cluster-view.component';
import { ReplicationViewComponent } from './replication-view/replication-view.component';
import { ReplicationChartComponent } from './replication-chart/replication-chart.component';
import { ReplicationStatusComponent } from './replication-status/replication-status.component';

import { SharedModule } from '../shared.module';

@NgModule({
  imports: [CommonModule, SharedModule],
  declarations: [
    ClusterViewComponent,
    ReplicationViewComponent,
    ReplicationChartComponent,
    ReplicationStatusComponent,
  ],
  exports: [CommonModule, ReplicationStatusComponent],
})
export class ReplicationModule {}

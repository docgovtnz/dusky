import { Component, OnInit, ViewChild } from '@angular/core';
import { ReplicationService } from '../../replication/replication.service';
import { IndexRebuildComponent } from '../index-rebuild/index-rebuild.component';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
})
export class HomeComponent implements OnInit {
  @ViewChild(IndexRebuildComponent, { static: true })
  private indexResetComponent;

  serverMode: string;

  constructor(private replicationService: ReplicationService) {}

  ngOnInit() {
    this.replicationService.getSyncGatewayStatus().subscribe((status) => {
      if (status) {
        this.serverMode = status.serverMode;
      }
    });
  }

  resetIndexes() {
    this.indexResetComponent.show();
  }
}

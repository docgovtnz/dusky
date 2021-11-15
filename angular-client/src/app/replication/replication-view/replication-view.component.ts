import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ReplicationService } from '../replication.service';

@Component({
  selector: 'app-replication-view',
  templateUrl: './replication-view.component.html',
})
export class ReplicationViewComponent implements OnInit {
  @Input()
  connectionMap: any;

  @Output()
  disconnectionEvent = new EventEmitter();

  constructor(private replicationService: ReplicationService) {}

  ngOnInit() {}

  connectionMapKeys(): string[] {
    return Object.keys(this.connectionMap);
  }

  onDisconnect(connectionKey: string) {
    this.replicationService.disconnect(connectionKey).subscribe(() => {
      console.log('Connection disconnected: ' + connectionKey);
      this.disconnectionEvent.next();
    });
  }
}

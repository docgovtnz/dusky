import { Component, Input, OnInit } from '@angular/core';

import { RecordEntity } from '../../domain/record.entity';
import { NestObservationEntity } from '../../domain/nestobservation.entity';
import { Subscription } from 'rxjs';
import { NestObservationService } from '../../nestobservation/nestobservation.service';

@Component({
  selector: 'app-record-panel',
  templateUrl: 'record-panel.component.html',
})
export class RecordPanelComponent implements OnInit {
  private _recordRevision: RecordEntity;

  nestObservation: NestObservationEntity;

  private _recordID;
  private subscription: Subscription;

  constructor(private nestObservationService: NestObservationService) {}

  ngOnInit(): void {}

  @Input()
  set recordRevision(value) {
    let recordID: string;
    if (value) {
      recordID = value.id;
      if (recordID.includes(':')) {
        recordID = recordID.substr(0, recordID.indexOf(':'));
      }
    }
    if (!recordID || this._recordID !== recordID) {
      this.nestObservation = null;
      if (this.subscription) {
        this.subscription.unsubscribe();
      }
      if (this._recordID !== recordID) {
        this.nestObservationService
          .findByRecordID(value.id)
          .subscribe((no) => (this.nestObservation = no));
      }
    }
    this._recordRevision = value;
  }

  get recordRevision() {
    return this._recordRevision;
  }
}

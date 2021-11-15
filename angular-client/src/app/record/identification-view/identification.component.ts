import { Component, Input, OnInit } from '@angular/core';
import { RecordEntity } from '../../domain/record.entity';
import { TransmitterService } from '../../transmitter/transmitter.service';
import { TransmitterEntity } from '../../domain/transmitter.entity';

@Component({
  selector: 'app-identification-view',
  templateUrl: './identification.component.html',
})
export class IdentificationComponent implements OnInit {
  _recordEntity: RecordEntity;

  transmitterEntity: TransmitterEntity = null;

  constructor(private transmitterService: TransmitterService) {}

  ngOnInit() {}

  @Input()
  set recordEntity(value: RecordEntity) {
    this.transmitterEntity = null;
    this._recordEntity = value;
    // Had to check both for undefined, was showing errors in console where transmitterId was undefined.
    if (value && value.transmitterChange && value.transmitterChange.txTo) {
      this.transmitterService
        .findById(value.transmitterChange.txTo)
        .subscribe((t: TransmitterEntity) => {
          this.transmitterEntity = t;
        });
    }
  }

  get recordEntity() {
    return this._recordEntity;
  }
}

import { Component, Input, OnInit } from '@angular/core';
import { TransmitterEntity } from '../../domain/transmitter.entity';
import { TransmitterService } from '../../transmitter/transmitter.service';

@Component({
  selector: 'app-transmitter-id-label',
  templateUrl: './transmitter-id-label.component.html',
})
export class TransmitterIdLabelComponent implements OnInit {
  private _transmitterID: string;
  transmitterEntity: TransmitterEntity;

  constructor(private transmitterService: TransmitterService) {}

  ngOnInit() {}

  @Input()
  set transmitterID(value: string) {
    this._transmitterID = value;
    if (this._transmitterID !== null) {
      this.transmitterService
        .findById(value)
        .subscribe(
          (transmitter: TransmitterEntity) =>
            (this.transmitterEntity = transmitter)
        );
    }
  }
}

import { Component, Input, OnInit } from '@angular/core';
import { BirdTransmitterHistoryDto } from '../../bird-transmitter-history-dto';
import { BirdService } from '../../bird.service';

@Component({
  selector: 'app-transmitter-history',
  templateUrl: './transmitter-history.component.html',
})
export class TransmitterHistoryComponent implements OnInit {
  historyList: BirdTransmitterHistoryDto[];

  private _birdId: string;

  constructor(private birdService: BirdService) {}

  ngOnInit() {}

  @Input()
  set birdId(value: string) {
    this._birdId = value;
    if (value) {
      this.birdService
        .getTransmitterHistory(value)
        .subscribe(
          (result: BirdTransmitterHistoryDto[]) => (this.historyList = result)
        );
    }
  }

  get birdId() {
    return this._birdId;
  }
}

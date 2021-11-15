import { Component, Input, OnInit } from '@angular/core';
import { TransmitterBirdHistoryDto } from '../../transmitter-bird-history-dto';
import { TransmitterService } from '../../transmitter.service';

@Component({
  selector: 'app-transmitter-bird-history',
  templateUrl: './transmitter-bird-history.component.html',
})
export class TransmitterBirdHistoryComponent implements OnInit {
  historyList: TransmitterBirdHistoryDto[];

  private _txDocId: string;

  constructor(private service: TransmitterService) {}

  ngOnInit() {}

  @Input()
  set txDocId(value: string) {
    this._txDocId = value;
    if (value) {
      this.service
        .getBirdHistory(value)
        .subscribe((result) => (this.historyList = result));
    }
  }

  get txDocId() {
    return this._txDocId;
  }
}

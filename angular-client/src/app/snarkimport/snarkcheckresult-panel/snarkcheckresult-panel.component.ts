import { Component, Input, OnInit } from '@angular/core';

import { SnarkCheckResultDTO } from '../snark-check-result-dto';

@Component({
  selector: 'app-snarkcheckresult-panel',
  templateUrl: 'snarkcheckresult-panel.component.html',
  styleUrls: ['./snarkcheckresult.component.scss'],
})
export class SnarkCheckResultPanelComponent implements OnInit {
  private _snarkCheckResult: SnarkCheckResultDTO;
  summary: any[];
  @Input()
  loading = false;

  constructor() {}

  ngOnInit(): void {}

  @Input()
  get snarkCheckResult(): SnarkCheckResultDTO {
    return this._snarkCheckResult;
  }

  set snarkCheckResult(value: SnarkCheckResultDTO) {
    this.summary = null;
    this._snarkCheckResult = value;
    if (value) {
      this.summary = [];
      value.eveningList.forEach((e) => {
        const eveningSummary = [];
        e.snarkRecordList.forEach((sr) => {
          const channel = sr.channel;
          let birdSummary = eveningSummary.find((es) => es.channel === channel);
          if (!birdSummary) {
            birdSummary = {
              date: e.date,
              birdID: sr.birdID,
              firstArrival: sr.arriveDateTime,
              channel: sr.channel,
            };
            eveningSummary.push(birdSummary);
          }
          if (sr.weight) {
            birdSummary.lastestWeight = sr.weight;
          }
          birdSummary.lastDeparture = sr.departDateTime;
        });
        this.summary = this.summary.concat(eveningSummary);
      });
    }
  }
}

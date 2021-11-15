import { Component, Input, OnInit } from '@angular/core';
import { BandsAndChipsDto } from '../../../common/bands-and-chips-dto';
import { RecordService } from '../../../record/record.service';

@Component({
  selector: 'app-band-history',
  templateUrl: './band-history.component.html',
})
export class BandHistoryComponent implements OnInit {
  private _birdID: string;

  bandHistory: BandsAndChipsDto[];

  constructor(private recordService: RecordService) {}

  @Input()
  set birdId(birdID: string) {
    this._birdID = birdID;
    this.loadBandHistory();
  }

  get birdID(): string {
    return this._birdID;
  }

  ngOnInit() {}

  loadBandHistory() {
    const birdId: string = this.birdID;
    this.recordService
      .findBandHistoryByBirdId(birdId)
      .subscribe((result) => (this.bandHistory = result));
  }
}

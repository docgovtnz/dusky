import { Component, Input, OnInit } from '@angular/core';
import { BandsAndChipsDto } from '../../../common/bands-and-chips-dto';
import { RecordService } from '../../../record/record.service';

@Component({
  selector: 'app-chip-history',
  templateUrl: './chip-history.component.html',
})
export class ChipHistoryComponent implements OnInit {
  private _birdID: string;

  chipHistory: BandsAndChipsDto[];

  constructor(private recordService: RecordService) {}

  @Input()
  set birdId(birdID: string) {
    this._birdID = birdID;
    this.loadChipHistory();
  }

  get birdID(): string {
    return this._birdID;
  }

  ngOnInit() {}

  loadChipHistory() {
    const birdId: string = this.birdID;
    this.recordService
      .findChipHistoryByBirdId(birdId)
      .subscribe((result) => (this.chipHistory = result));
  }
}

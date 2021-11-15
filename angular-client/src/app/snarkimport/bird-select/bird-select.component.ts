import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { BirdSummaryDto } from '../../common/bird-summary-dto';
import { BirdService } from '../../bird/bird.service';
import { BirdSummaryCriteria } from '../../common/bird-summary-criteria';

@Component({
  selector: 'app-bird-select',
  templateUrl: './bird-select.component.html',
})
export class BirdSelectComponent implements OnInit {
  private _birdIDList: string[];

  birdSummaryList: BirdSummaryDto[] = [];

  @Input()
  birdID: string;

  @Output()
  birdIDChange = new EventEmitter();

  constructor(private birdService: BirdService) {}

  ngOnInit() {}

  get birdIDList(): string[] {
    return this._birdIDList;
  }

  @Input()
  set birdIDList(value: string[]) {
    this._birdIDList = value;
    if (value) {
      const criteria = new BirdSummaryCriteria();
      criteria.birdIDList = value;
      this.birdService
        .findSummaryDTOByCriteria(criteria)
        .subscribe((response) => {
          this.birdSummaryList = response;
        });
    }
  }

  onBirdIDChange() {
    this.birdIDChange.next(this.birdID);
  }
}

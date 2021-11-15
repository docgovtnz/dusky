import { Component, Input, OnInit } from '@angular/core';
import { BirdService } from '../bird.service';
import { OptionsService } from '../../common/options.service';
import { map } from 'rxjs/operators';

import { BirdSummaryDto } from '../../common/bird-summary-dto';

@Component({
  selector: 'app-bird-name-label',
  templateUrl: './bird-name-label.component.html',
})
export class BirdNameLabelComponent implements OnInit {
  private _birdID: string;
  birdSummary: BirdSummaryDto;

  @Input()
  link = true;

  constructor(private optionsService: OptionsService) {}

  ngOnInit() {}

  @Input()
  set birdID(value: string) {
    this.birdSummary = null;
    this._birdID = value;
    if (value !== null) {
      this.optionsService
        .getBirdSummaries()
        .pipe(
          map((dtoArray: BirdSummaryDto[]) =>
            dtoArray.find((dto) => dto.id === this._birdID)
          )
        )
        .subscribe((dto) => (this.birdSummary = dto));
    }
  }
}

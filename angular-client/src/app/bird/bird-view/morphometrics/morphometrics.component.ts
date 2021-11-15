import { Component, Input, OnInit } from '@angular/core';
import { DatedMeasureDetailDto } from '../../../common/dated-measure-detail-dto';
import { BirdService } from '../../bird.service';

@Component({
  selector: 'app-morphometrics',
  templateUrl: './morphometrics.component.html',
})
export class MorphometricsComponent implements OnInit {
  private _birdID: string;

  morphometrics: DatedMeasureDetailDto;

  constructor(private birdService: BirdService) {}

  @Input()
  set birdID(birdID: string) {
    this._birdID = birdID;
    this.loadMorphometrics();
  }

  get birdID(): string {
    return this._birdID;
  }

  ngOnInit() {}

  loadMorphometrics() {
    if (this.birdID) {
      this.morphometrics = null;
      this.birdService
        .findCurrentMeasureDetailByBirdID(this.birdID)
        .subscribe((result) => (this.morphometrics = result));
    }
  }
}

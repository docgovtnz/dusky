import { Component, Input, OnInit } from '@angular/core';
import { BirdService } from '../bird.service';

@Component({
  selector: 'app-bird-age-label',
  templateUrl: './bird-age-label.component.html',
})
export class BirdAgeLabelComponent implements OnInit {
  ageInDays: number;

  private _birdID: string;

  constructor(private birdService: BirdService) {}

  ngOnInit() {}

  get birdID(): string {
    return this._birdID;
  }

  @Input()
  set birdID(value: string) {
    this.ageInDays = null;
    this._birdID = value;
    if (value) {
      this.birdService
        .getAgeInDays(value)
        .subscribe((ageInDays) => (this.ageInDays = ageInDays));
    }
  }
}

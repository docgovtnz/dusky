import * as ColorHash from 'color-hash';

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

import { BirdData } from '../esri-map.component';
import { MapColorService } from '../map-color.service';

@Component({
  selector: 'app-bird-legend',
  templateUrl: './bird-legend.component.html',
  styleUrls: ['./bird-legend.component.scss'],
})
export class BirdLegendComponent implements OnInit {
  colorHash = new ColorHash();

  @Input()
  birdList: BirdData[];

  @Output()
  birdSelectionChanges = new EventEmitter();

  constructor(private colorService: MapColorService) {}

  ngOnInit() {}

  getColorForBird(birdItemFromList: any) {
    // const color = this.colorHash.hex(birdItemFromList.birdID);
    // return color;
    let colorP = 0;
    const colorRamp = this.colorService.colorRamp;
    for (colorP = 0; colorP < colorRamp.length; colorP++) {
      if (
        birdItemFromList.birdID === this.colorService.birdList[colorP].birdID
      ) {
        break;
      }
    }
    // console.log('Legend color');
    // console.log(this.colorService.birdList);
    // console.log(colorRamp);
    // console.log(colorP)
    if (colorP < colorRamp.length) {
      const rgb = colorRamp[colorP];
      let hex1 = Number(rgb[0]).toString(16);
      if (hex1.length < 2) {
        hex1 = '0' + hex1;
      }
      let hex2 = Number(rgb[1]).toString(16);
      if (hex2.length < 2) {
        hex2 = '0' + hex2;
      }
      let hex3 = Number(rgb[2]).toString(16);
      if (hex3.length < 2) {
        hex3 = '0' + hex3;
      }
      return '#' + hex1 + hex2 + hex3;
    } else {
      const color = this.colorHash.hex(birdItemFromList.birdID);
      return color;
    }
  }

  selectAll() {
    this.setAllBirdsSelected(true);
  }

  unselectAll() {
    this.setAllBirdsSelected(false);
  }

  private setAllBirdsSelected(value: boolean) {
    if (this.birdList && this.birdList.length > 0) {
      let changes = false;
      this.birdList.forEach((bird) => {
        const changedOne = this.setOneBirdSelected(bird, value);
        if (changedOne) {
          changes = true;
        }
      });

      if (changes) {
        this.birdSelectionChanges.next();
      }
    }
  }

  private setOneBirdSelected(bird: any, value: boolean) {
    const change = bird.selected !== value;
    bird.selected = value;
    return change;
  }

  toggleSelected(bird: any) {
    // toggle this bird to the opposite of its' current value
    const changed = this.setOneBirdSelected(bird, !bird.selected);
    if (changed) {
      this.birdSelectionChanges.next();
    }
  }
}

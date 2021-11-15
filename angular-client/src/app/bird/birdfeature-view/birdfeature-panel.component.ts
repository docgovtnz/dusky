import { Component, Input, OnInit } from '@angular/core';

import { BirdFeatureEntity } from '../../domain/birdfeature.entity';

@Component({
  selector: 'app-birdfeature-panel',
  templateUrl: 'birdfeature-panel.component.html',
})
export class BirdFeaturePanelComponent implements OnInit {
  @Input()
  birdfeatureList: BirdFeatureEntity[];

  constructor() {}

  ngOnInit(): void {}
}

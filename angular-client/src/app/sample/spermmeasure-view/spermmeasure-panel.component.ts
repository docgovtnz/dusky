import { Component, Input, OnInit } from '@angular/core';

import { SpermMeasureEntity } from '../../domain/spermmeasure.entity';

@Component({
  selector: 'app-spermmeasure-panel',
  templateUrl: 'spermmeasure-panel.component.html',
})
export class SpermMeasurePanelComponent implements OnInit {
  @Input()
  spermmeasureList: SpermMeasureEntity[];

  constructor() {}

  ngOnInit(): void {}
}

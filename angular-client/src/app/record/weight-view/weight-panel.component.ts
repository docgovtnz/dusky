import { Component, Input, OnInit } from '@angular/core';

import { WeightEntity } from '../../domain/weight.entity';

@Component({
  selector: 'app-weight-panel',
  templateUrl: 'weight-panel.component.html',
})
export class WeightPanelComponent implements OnInit {
  @Input()
  weightRevision: WeightEntity;

  constructor() {}

  ngOnInit(): void {}
}

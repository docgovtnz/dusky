import { Component, Input, OnInit } from '@angular/core';

import { ObservationTimesEntity } from '../../domain/observationtimes.entity';

@Component({
  selector: 'app-observationtimes-panel',
  templateUrl: 'observationtimes-panel.component.html',
})
export class ObservationTimesPanelComponent implements OnInit {
  @Input()
  observationtimesRevision: ObservationTimesEntity;

  constructor() {}

  ngOnInit(): void {}
}

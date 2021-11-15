import { Component, Input, OnInit } from '@angular/core';

import { EmbryoMeasurementsEntity } from '../../domain/embryomeasurements.entity';

@Component({
  selector: 'app-embryomeasurements-panel',
  templateUrl: 'embryomeasurements-panel.component.html',
})
export class EmbryoMeasurementsPanelComponent implements OnInit {
  @Input()
  embryomeasurementsRevision: EmbryoMeasurementsEntity;

  constructor() {}

  ngOnInit(): void {}
}

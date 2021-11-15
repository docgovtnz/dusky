import { Component, Input, OnInit } from '@angular/core';

import { HealthCheckEntity } from '../../domain/healthcheck.entity';
import { BirdEntity } from '../../domain/bird.entity';

@Component({
  selector: 'app-healthcheck-panel',
  templateUrl: 'healthcheck-panel.component.html',
})
export class HealthCheckPanelComponent implements OnInit {
  @Input()
  healthcheckRevision: HealthCheckEntity;

  @Input()
  birdEntity: BirdEntity;

  constructor() {}

  ngOnInit(): void {}
}

import { Component, Input, OnInit } from '@angular/core';

import { HealthStatusEntity } from '../../domain/healthstatus.entity';

@Component({
  selector: 'app-healthstatus-panel',
  templateUrl: 'healthstatus-panel.component.html',
})
export class HealthStatusPanelComponent implements OnInit {
  @Input()
  healthstatusRevision: HealthStatusEntity;

  constructor() {}

  ngOnInit(): void {}
}

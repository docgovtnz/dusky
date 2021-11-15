import { Component, Input, OnInit } from '@angular/core';

import { BatteryLifeEntity } from '../../domain/batterylife.entity';

@Component({
  selector: 'app-batterylife-panel',
  templateUrl: 'batterylife-panel.component.html',
})
export class BatteryLifePanelComponent implements OnInit {
  @Input()
  batterylifeRevision: BatteryLifeEntity;

  constructor() {}

  ngOnInit(): void {}
}

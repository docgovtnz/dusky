import { Component, Input, OnInit } from '@angular/core';

import { NestChamberEntity } from '../../domain/nestchamber.entity';

@Component({
  selector: 'app-nestchamber-panel',
  templateUrl: 'nestchamber-panel.component.html',
})
export class NestChamberPanelComponent implements OnInit {
  @Input()
  nestchamberRevision: NestChamberEntity;

  constructor() {}

  ngOnInit(): void {}
}

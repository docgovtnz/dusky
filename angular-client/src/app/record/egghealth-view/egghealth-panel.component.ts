import { Component, Input, OnInit } from '@angular/core';

import { EggHealthEntity } from '../../domain/egghealth.entity';

@Component({
  selector: 'app-egghealth-panel',
  templateUrl: 'egghealth-panel.component.html',
})
export class EggHealthPanelComponent implements OnInit {
  @Input()
  egghealthRevision: EggHealthEntity;

  constructor() {}

  ngOnInit(): void {}
}

import { Component, Input, OnInit } from '@angular/core';

import { ChickHealthEntity } from '../../domain/chickhealth.entity';

@Component({
  selector: 'app-chickhealth-panel',
  templateUrl: 'chickhealth-panel.component.html',
})
export class ChickHealthPanelComponent implements OnInit {
  @Input()
  chickhealthRevision: ChickHealthEntity;

  constructor() {}

  ngOnInit(): void {}
}

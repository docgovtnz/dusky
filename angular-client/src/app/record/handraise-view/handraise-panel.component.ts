import { Component, Input, OnInit } from '@angular/core';

import { HandRaiseEntity } from '../../domain/handraise.entity';

@Component({
  selector: 'app-handraise-panel',
  templateUrl: 'handraise-panel.component.html',
})
export class HandRaisePanelComponent implements OnInit {
  @Input()
  handraiseRevision: HandRaiseEntity;

  constructor() {}

  ngOnInit(): void {}
}

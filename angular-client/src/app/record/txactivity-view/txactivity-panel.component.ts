import { Component, Input, OnInit } from '@angular/core';

import { TxActivityEntity } from '../../domain/txactivity.entity';

@Component({
  selector: 'app-txactivity-panel',
  templateUrl: 'txactivity-panel.component.html',
})
export class TxActivityPanelComponent implements OnInit {
  @Input()
  txactivityRevision: TxActivityEntity;

  constructor() {}

  ngOnInit(): void {}
}

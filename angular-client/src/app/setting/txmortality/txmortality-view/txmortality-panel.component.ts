import { Component, Input, OnInit } from '@angular/core';

import { TxMortalityEntity } from '../../../domain/txmortality.entity';

@Component({
  selector: 'app-txmortality-panel',
  templateUrl: 'txmortality-panel.component.html',
})
export class TxMortalityPanelComponent implements OnInit {
  @Input()
  txmortalityRevision: TxMortalityEntity;

  constructor() {}

  ngOnInit(): void {}
}

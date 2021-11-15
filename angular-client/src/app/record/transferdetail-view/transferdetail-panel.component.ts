import { Component, Input, OnInit } from '@angular/core';

import { TransferDetailEntity } from '../../domain/transferdetail.entity';

@Component({
  selector: 'app-transferdetail-panel',
  templateUrl: 'transferdetail-panel.component.html',
})
export class TransferDetailPanelComponent implements OnInit {
  @Input()
  transferdetailRevision: TransferDetailEntity;

  constructor() {}

  ngOnInit(): void {}
}

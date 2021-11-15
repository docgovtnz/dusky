import { Component, Input, OnInit } from '@angular/core';

import { SpermDetailEntity } from '../../domain/spermdetail.entity';

@Component({
  selector: 'app-spermdetail-panel',
  templateUrl: 'spermdetail-panel.component.html',
})
export class SpermDetailPanelComponent implements OnInit {
  @Input()
  spermdetailRevision: SpermDetailEntity;

  constructor() {}

  ngOnInit(): void {}
}

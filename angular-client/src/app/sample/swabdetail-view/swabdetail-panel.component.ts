import { Component, Input, OnInit } from '@angular/core';

import { SwabDetailEntity } from '../../domain/swabdetail.entity';

@Component({
  selector: 'app-swabdetail-panel',
  templateUrl: 'swabdetail-panel.component.html',
})
export class SwabDetailPanelComponent implements OnInit {
  @Input()
  swabdetailRevision: SwabDetailEntity;

  constructor() {}

  ngOnInit(): void {}
}

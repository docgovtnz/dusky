import { Component, Input, OnInit } from '@angular/core';

import { MeasureDetailEntity } from '../../domain/measuredetail.entity';

@Component({
  selector: 'app-measuredetail-panel',
  templateUrl: 'measuredetail-panel.component.html',
})
export class MeasureDetailPanelComponent implements OnInit {
  @Input()
  measuredetailRevision: MeasureDetailEntity;

  constructor() {}

  ngOnInit(): void {}
}

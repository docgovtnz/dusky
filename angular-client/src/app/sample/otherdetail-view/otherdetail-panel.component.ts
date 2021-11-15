import { Component, Input, OnInit } from '@angular/core';

import { OtherDetailEntity } from '../../domain/otherdetail.entity';

@Component({
  selector: 'app-otherdetail-panel',
  templateUrl: 'otherdetail-panel.component.html',
})
export class OtherDetailPanelComponent implements OnInit {
  @Input()
  otherdetailRevision: OtherDetailEntity;

  constructor() {}

  ngOnInit(): void {}
}

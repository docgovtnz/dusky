import { Component, Input, OnInit } from '@angular/core';

import { CaptureDetailEntity } from '../../domain/capturedetail.entity';

@Component({
  selector: 'app-capturedetail-panel',
  templateUrl: 'capturedetail-panel.component.html',
})
export class CaptureDetailPanelComponent implements OnInit {
  @Input()
  capturedetailRevision: CaptureDetailEntity;

  constructor() {}

  ngOnInit(): void {}
}

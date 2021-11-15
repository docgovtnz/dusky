import { Component, Input, OnInit } from '@angular/core';

import { BloodSampleDetailEntity } from '../../domain/bloodsampledetail.entity';

@Component({
  selector: 'app-blooddetail-panel',
  templateUrl: 'blooddetail-panel.component.html',
})
export class BloodDetailPanelComponent implements OnInit {
  @Input()
  blooddetailRevision: BloodSampleDetailEntity;

  constructor() {}

  ngOnInit(): void {}
}

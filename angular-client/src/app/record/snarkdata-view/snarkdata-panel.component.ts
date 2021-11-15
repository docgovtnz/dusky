import { Component, Input, OnInit } from '@angular/core';

import { SnarkDataEntity } from '../../domain/snarkdata.entity';

@Component({
  selector: 'app-snarkdata-panel',
  templateUrl: 'snarkdata-panel.component.html',
})
export class SnarkDataPanelComponent implements OnInit {
  @Input()
  snarkdataRevision: SnarkDataEntity;

  constructor() {}

  ngOnInit(): void {}
}

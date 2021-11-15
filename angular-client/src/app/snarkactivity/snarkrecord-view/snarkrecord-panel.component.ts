import { Component, Input, OnInit } from '@angular/core';

import { SnarkRecordEntity } from '../../domain/snarkrecord.entity';

@Component({
  selector: 'app-snarkrecord-panel',
  templateUrl: 'snarkrecord-panel.component.html',
})
export class SnarkRecordPanelComponent implements OnInit {
  @Input()
  snarkrecordList: SnarkRecordEntity[];

  constructor() {}

  ngOnInit(): void {}
}

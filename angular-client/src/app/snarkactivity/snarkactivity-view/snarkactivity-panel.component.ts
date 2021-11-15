import { Component, Input, OnInit } from '@angular/core';

import { SnarkActivityEntity } from '../../domain/snarkactivity.entity';

@Component({
  selector: 'app-snarkactivity-panel',
  templateUrl: 'snarkactivity-panel.component.html',
})
export class SnarkActivityPanelComponent implements OnInit {
  @Input()
  snarkactivityRevision: SnarkActivityEntity;

  constructor() {}

  ngOnInit(): void {}
}

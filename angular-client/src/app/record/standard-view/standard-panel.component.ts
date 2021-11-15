import { Component, Input, OnInit } from '@angular/core';

import { StandardEntity } from '../../domain/standard.entity';
import { RecordEntity } from '../../domain/record.entity';

@Component({
  selector: 'app-standard-panel',
  templateUrl: 'standard-panel.component.html',
})
export class StandardPanelComponent implements OnInit {
  @Input()
  standardRevision: StandardEntity;

  @Input()
  recordRevision: RecordEntity;

  constructor() {}

  ngOnInit(): void {}
}

import { Component, Input, OnInit } from '@angular/core';

import { CheckmateEntity } from '../../domain/checkmate.entity';
import { RecordEntity } from '../../domain/record.entity';

@Component({
  selector: 'app-checkmate-panel',
  templateUrl: 'checkmate-panel.component.html',
})
export class CheckmatePanelComponent implements OnInit {
  @Input()
  checkmateRevision: CheckmateEntity;

  @Input()
  recordRevision: RecordEntity;

  constructor() {}

  ngOnInit(): void {}
}

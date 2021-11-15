import { Component, Input, OnInit } from '@angular/core';

import { EggTimerEntity } from '../../domain/eggtimer.entity';
import { RecordEntity } from '../../domain/record.entity';

@Component({
  selector: 'app-eggtimer-panel',
  templateUrl: 'eggtimer-panel.component.html',
})
export class EggTimerPanelComponent implements OnInit {
  @Input()
  eggtimerRevision: EggTimerEntity;

  @Input()
  recordRevision: RecordEntity;

  constructor() {}

  ngOnInit(): void {}
}

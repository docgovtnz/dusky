import { Component, Input, OnInit } from '@angular/core';

import { SupplementaryFeedingEntity } from '../../domain/supplementaryfeeding.entity';

@Component({
  selector: 'app-supplementaryfeeding-panel',
  templateUrl: 'supplementaryfeeding-panel.component.html',
})
export class SupplementaryFeedingPanelComponent implements OnInit {
  @Input()
  supplementaryfeedingRevision: SupplementaryFeedingEntity;

  constructor() {}

  ngOnInit(): void {}
}

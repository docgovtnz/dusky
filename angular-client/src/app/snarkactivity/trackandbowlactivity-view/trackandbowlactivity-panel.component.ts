import { Component, Input, OnInit } from '@angular/core';

import { TrackAndBowlActivityEntity } from '../../domain/trackandbowlactivity.entity';

@Component({
  selector: 'app-trackandbowlactivity-panel',
  templateUrl: 'trackandbowlactivity-panel.component.html',
})
export class TrackAndBowlActivityPanelComponent implements OnInit {
  @Input()
  trackandbowlactivityRevision: TrackAndBowlActivityEntity;

  constructor() {}

  ngOnInit(): void {}
}

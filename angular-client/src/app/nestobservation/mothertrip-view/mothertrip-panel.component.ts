import { Component, Input, OnInit } from '@angular/core';

import { MotherTripEntity } from '../../domain/mothertrip.entity';
import { MotherTripSummaryEntity } from '../../domain/mothertripsummary.entity';

@Component({
  selector: 'app-mothertrip-panel',
  templateUrl: 'mothertrip-panel.component.html',
})
export class MotherTripPanelComponent implements OnInit {
  @Input()
  mothertripList: MotherTripEntity[];

  @Input()
  mothertripsummaryRevision: MotherTripSummaryEntity;

  constructor() {}

  ngOnInit(): void {}
}

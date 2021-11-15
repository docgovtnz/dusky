import { Component, Input, OnInit } from '@angular/core';

import { NestObservationEntity } from '../../domain/nestobservation.entity';

@Component({
  selector: 'app-nestobservation-panel',
  templateUrl: 'nestobservation-panel.component.html',
})
export class NestObservationPanelComponent implements OnInit {
  @Input()
  nestobservationRevision: NestObservationEntity;

  constructor() {}

  ngOnInit(): void {}
}

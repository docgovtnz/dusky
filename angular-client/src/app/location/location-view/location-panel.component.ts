import { Component, Input, OnInit } from '@angular/core';

import { LocationEntity } from '../../domain/location.entity';

@Component({
  selector: 'app-location-panel',
  templateUrl: 'location-panel.component.html',
})
export class LocationPanelComponent implements OnInit {
  @Input()
  locationRevision: LocationEntity;

  constructor() {}

  ngOnInit(): void {}
}

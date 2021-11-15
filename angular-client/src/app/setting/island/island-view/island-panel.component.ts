import { Component, Input, OnInit } from '@angular/core';

import { IslandEntity } from '../../../domain/island.entity';

@Component({
  selector: 'app-island-panel',
  templateUrl: 'island-panel.component.html',
})
export class IslandPanelComponent implements OnInit {
  @Input()
  islandRevision: IslandEntity;

  constructor() {}

  ngOnInit(): void {}
}

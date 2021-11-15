import { Component, Input, OnInit } from '@angular/core';

import { BirdEntity } from '../../domain/bird.entity';

@Component({
  selector: 'app-bird-panel',
  templateUrl: 'bird-panel.component.html',
})
export class BirdPanelComponent implements OnInit {
  @Input()
  birdRevision: BirdEntity;

  constructor() {}

  ngOnInit(): void {}
}

import { Component, Input, OnInit } from '@angular/core';

import { NoraNetEntity } from '../entities/noranet.entity';

@Component({
  selector: 'app-noranet-panel',
  templateUrl: 'noranet-panel.component.html',
})
export class NoraNetPanelComponent implements OnInit {
  @Input()
  noranetRevision: NoraNetEntity;

  constructor() {}

  ngOnInit(): void {}
}

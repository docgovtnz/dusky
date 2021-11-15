import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { NoraNetStandardEntity } from '../../entities/noranetstandard.entity';

@Component({
  selector: 'app-noranet-standard-panel',
  templateUrl: './noranet-standard.component.html',
})
export class NoraNetStandardPanelComponent implements OnInit {
  @Input()
  standardList: NoraNetStandardEntity[];

  constructor(private router: Router) {}

  ngOnInit(): void {}
}

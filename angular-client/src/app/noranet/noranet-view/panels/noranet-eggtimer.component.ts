import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { NoraNetEggTimerEntity } from '../../entities/noraneteggtimer.entity';

@Component({
  selector: 'app-noranet-eggtimer-panel',
  templateUrl: './noranet-eggtimer.component.html',
})
export class NoraNetEggTimerPanelComponent implements OnInit {
  @Input()
  eggTimerList: NoraNetEggTimerEntity[];

  constructor(private router: Router) {}

  ngOnInit(): void {}
}

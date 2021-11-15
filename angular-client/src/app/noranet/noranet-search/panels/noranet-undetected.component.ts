import { Component, Input, Output, OnInit, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';

import { NoraNetCriteria } from '../../noranet.criteria';
import { UndetectedBirdDTO } from '../../undetected-bird.dto';

@Component({
  selector: 'app-noranet-undetected-panel',
  templateUrl: './noranet-undetected.component.html',
})
export class NoraNetUndetectedPanelComponent implements OnInit {
  @Input()
  undetectedBirdsResponse: UndetectedBirdDTO[];

  @Input()
  noranetCriteria: NoraNetCriteria;

  constructor(private router: Router) {}

  ngOnInit(): void {}
}

import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { NoraNetCmShortEntity } from '../../entities/noranetcmshort.entity';

@Component({
  selector: 'app-noranet-cmshort-panel',
  templateUrl: './noranet-cmshort.component.html',
})
export class NoraNetCmShortPanelComponent implements OnInit {
  @Input()
  cmShortList: NoraNetCmShortEntity[];

  constructor(private router: Router) {}

  ngOnInit(): void {}
}

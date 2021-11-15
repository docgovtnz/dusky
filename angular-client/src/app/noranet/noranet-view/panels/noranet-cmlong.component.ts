import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { NoraNetCmLongEntity } from '../../entities/noranetcmlong.entity';

@Component({
  selector: 'app-noranet-cmlong-panel',
  templateUrl: './noranet-cmlong.component.html',
})
export class NoraNetCmLongPanelComponent implements OnInit {
  @Input()
  cmLongList: NoraNetCmLongEntity[];

  constructor(private router: Router) {}

  ngOnInit(): void {}
}

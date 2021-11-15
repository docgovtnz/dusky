import { Component, Input, OnInit } from '@angular/core';

import { HaematologyTestEntity } from '../../domain/haematologytest.entity';

@Component({
  selector: 'app-haematologytest-panel',
  templateUrl: 'haematologytest-panel.component.html',
})
export class HaematologyTestPanelComponent implements OnInit {
  @Input()
  haematologytestList: HaematologyTestEntity[];

  constructor() {}

  ngOnInit(): void {}
}

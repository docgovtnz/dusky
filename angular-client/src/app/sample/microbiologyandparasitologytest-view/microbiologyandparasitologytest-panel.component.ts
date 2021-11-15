import { Component, Input, OnInit } from '@angular/core';

import { MicrobiologyAndParasitologyTestEntity } from '../../domain/microbiologyandparasitologytest.entity';

@Component({
  selector: 'app-microbiologyandparasitologytest-panel',
  templateUrl: 'microbiologyandparasitologytest-panel.component.html',
})
export class MicrobiologyAndParasitologyTestPanelComponent implements OnInit {
  @Input()
  microbiologyandparasitologytestList: MicrobiologyAndParasitologyTestEntity[];

  constructor() {}

  ngOnInit(): void {}
}

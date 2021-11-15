import { Component, Input, OnInit } from '@angular/core';

import { SampleEntity } from '../../domain/sample.entity';

@Component({
  selector: 'app-sample-panel',
  templateUrl: 'sample-panel.component.html',
})
export class SamplePanelComponent implements OnInit {
  @Input()
  sampleRevision: SampleEntity;
  @Input()
  recordID: string;

  constructor() {}

  ngOnInit(): void {}
}

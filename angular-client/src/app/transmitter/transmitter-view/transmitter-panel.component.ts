import { Component, Input, OnInit } from '@angular/core';

import { TransmitterEntity } from '../../domain/transmitter.entity';

@Component({
  selector: 'app-transmitter-panel',
  templateUrl: 'transmitter-panel.component.html',
})
export class TransmitterPanelComponent implements OnInit {
  @Input()
  transmitterRevision: TransmitterEntity;

  constructor() {}

  ngOnInit(): void {}
}

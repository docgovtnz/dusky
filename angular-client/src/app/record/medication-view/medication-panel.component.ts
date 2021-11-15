import { Component, Input, OnInit } from '@angular/core';

import { MedicationEntity } from '../../domain/medication.entity';

@Component({
  selector: 'app-medication-panel',
  templateUrl: 'medication-panel.component.html',
})
export class MedicationPanelComponent implements OnInit {
  @Input()
  medicationList: MedicationEntity[];

  constructor() {}

  ngOnInit(): void {}
}

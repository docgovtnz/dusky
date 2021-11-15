import { Component, Input, OnInit } from '@angular/core';

import { ChemistryAssayEntity } from '../../domain/chemistryassay.entity';

@Component({
  selector: 'app-chemistryassay-panel',
  templateUrl: 'chemistryassay-panel.component.html',
})
export class ChemistryAssayPanelComponent implements OnInit {
  @Input()
  chemistryassayList: ChemistryAssayEntity[];

  constructor() {}

  ngOnInit(): void {}
}

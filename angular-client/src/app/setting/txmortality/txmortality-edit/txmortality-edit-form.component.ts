import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { OptionsService } from '../../../common/options.service';

@Component({
  selector: 'app-txmortality-edit-form',
  templateUrl: './txmortality-edit-form.component.html',
})
export class TxMortalityEditFormComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  constructor(public optionsService: OptionsService) {}

  ngOnInit() {}
}

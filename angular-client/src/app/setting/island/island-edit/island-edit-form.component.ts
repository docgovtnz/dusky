import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { OptionsService } from '../../../common/options.service';

@Component({
  selector: 'app-island-edit-form',
  templateUrl: './island-edit-form.component.html',
})
export class IslandEditFormComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  constructor(public optionsService: OptionsService) {}

  ngOnInit() {}
}

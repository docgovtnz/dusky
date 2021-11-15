import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { OptionsService } from '../../../common/options.service';

@Component({
  selector: 'app-option-list-new-form',
  templateUrl: './option-list-new-form.component.html',
})
export class OptionListNewFormComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  constructor(public optionsService: OptionsService) {}

  ngOnInit() {}
}

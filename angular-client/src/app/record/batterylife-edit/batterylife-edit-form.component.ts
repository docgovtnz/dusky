import { Component, Input, OnInit } from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-batterylife-edit-form',
  templateUrl: './batterylife-edit-form.component.html',
})
export class BatteryLifeEditFormComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  constructor(public optionsService: OptionsService) {}

  ngOnInit() {}
}

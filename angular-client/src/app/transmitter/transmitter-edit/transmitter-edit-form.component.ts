import { Component, Input, OnInit } from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-transmitter-edit-form',
  templateUrl: './transmitter-edit-form.component.html',
})
export class TransmitterEditFormComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  @Input()
  expiry: Date;

  constructor(public optionsService: OptionsService) {}

  ngOnInit() {}
}

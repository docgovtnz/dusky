import { Component, Input, OnInit } from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-snarkactivity-edit-form',
  templateUrl: './snarkactivity-edit-form.component.html',
})
export class SnarkActivityEditFormComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  constructor(public optionsService: OptionsService) {}

  ngOnInit() {}
}

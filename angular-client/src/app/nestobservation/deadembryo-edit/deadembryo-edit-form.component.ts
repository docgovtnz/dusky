import { Component, Input, OnInit } from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-deadembryo-edit-form',
  templateUrl: './deadembryo-edit-form.component.html',
})
export class DeadEmbryoEditFormComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  constructor(public optionsService: OptionsService) {}

  ngOnInit() {}
}

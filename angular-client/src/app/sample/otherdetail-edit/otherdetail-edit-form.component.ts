import { Component, Input, OnInit } from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-otherdetail-edit-form',
  templateUrl: './otherdetail-edit-form.component.html',
})
export class OtherDetailEditFormComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  constructor(public optionsService: OptionsService) {}

  ngOnInit() {}
}

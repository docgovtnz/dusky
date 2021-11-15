import {
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-handraise-edit-form',
  templateUrl: './handraise-edit-form.component.html',
})
export class HandRaiseEditFormComponent implements OnInit, OnChanges {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  @Input()
  isMedMandatory = false;

  constructor(public optionsService: OptionsService) {}

  ngOnInit() {
    const validate = (c: FormControl) =>
      !c.value && this.isMedMandatory
        ? { validate: 'Medication must be checked if a drug is selected' }
        : null;
    this.myFormGroup.get('medication').setValidators(validate);
    this.myFormGroup.get('medication').markAsDirty();
  }

  ngOnChanges() {
    this.myFormGroup.get('medication').updateValueAndValidity();
  }
}

import { Component, Input, OnInit } from '@angular/core';
import { SnarkRecordEntity } from '../../domain/snarkrecord.entity';
import { OptionsService } from '../../common/options.service';
import { FormGroup, FormArray } from '@angular/forms';

@Component({
  selector: 'app-snarkrecord-edit-form',
  templateUrl: './snarkrecord-edit-form.component.html',
})
export class SnarkRecordEditFormComponent implements OnInit {
  @Input()
  myFormArray: FormArray;

  constructor(public optionsService: OptionsService) {}

  ngOnInit() {}

  get myFormGroup(): FormGroup {
    // return the form group that contains the provided FormArray so that the FormArray bound to the root div element
    // in the template using the FormArrayName directive (there is no FormArrayDirective)
    return this.myFormArray.parent as FormGroup;
  }

  get myFormArrayName(): string {
    // determine the name given to the provided FormArray so that it can be bound to the root div template using the
    // FormArrayName directive (there is no FormArrayDirective)
    return Object.keys(this.myFormGroup.controls).find(
      (name) => this.myFormGroup.get(name) === this.myFormArray
    );
  }

  addItem() {
    // call addItem method added to FormArray instance by the FormService. See form.service.ts
    (this.myFormArray as any).addItem(new SnarkRecordEntity());
  }

  removeItem(i: number) {
    this.myFormArray.removeAt(i);
  }
}

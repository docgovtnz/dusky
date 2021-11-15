import { Component, Input, OnInit } from '@angular/core';
import { SpermMeasureEntity } from '../../domain/spermmeasure.entity';
import { OptionsService } from '../../common/options.service';
import { FormGroup, FormArray } from '@angular/forms';

@Component({
  selector: 'app-spermmeasure-edit-form',
  templateUrl: './spermmeasure-edit-form.component.html',
})
export class SpermMeasureEditFormComponent implements OnInit {
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
    const fg = (this.myFormArray as any).addItem(new SpermMeasureEntity());
    // populate the lab name, case number, and date processed from the previous row
    const rawValue = this.myFormArray.getRawValue();
    let previousIndex = null;
    if (rawValue.length > 1) {
      previousIndex = rawValue.length - 2;
    }
    if (previousIndex !== null) {
      const previousRow = rawValue[previousIndex];
      if (previousRow.labName) {
        fg.controls.labName.setValue(previousRow.labName);
      }
      if (previousRow.caseNumber) {
        fg.controls.caseNumber.setValue(previousRow.caseNumber);
      }
      if (previousRow.dateProcessed) {
        fg.controls.dateProcessed.setValue(previousRow.dateProcessed);
      }
    }
  }

  removeItem(i: number) {
    this.myFormArray.removeAt(i);
  }
}

import { Component, Input, OnInit } from '@angular/core';
import { HaematologyTestEntity } from '../../domain/haematologytest.entity';
import { OptionsService } from '../../common/options.service';
import { FormGroup, FormArray } from '@angular/forms';
import { SampleResultAutofill } from '../SampleResultAutofill';

@Component({
  selector: 'app-haematologytest-edit-form',
  templateUrl: './haematologytest-edit-form.component.html',
})
export class HaematologyTestEditFormComponent implements OnInit {
  @Input()
  myFormArray: FormArray;

  standardAssayOptions: string[];

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
    const fg = (this.myFormArray as any).addItem(new HaematologyTestEntity());
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

  addAutofill(autofill: SampleResultAutofill) {
    if (autofill) {
      autofill.tests.map((test) => {
        const newItem = (this.myFormArray as any).addItem(
          new HaematologyTestEntity()
        );
        newItem.controls.labName.setValue(autofill.labName);
        newItem.controls.caseNumber.setValue(autofill.caseNumber);
        newItem.controls.dateProcessed.setValue(autofill.dateProcessed);
        newItem.controls.test.setValue(test);
      });
    }
  }
}

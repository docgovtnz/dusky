import { Component, Input, OnInit } from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { FormArray, FormGroup } from '@angular/forms';
import { CheckmateDataEntity } from '../../domain/checkmatedata.entity';

@Component({
  selector: 'app-checkmate-edit-form',
  templateUrl: './checkmate-edit-form.component.html',
})
export class CheckmateEditFormComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  @Input()
  myFormArray: FormArray;

  oldDataCaptureType: string;

  constructor(public optionsService: OptionsService) {}

  ngOnInit() {
    this.oldDataCaptureType = this.myFormGroup.get('dataCaptureType').value;

    // Reset data on a change.
    this.myFormGroup
      .get('dataCaptureType')
      .valueChanges.subscribe((newValue) => {
        if (newValue !== this.oldDataCaptureType) {
          this.myFormGroup['checkmateDataListRemove']();
          this.oldDataCaptureType = newValue;
        }
      });
  }

  get myFormArrayName(): string {
    // determine the name given to the provided FormArray so that it can be bound to the root div template using the
    // FormArrayName directive (there is no FormArrayDirective)
    return Object.keys(this.myFormGroup.controls).find(
      (name) => this.myFormGroup.get(name) === this.myFormArray
    );
  }

  public addItem() {
    // call addItem method added to FormArray instance by the FormService. See form.service.ts
    // Set the defaults
    const newEntity = new CheckmateDataEntity();

    if (!this.myFormArray) {
      this.myFormGroup['checkmateDataListAdd']([newEntity]);
      this.myFormArray = this.myFormGroup.get('checkmateDataList') as FormArray;
    } else {
      (this.myFormArray as any).addItem(newEntity);
    }
  }

  removeItem(i: number) {
    this.myFormArray.removeAt(i);
  }
}

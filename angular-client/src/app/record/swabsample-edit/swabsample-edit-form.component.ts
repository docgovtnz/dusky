import { Component, Input, OnInit } from '@angular/core';
import { SwabSampleEntity } from '../../domain/swabsample.entity';
import { OptionsService } from '../../common/options.service';
import { FormGroup, FormArray } from '@angular/forms';
import { SampleService } from '../../sample/sample.service';

@Component({
  selector: 'app-swabsample-edit-form',
  templateUrl: './swabsample-edit-form.component.html',
})
export class SwabSampleEditFormComponent implements OnInit {
  @Input()
  myFormArray: FormArray;
  @Input()
  _sampleNamePrefix: string;

  constructor(
    public optionsService: OptionsService,
    private sampleService: SampleService
  ) {}

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
    const added: FormGroup = (this.myFormArray as any).addItem(
      new SwabSampleEntity()
    );
    const existing: SwabSampleEntity[] = this.myFormArray.getRawValue();

    const existingSampleNames = existing.map((s) => s.sampleName);
    const nextSampleName = this.sampleService.nextSampleName(
      this.sampleNamePrefix,
      existingSampleNames
    );
    if (nextSampleName) {
      added.get('sampleName').setValue(nextSampleName);
    }

    // Get most recent person and apply to next (last element in array is setter, hence -2)
    const previousSampleTakenBy = existing
      .map((s) => s.sampleTakenBy)
      .slice(-2)[0];
    if (previousSampleTakenBy) {
added.get('sampleTakenBy').setValue(previousSampleTakenBy);
}
  }

  removeItem(i: number) {
    this.myFormArray.removeAt(i);
  }

  get sampleNamePrefix() {
    return this._sampleNamePrefix;
  }

  @Input()
  set sampleNamePrefix(value) {
    if (value) {
      // partition array into saved and unsaved
      // track the sample and the index
      const [saved, unsaved] = this.myFormArray.getRawValue().reduce(
        ([rSaved, rUnsaved], s, i) => {
          console.log(s._originalIndex);
          if (s._originalIndex !== null) {
            return [[...rSaved, { sample: s, index: i }], rUnsaved];
          } else {
            return [rSaved, [...rUnsaved, { sample: s, index: i }]];
          }
        },
        [[], []]
      );
      const existing = saved.map((s) => s.sample.sampleName);
      // iterate through each unsaved sample in order
      // determining the next sample name
      // based on the saved and previously encountered/processed unsaved samples
      for (const s of unsaved) {
        const sampleName = this.sampleService.nextSampleName(value, existing);
        if (sampleName) {
          this.myFormArray.controls[s.index]
            .get('sampleName')
            .setValue(sampleName);
          existing.push(sampleName);
        }
      }
    }
    this._sampleNamePrefix = value;
  }
}

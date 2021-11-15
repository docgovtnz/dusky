import { Component, Input, OnInit } from '@angular/core';
import { FormArray, FormGroup } from '@angular/forms';
import { OptionsService } from '../../common/options.service';
import { SampleService } from '../../sample/sample.service';
import { BloodSampleDetailEntity } from 'src/app/domain/bloodsampledetail.entity';
import { BloodSampleEntity } from 'src/app/domain/bloodsample.entity';

@Component({
  selector: 'app-bloodsampledetail-edit-form',
  templateUrl: './bloodsampledetail-edit-form.component.html',
})
export class BloodSampleDetailEditFormComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  @Input()
  myFormArray: FormArray;

  @Input()
  _sampleNamePrefix: string;

  get myFormArrayName(): string {
    // determine the name given to the provided FormArray so that it can be bound to the root div template using the
    // FormArrayName directive (there is no FormArrayDirective)
    return Object.keys(this.myFormGroup.controls).find(
      (name) => this.myFormGroup.get(name) === this.myFormArray
    );
  }

  constructor(
    public optionsService: OptionsService,
    private sampleService: SampleService
  ) {}

  ngOnInit() {
    if (!this.myFormArray) {
      this.myFormGroup['bloodSampleListAdd']();
      this.myFormArray = this.myFormGroup.get('bloodSampleList') as FormArray;
    }
  }

  addItem() {
    // call addItem method added to FormArray instance by the FormService. See form.service.ts
    const added: FormGroup = (this.myFormArray as any).addItem(
      new BloodSampleEntity()
    );
    const existing: BloodSampleEntity[] = this.myFormArray.getRawValue();

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

  isVolumeDisabled(i: number) {
    const bloodSample = this.myFormArray.at(i);
    const type = bloodSample.get('type').value;
    const container = bloodSample.get('container').value;
    const volumeInMl = bloodSample.get('volumeInMl');

    if (type === 'Whole blood' && container === 'Slide') {
      volumeInMl.setValue(null);
      return true;
    } else {
      return null;
    }
  }
}

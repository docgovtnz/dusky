import { Component, Input, OnInit } from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { FormGroup } from '@angular/forms';
import { SampleService } from '../sample.service';

@Component({
  selector: 'app-blooddetail-edit-form',
  templateUrl: './blooddetail-edit-form.component.html',
})
export class BloodDetailEditFormComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  constructor(
    public optionsService: OptionsService,
    private sampleService: SampleService
  ) {}

  ngOnInit() {}

  get getContainer(): string {
    return this.sampleService.bloodSampleContainer;
  }

  isVolumeDisabled() {
    const typeValue = this.myFormGroup.get('type').value;
    const containerValue = this.getContainer;
    const volumeInMl = this.myFormGroup.get('volumeInMl');

    if (typeValue === 'Whole blood' && containerValue === 'Slide') {
      volumeInMl.setValue(null);
      return true;
    } else {
      return null;
    }
  }
}

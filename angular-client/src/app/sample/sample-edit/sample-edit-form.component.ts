import { Component, Input, OnInit } from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { FormGroup } from '@angular/forms';
import { SampleService } from '../sample.service';

@Component({
  selector: 'app-sample-edit-form',
  templateUrl: './sample-edit-form.component.html',
})
export class SampleEditFormComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  constructor(
    public optionsService: OptionsService,
    private sampleService: SampleService
  ) {}

  ngOnInit() {
    this.getBloodSampleContainerValue();
  }

  selectContainerChange() {
    this.getBloodSampleContainerValue();
  }

  getBloodSampleContainerValue() {
    this.sampleService.bloodSampleContainer = this.myFormGroup.get(
      'container'
    ).value;
  }
}

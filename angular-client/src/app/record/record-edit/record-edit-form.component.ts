import { Component, Input, OnInit } from '@angular/core';
import { RecordEntity } from '../../domain/record.entity';
import { OptionsService } from '../../common/options.service';
import { FormGroup } from '@angular/forms';
import { RecordService } from '../record.service';

@Component({
  selector: 'app-record-edit-form',
  templateUrl: './record-edit-form.component.html',
})
export class RecordEditFormComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;
  // New form control approach still needs RecordEntity for editing Observers, TODO: remove this
  @Input()
  recordEntity: RecordEntity;

  constructor(
    public optionsService: OptionsService,
    public recordService: RecordService
  ) {}

  ngOnInit() {
    this.myFormGroup.controls.reason.valueChanges.subscribe((e) => {
      //console.log(e);
      this.recordService.eventReason = e;
    });
  }
}

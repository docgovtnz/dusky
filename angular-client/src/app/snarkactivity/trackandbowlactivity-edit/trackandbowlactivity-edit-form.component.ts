import { Component, Input, OnInit } from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-trackandbowlactivity-edit-form',
  templateUrl: './trackandbowlactivity-edit-form.component.html',
})
export class TrackAndBowlActivityEditFormComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  constructor(public optionsService: OptionsService) {}

  ngOnInit() {}
}

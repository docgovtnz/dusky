import { Component, Input, OnInit } from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { FormGroup } from '@angular/forms';
import { SettingService } from '../../setting/setting.service';
import { of, Observable, combineLatest } from 'rxjs';
import {
  flatMap,
  shareReplay,
  startWith,
  filter,
  map,
  tap,
} from 'rxjs/operators';

@Component({
  selector: 'app-bird-edit-form',
  templateUrl: './bird-edit-form.component.html',
})
export class BirdEditFormComponent implements OnInit {
  defaultCoefficient: Observable<number> = of(null);

  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  constructor(public optionsService: OptionsService) {}

  ngOnInit() {}
}

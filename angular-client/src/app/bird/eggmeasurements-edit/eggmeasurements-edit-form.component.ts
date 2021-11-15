import { Component, Input, OnInit } from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { FormGroup } from '@angular/forms';
import {
  filter,
  flatMap,
  map,
  shareReplay,
  startWith,
  tap,
} from 'rxjs/operators';
import { combineLatest, Observable, of } from 'rxjs';
import { SettingService } from '../../setting/setting.service';

@Component({
  selector: 'app-eggmeasurements-edit-form',
  templateUrl: './eggmeasurements-edit-form.component.html',
})
export class EggMeasurementsEditFormComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  @Input()
  idPrefix = '';
  defaultCoefficient: Observable<number> = of(null);
  constructor(
    public optionsService: OptionsService,
    private settingService: SettingService
  ) {}

  ngOnInit() {
    this.defaultCoefficient = this.settingService
      .get(['DEFAULT_FRESH_WEIGHT_COEFFICIENT'])
      .pipe(
        flatMap((r) => of(r['DEFAULT_FRESH_WEIGHT_COEFFICIENT'])),
        shareReplay()
      );

    this.myFormGroup.controls.calculatedFreshWeight.disable();

    combineLatest(
      this.myFormGroup.controls.eggWidth.valueChanges.pipe(
        startWith(this.myFormGroup.controls.eggWidth.value)
      ),
      this.myFormGroup.controls.eggLength.valueChanges.pipe(
        startWith(this.myFormGroup.controls.eggLength.value)
      ),
      this.myFormGroup.controls.fwCoefficientX104.valueChanges.pipe(
        startWith(this.myFormGroup.controls.fwCoefficientX104.value)
      ),
      this.defaultCoefficient
    )
      .pipe(
        // Reset on change in case the filter prevents setting a new value.
        tap(() =>
          this.myFormGroup.controls.calculatedFreshWeight.setValue(null)
        ),

        // We need width, length, and at least one coefficient.
        filter(
          ([eggWidth, eggLength, coefficient, defaultCoefficient]) =>
            eggWidth && eggLength && (coefficient || defaultCoefficient)
        ),

        // Choose the preferred coefficient.
        map(([eggWidth, eggLength, coefficient, defaultCoefficient]) => [
          eggWidth,
          eggLength,
          coefficient ? coefficient : defaultCoefficient,
        ])
      )

      .subscribe(([eggWidth, eggLength, coefficient]) => {
        // Calculate the value and present on the screen, narrowing the precision.
        const calculatedInitialWeight =
          (coefficient / 10000) * eggLength * eggWidth * eggWidth;
        this.myFormGroup.controls.calculatedFreshWeight.setValue(
          calculatedInitialWeight.toPrecision(4)
        );
      });
  }
}

/* eslint-disable @angular-eslint/component-selector */

import { Component, Input, Output, OnInit, EventEmitter } from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { FormGroup, FormControl, AbstractControl } from '@angular/forms';
import { merge, of } from 'rxjs';
import {
  map,
  switchMap,
  tap,
  filter,
  debounceTime,
  catchError,
} from 'rxjs/operators';
import { BirdService } from '../../bird/bird.service';

@Component({
  selector: '[app-checkmate-data-row]',
  templateUrl: './checkmate-data-row.component.html',
})
export class CheckmateDataRowComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;
  @Input()
  last: boolean;
  @Input()
  index: number;

  @Output() rowDeleted = new EventEmitter<number>();
  @Output() rowAdded = new EventEmitter<void>();

  currentBirdId = '';
  currentQuality = '';
  currentDuration: number = null;
  currentTime: Date = null;

  islandControl: FormControl;
  dateTimeControl: FormControl;

  constructor(
    public optionsService: OptionsService,
    private birdService: BirdService
  ) {}

  ngOnInit() {
    // Extract simpler references to the controls we are interested in.
    this.islandControl = this.myFormGroup.parent.parent.parent.controls[
      'island'
    ];
    this.dateTimeControl = this.myFormGroup.parent.parent.parent.controls[
      'dateTime'
    ];

    // Retrieve currently saved values.
    this.currentBirdId = this.myFormGroup.controls.birdId.value;

    this.currentTime = this.myFormGroup.controls.time.value;
    this.currentDuration = this.myFormGroup.controls.duration.value;
    this.currentQuality = this.myFormGroup.controls.quality.value;

    const ctrls = this.myFormGroup.controls;

    // Listen for changes to the female TX and island controls to derive the bird.
    merge(
      ctrls.femaleTx1.valueChanges,
      ctrls.femaleTx2.valueChanges,
      this.islandControl.valueChanges
    )
      .pipe(
        debounceTime(300),
        // Reset the bird to empty while calculating.
        tap(() => {
          this.currentBirdId = null;
        }),
        // Filter out values that we don't want to make calls for.
        filter(
          () =>
            ctrls.femaleTx1.value >= 2 &&
            ctrls.femaleTx2.value >= 2 &&
            this.islandControl.value
        ),
        switchMap(() => {
          // Calculate the channel.
          const channel =
            (ctrls.femaleTx1.value - 2) * 10 + ctrls.femaleTx2.value - 2;
          // Call the service.
          return this.birdService
            .findBirdByTransmitterID(
              this.islandControl.value,
              channel,
              'Female'
            )
            .pipe(catchError(() => of(null)));
        })
      )
      .subscribe((birdId) => {
        if (birdId) {
          this.currentBirdId = birdId.id;
          this.myFormGroup.get('birdId').setValue(birdId.id);
        } else {
          this.currentBirdId = null;
          this.myFormGroup.get('birdId').setValue(null);
        }
      });

    // Listen for changes on the time values and the dateTime of the record.
    merge(
      ctrls.time1.valueChanges,
      ctrls.time2.valueChanges,
      this.dateTimeControl.valueChanges
    )
      .pipe(
        tap(() => (this.currentTime = null)),
        filter(
          () =>
            ctrls.time1.value >= 2 &&
            ctrls.time2.value >= 2 &&
            this.dateTimeControl.value
        )
      )
      .subscribe(() => {
        this.currentTime = this.deriveTime(
          (ctrls.time1.value - 2) * 10 + (ctrls.time2.value - 2)
        );
      });

    this.processDataEntry(ctrls.quality1, ctrls.quality2, (quality) => {
      this.currentQuality = quality;
    });
    this.processDataEntry(ctrls.duration1, ctrls.duration2, (processValue) => {
      this.currentDuration = processValue;
    });
  }

  /**
   * Process the data entry, filter out bad results, and call a callback  to calculate the result.
   *
   * @param control1 The first part of the control.
   * @param control2 The second part of the control.
   * @param fn The function to populate the target data. Takes an argument with the derived numeric value.
   */
  processDataEntry(
    control1: AbstractControl,
    control2: AbstractControl,
    fn: any
  ) {
    merge(control1.valueChanges, control2.valueChanges)
      .pipe(
        tap(() => fn(null)),
        // Filter out bad values.
        filter(() => control1.value >= 2 && control2.value >= 2),
        // Map to the calculated value.
        map(() => (control1.value - 2) * 10 + control2.value - 2)
      )
      .subscribe(
        // Call the target function.
        (derivedValue) => {
          fn(derivedValue);
        }
      );
  }

  /**
   * Determines the display value for the time of mating.
   *
   * @param hoursAgo The number of hours prior to the record of the mating.
   */
  deriveTime(hoursAgo: number): Date {
    if (hoursAgo === 255) {
      return null;
    } else {
      const dt = this.dateTimeControl.value;
      if (dt) {
        const derivedTime = new Date(dt.getTime() - hoursAgo * 60 * 60 * 1000);
        return derivedTime;
      } else {
        return null;
      }
    }
  }

  addItem(): void {
    this.rowAdded.emit();
  }

  removeItem(index: number): void {
    this.rowDeleted.emit(index);
  }
}

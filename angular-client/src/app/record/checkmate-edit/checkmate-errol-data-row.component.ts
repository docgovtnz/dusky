/* eslint-disable @angular-eslint/component-selector */

import { merge, of } from 'rxjs';
import { switchMap, debounceTime, catchError } from 'rxjs/operators';

import { Component, Input, Output, OnInit, EventEmitter } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';

import { OptionsService } from '../../common/options.service';
import { BirdService } from '../../bird/bird.service';

@Component({
  selector: '[app-errol-checkmate-data-row]',
  templateUrl: './checkmate-errol-data-row.component.html',
})
export class CheckmateErrolDataRowComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;
  @Input()
  last: boolean;
  @Input()
  index: number;

  @Output() rowDeleted = new EventEmitter<number>();
  @Output() rowAdded = new EventEmitter<void>();

  islandControl: FormControl;
  dateTimeControl: FormControl;
  currentBirdId: string;

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
    this.currentBirdId = this.myFormGroup.get('birdId').value;

    const ctrls = this.myFormGroup.controls;

    merge(ctrls.femaleTx.valueChanges, this.islandControl.valueChanges)
      .pipe(
        debounceTime(300),
        switchMap(() => {
          // Reset the current bird ID so we know we are looking it up.
          this.currentBirdId = null;
          // Call the service.
          const islandValue = this.islandControl.value;
          const femaleTxValue = this.myFormGroup.get('femaleTx').value;
          if (islandValue && femaleTxValue) {
            return this.birdService
              .findBirdByTransmitterID(
                this.islandControl.value,
                this.myFormGroup.get('femaleTx').value,
                'Female'
              )
              .pipe(catchError(() => of(null)));
          } else {
            return of(null);
          }
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
  }

  addItem(): void {
    this.rowAdded.emit();
  }

  removeItem(index: number): void {
    this.rowDeleted.emit(index);
  }
}

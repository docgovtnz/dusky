import { Input } from '@angular/core';
import { Component, forwardRef, OnDestroy, OnInit } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { OptionsService } from '../options.service';
import { InputService } from '../input.service';
import { LocationSummaryDto } from '../location-summary-dto';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-location-name-id-select-control',
  templateUrl: './location-name-id-select-control.component.html',
  styleUrls: ['./location-name-id-select-control.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => LocationNameIdSelectControlComponent),
      multi: true,
    },
  ],
})
export class LocationNameIdSelectControlComponent
  implements OnInit, OnDestroy, ControlValueAccessor {
  private _rawSummaryList: LocationSummaryDto[];
  private _island: string;
  locationNameSelected: string;
  locationIDSelected: string;
  // we must set to an empty array to avoid 'You provided 'undefined' where a stream was expected.' error
  summaryList: LocationSummaryDto[] = [];

  @Input()
  disabled = false;

  @Input()
  autofocus = false;

  private clearInputListener: Subscription;

  // the method set in registerOnChange, it is just
  // a placeholder for a method that takes one parameter,
  // we use it to emit changes back to the form
  private propagateChange = (_: any) => {};

  constructor(
    private optionsService: OptionsService,
    private clearInputService: InputService
  ) {
    this.clearInputListener = clearInputService.clearInputRequest$.subscribe(
      () => {
        this.locationIDSelected = null;
        this.refreshLocationNameSelected();
      }
    );
  }

  ngOnInit() {
    this.optionsService
      .getLocationSummaries()
      .subscribe((summaryList: LocationSummaryDto[]) => {
        this._rawSummaryList = summaryList;
        this.refreshLocationNameSelected();
        this.refreshSummaryList();
      });
  }

  private refreshSummaryList() {
    if (this._rawSummaryList) {
      // only use locations associated with the island (if island is not null)
      this.summaryList = this._rawSummaryList.filter((ls) =>
        this._island ? RegExp(`${this._island}.*`).test(ls.island) : true
      );
    }
  }

  private refreshLocationNameSelected() {
    if (!this.locationIDSelected) {
      this.locationNameSelected = null;
    } else if (this._rawSummaryList) {
      // use the raw summary list as this has all the ids in it
      const ls = this._rawSummaryList.find(
        (i) => i.id === this.locationIDSelected
      );
      if (ls) {
        this.locationNameSelected = ls.locationName;
      }
    }
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {}

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  writeValue(value: any): void {
    this.locationIDSelected = value;
    this.refreshLocationNameSelected();
  }

  @Input()
  set island(island: string) {
    this._island = island;
    this.refreshSummaryList();
  }

  get island(): string {
    return this._island;
  }

  onValueChange(nextValue: any) {
    this.locationIDSelected = nextValue;
    this.propagateChange(nextValue);
  }

  ngOnDestroy() {
    this.clearInputListener.unsubscribe();
  }

  onInputBlur() {
    // if the summary list has been loaded
    // (meaning refreshLocationNameSelected() has hopefully been called for the initial value)
    if (this._rawSummaryList) {
      // if there is text in the input
      if (this.locationNameSelected) {
        const ls = this._rawSummaryList.find(
          (i) => i.locationName === this.locationNameSelected
        );
        if (ls) {
          this.locationIDSelected = ls.id;
          this.propagateChange(this.locationIDSelected);
        } else {
          // then set the text back to the name corresponding to the id
          this.refreshLocationNameSelected();
        }
      } else {
        this.locationIDSelected = null;
        this.propagateChange(null);
      }
    }
  }
}

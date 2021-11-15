import { Component, forwardRef, OnDestroy, OnInit, Input } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { Subscription } from 'rxjs';
import { OptionsService } from '../../common/options.service';
import { InputService } from '../../common/input.service';
import { BirdSummaryDto } from '../../common/bird-summary-dto';

@Component({
  selector: 'app-bird-name-id-select-control',
  templateUrl: './bird-name-id-select-control.component.html',
  styleUrls: ['./bird-name-id-select-control.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => BirdNameIdSelectControlComponent),
      multi: true,
    },
  ],
})
export class BirdNameIdSelectControlComponent
  implements OnInit, OnDestroy, ControlValueAccessor {
  birdNameSelected: string = null;
  birdIDSelected: string = null;
  // we must set to an empty array to avoid 'You provided 'undefined' where a stream was expected.' error
  summaryList: BirdSummaryDto[] = [];

  @Input()
  disabled = false;

  @Input()
  autofocus = false;

  private clearInputListener: Subscription;

  private refreshSubscription: Subscription = new Subscription();

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
        if (this.birdIDSelected) {
          // only emit a change if the bird id was actually changed
          // this is to prevent focus events from firing a null change event
          // before the initial value has been set
          this.birdIDSelected = null;
          this.propagateChange(null);
          this.refreshBirdNameSelected();
        }
      }
    );
  }

  ngOnInit() {
    this.optionsService
      .getBirdSummaries()
      .subscribe((summaryList: BirdSummaryDto[]) => {
        this.summaryList = summaryList;
        this.refreshBirdNameSelected();
      });
  }

  private refreshBirdNameSelected() {
    if (!this.birdIDSelected) {
      this.birdNameSelected = null;
    } else if (this.summaryList) {
      const bs = this.summaryList.find((i) => i.id === this.birdIDSelected);
      if (bs) {
        this.birdNameSelected = bs.birdName;
      } else {
        this.optionsService.resetBirdCache();
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
    this.birdIDSelected = value;
    this.refreshBirdNameSelected();
  }

  onValueChange(nextValue: any) {
    if (!(!nextValue && !this.birdIDSelected)) {
      // only emit a change if the bird id was actually changed
      // this is to prevent focus events from firing a null change event
      // before the initial value has been set
      this.birdIDSelected = nextValue;
      this.propagateChange(nextValue);
    }
  }

  ngOnDestroy() {
    this.clearInputListener.unsubscribe();
    this.refreshSubscription.unsubscribe();
  }

  onInputBlur() {
    // if the summary list has been loaded
    // (meaning refreshBirdNameSelected() has hopefully been called for the initial value)
    if (this.summaryList.length > 0) {
      // if there is text in the input
      if (this.birdNameSelected) {
        const bs = this.summaryList.find(
          (i) => i.birdName === this.birdNameSelected
        );
        if (bs) {
          if (bs.id !== this.birdIDSelected) {
            this.birdIDSelected = bs.id;
            this.propagateChange(this.birdIDSelected);
          }
        } else {
          // then set the text back to the name corresponding to the id
          this.refreshBirdNameSelected();
        }
      } else if (this.birdIDSelected) {
        // only emit a change if the bird id was actually changed
        // this is to prevent focus events from firing a null change event
        // before the initial value has been set
        this.birdIDSelected = null;
        this.propagateChange(null);
      }
    }
  }
}

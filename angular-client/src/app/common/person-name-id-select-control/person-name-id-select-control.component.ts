import { Component, forwardRef, OnDestroy, OnInit, Input } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { Subscription } from 'rxjs';
import { OptionsService } from '../options.service';
import { InputService } from '../input.service';
import { PersonSummaryDto } from '../person-summary-dto';

@Component({
  selector: 'app-person-name-id-select-control',
  templateUrl: './person-name-id-select-control.component.html',
  styleUrls: ['./person-name-id-select-control.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => PersonNameIdSelectControlComponent),
      multi: true,
    },
  ],
})
export class PersonNameIdSelectControlComponent
  implements OnInit, OnDestroy, ControlValueAccessor {
  personNameSelected: string;
  personIDSelected: string;
  // we must set to an empty array to avoid 'You provided 'undefined' where a stream was expected.' error
  summaryList: PersonSummaryDto[] = [];

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
        this.personIDSelected = null;
        this.refreshPersonNameSelected();
      }
    );
  }

  ngOnInit() {
    this.optionsService
      .getPersonSummaries()
      .subscribe((summaryList: PersonSummaryDto[]) => {
        this.summaryList = summaryList;
        this.refreshPersonNameSelected();
      });
  }

  private refreshPersonNameSelected() {
    if (!this.personIDSelected) {
      this.personNameSelected = null;
    } else if (this.summaryList) {
      const ps = this.summaryList.find((i) => i.id === this.personIDSelected);
      if (ps) {
        this.personNameSelected = ps.personName;
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
    this.personIDSelected = value;
    this.refreshPersonNameSelected();
  }

  onValueChange(nextValue: any) {
    this.personIDSelected = nextValue;
    this.propagateChange(nextValue);
  }

  ngOnDestroy() {
    this.clearInputListener.unsubscribe();
  }

  onInputBlur() {
    // if the summary list has been loaded
    // (meaning refreshLocationNameSelected() has hopefully been called for the initial value)
    if (this.summaryList.length > 0) {
      // if there is text in the input
      if (this.personNameSelected) {
        const ps = this.summaryList.find(
          (i) => i.personName === this.personNameSelected
        );
        if (ps) {
          this.personIDSelected = ps.id;
          this.propagateChange(this.personIDSelected);
        } else {
          // then set the text back to the name corresponding to the id
          this.refreshPersonNameSelected();
        }
      } else {
        this.personIDSelected = null;
        this.propagateChange(null);
      }
    }
  }
}

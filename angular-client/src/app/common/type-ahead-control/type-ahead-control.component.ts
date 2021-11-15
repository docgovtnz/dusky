import { Component, forwardRef, Input, OnDestroy, OnInit } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { OptionsService } from '../options.service';
import { InputService } from '../input.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-type-ahead-control',
  templateUrl: './type-ahead-control.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TypeAheadControlComponent),
      multi: true,
    },
  ],
})
export class TypeAheadControlComponent
  implements OnInit, OnDestroy, ControlValueAccessor {
  @Input()
  optionType: string;

  optionList: string[] = [];

  private _selectedOption: string;

  @Input()
  disabled = false;
  @Input()
  autofocus = false;

  get selectedOption(): string {
    return this._selectedOption;
  }

  set selectedOption(value: string) {
    // for some reason type ahead still fires a change event when a user clicks on the disabled input
    if (!this.disabled) {
      this._selectedOption = value;
      this.onValueChange(value);
    }
  }

  // the method set in registerOnChange, it is just
  // a placeholder for a method that takes one parameter,
  // we use it to emit changes back to the form
  private propagateChange = (_: any) => {};

  private clearInputListener: Subscription;

  constructor(
    private optionsService: OptionsService,
    private clearInputService: InputService
  ) {
    this.clearInputListener = clearInputService.clearInputRequest$.subscribe(
      (request) => (this.selectedOption = request)
    );
  }

  ngOnInit() {
    // get the list from the options service function
    this.optionsService.getOptions(this.optionType).subscribe((response) => {
      this.optionList = response;
    });
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {}

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  writeValue(obj: any): void {
    this._selectedOption = obj;
  }

  onValueChange(nextValue: any) {
    this.propagateChange(nextValue);
  }

  ngOnDestroy() {
    this.clearInputListener.unsubscribe();
  }
}

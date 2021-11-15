import { OptionsService } from '../../common/options.service';
import { Component, forwardRef, Input, OnDestroy, OnInit } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { InputService } from '../../common/input.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-multi-select-control',
  templateUrl: './multi-select-control.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => MultiSelectControlComponent),
      multi: true,
    },
  ],
})
export class MultiSelectControlComponent
  implements OnInit, OnDestroy, ControlValueAccessor {
  @Input()
  optionType: string;

  @Input()
  disabled = false;

  @Input()
  autofocus = false;

  optionList: string[] = [];

  private _selectedOptions: string[] = [];

  get selectedOptions(): string[] {
    return this._selectedOptions;
  }

  set selectedOptions(value: string[]) {
    this._selectedOptions = value;
    this.onValueChange(value);
  }

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
      () => (this.selectedOptions = [])
    );
  }

  ngOnInit() {
    // get the list from the options service function
    // remove any null or empty string values from the list as this is a valid multi select option
    this.optionsService
      .getOptions(this.optionType)
      .subscribe((response: string[]) => {
        this.optionList = response.filter((i) => i !== '' && i !== null);
      });
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {}

  setDisabledState(isDisabled: boolean): void {}

  writeValue(obj: any): void {
    this._selectedOptions = obj;
  }

  onValueChange(nextValue: any) {
    this.propagateChange(nextValue);
  }

  ngOnDestroy() {
    this.clearInputListener.unsubscribe();
  }

  onRemoveOptionAction(index: number) {
    this.selectedOptions.splice(index, 1);
    console.log(this.selectedOptions);
  }

  onSelectOption(option: string, target) {
    if (option !== 'XXX') {
      // if no array then add one
      if (!this.selectedOptions) {
        this.selectedOptions = [];
      }

      // if array does not already contain item then add it
      if (!this.selectedOptions.includes(option)) {
        this.selectedOptions.push(option);
        this.selectedOptions.sort();
      }

      // setting target value back to 'XXX' ready for selecting the next option
      target.value = 'XXX';
    }
  }
}

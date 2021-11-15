import {
  Component,
  forwardRef,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { OptionsService } from '../options.service';

@Component({
  selector: 'app-option-multi-select',
  templateUrl: './option-multi-select.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => OptionMultiSelectComponent),
      multi: true,
    },
  ],
})
export class OptionMultiSelectComponent
  implements OnInit, OnDestroy, ControlValueAccessor {
  @Input()
  optionType: string;

  optionList: string[] = [];

  private _selectedOption: string;
  _selectedOptions: string[] = [];

  @Input()
  disabled = false;

  constructor(private optionsService: OptionsService) {}

  ngOnInit() {
    this.optionsService.getOptions(this.optionType).subscribe((response) => {
      this.optionList = response;
    });
  }

  ngOnDestroy() {}

  get selectedOption(): string {
    return this._selectedOption;
  }
  set selectedOption(value: string) {
    if (!this.disabled) {
      if (!this._selectedOptions.includes(value)) {
        this._selectedOptions.push(value);
        //this.selectedOptionsChange.emit(this._selectedOptions);
      }
      this._selectedOption = null;
      this.onValueChange(this._selectedOptions);
    }
  }

  onRemoveOptionAction(index: number) {
    this._selectedOptions.splice(index, 1);
    //this.selectedOptionsChange.emit(this._selectedOptions);
  }

  get selectedOptions(): string[] {
    return this._selectedOptions;
  }

  set selectedOptions(value: string[]) {
    if (!this.disabled) {
      this._selectedOptions = value;
      this.onValueChange(value);
    }
  }

  // Value Accessor stuff
  private propagateChange = (_: any) => {};

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {}

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  writeValue(obj: any): void {
    if (!obj) {
obj = [];
}
    this._selectedOptions = obj;
  }

  onValueChange(nextValue: any) {
    this.propagateChange(nextValue);
  }
}

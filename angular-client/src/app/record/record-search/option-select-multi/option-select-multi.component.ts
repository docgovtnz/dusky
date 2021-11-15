import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { OptionsService } from '../../../common/options.service';

@Component({
  selector: 'app-option-select-multi',
  templateUrl: './option-select-multi.component.html',
})
export class OptionSelectMultiComponent implements OnInit {
  allOptions: string[] = [];
  _selectedOptions: string[] = [];
  txtEntered = '';

  @Output()
  selectedOptionsChange: EventEmitter<string[]> = new EventEmitter<string[]>();

  @Input()
  autofocus = false;

  @Input()
  disabled = false;

  @Input()
  optionType: string;

  @Input()
  set selectedOptions(selectedOptions: string[]) {
    if (selectedOptions) {
      if (typeof selectedOptions === 'string') {
        this._selectedOptions = [selectedOptions];
      } else {
        this._selectedOptions = selectedOptions;
      }
    } else {
      this._selectedOptions = [];
    }
  }
  get selectedOptions(): string[] {
    return this._selectedOptions;
  }

  constructor(private optionsService: OptionsService) {}

  ngOnInit() {
    // get the list from the options service function
    this.optionsService.getOptions(this.optionType).subscribe((response) => {
      this.allOptions = response;
    });
  }

  onEscape(event) {
    if (this.txtEntered) {
      if (!this._selectedOptions.includes(this.txtEntered)) {
        this._selectedOptions.push(this.txtEntered);
        this.selectedOptionsChange.emit(this._selectedOptions);
      }
      this.txtEntered = '';
    }
  }

  onEnter(event) {
    event.preventDefault();
    event.stopPropagation();
  }

  onSelect(event) {
    if (event.item) {
      if (!this._selectedOptions.includes(event.item)) {
        this._selectedOptions.push(event.item);
        this.selectedOptionsChange.emit(this._selectedOptions);
      }
      this.txtEntered = '';
    }
  }

  onRemoveOptionAction(index: number) {
    this._selectedOptions.splice(index, 1);
    this.selectedOptionsChange.emit(this._selectedOptions);
  }
}

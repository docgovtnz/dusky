import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-toggle-switch',
  styleUrls: ['./toggle-switch.component.scss'],
  templateUrl: './toggle-switch.component.html',
})
export class ToggleSwitchComponent implements OnInit {
  private _buttonNames: string[] = [];
  private _selectedName;

  @Output()
  selectedNameChange = new EventEmitter<string>();

  @Input()
  disabledNames: string[] = [];

  @Input()
  disabled = false;

  constructor() {}

  ngOnInit() {}

  get buttonNames(): string[] {
    return this._buttonNames;
  }

  @Input()
  set buttonNames(value: string[]) {
    this._buttonNames = value;
    if (value && value.length > 0) {
      this._selectedName = value[0];
    }
  }

  set selectedNameInternal(value) {
    this.selectedNameChange.next(value);
  }

  get selectedNameInternal() {
    return this._selectedName;
  }

  get selectedName() {
    return this._selectedName;
  }

  @Input()
  set selectedName(value) {
    this._selectedName = value;
    this.selectedNameChange.next(value);
  }

  isDisabled(btnName): boolean {
    return this.disabled || this.disabledNames.indexOf(btnName) > -1;
  }
}

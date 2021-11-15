import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

const ICON_NAMES = [
  { name: 'Hidden', icon: 'ban' },
  { name: 'Visible', icon: 'map-pin' },
  { name: 'Labelled', icon: 'list' },
];

@Component({
  selector: 'app-three-state-button',
  templateUrl: './three-state-button.component.html',
})
export class ThreeStateButtonComponent implements OnInit {
  private _state = 0;
  @Output()
  stateChange = new EventEmitter();

  private _labelText = '';

  constructor() {}

  ngOnInit() {}

  get state(): number {
    return this._state;
  }

  @Input()
  set state(value: number) {
    this._state = value;
  }

  get labelText(): string {
    return this._labelText;
  }

  @Input()
  set labelText(value: string) {
    this._labelText = value;
  }

  changeButtonState() {
    this._state++;
    if (this._state > 2) {
      this._state = 0;
    }
    this.stateChange.next(this._state);
  }

  getButtonIcon() {
    return ICON_NAMES[this._state]['icon'];
  }

  getButtonName() {
    return ICON_NAMES[this._state]['name'];
  }
}

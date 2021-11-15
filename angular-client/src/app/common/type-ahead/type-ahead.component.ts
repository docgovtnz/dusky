import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  OnDestroy,
} from '@angular/core';
import { OptionsService } from '../options.service';
import { Subscription } from 'rxjs';
import { InputService } from '../input.service';

@Component({
  selector: 'app-type-ahead',
  templateUrl: './type-ahead.component.html',
})
export class TypeAheadComponent implements OnInit, OnDestroy {
  optionList: string[] = [];
  private _selectedOption: string;

  @Input()
  optionType: string;

  @Input()
  disabled = false;

  @Input()
  autofocus = false;

  @Output()
  selectedOptionChange = new EventEmitter<string>();

  clearInputListener: Subscription;

  constructor(
    private optionsService: OptionsService,
    private clearInputService: InputService
  ) {
    this.clearInputListener = clearInputService.clearInputRequest$.subscribe(
      (request) => (this._selectedOption = request)
    );
  }

  ngOnInit() {
    // get the list from the options service function
    this.optionsService.getOptions(this.optionType).subscribe((response) => {
      this.optionList = response;
    });
  }

  get selectedOption(): string {
    return this._selectedOption;
  }

  @Input()
  set selectedOption(value: string) {
    // for some reason type ahead still fires a change event when a user clicks on the disabled input
    if (!this.disabled) {
      const changed = this._selectedOption !== value;
      this._selectedOption = value;
      if (changed) {
        this.selectedOptionChange.emit(this._selectedOption);
      }
    }
  }

  ngOnDestroy() {
    this.clearInputListener.unsubscribe();
  }
}

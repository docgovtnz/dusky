import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  OnDestroy,
} from '@angular/core';
import { OptionsService } from '../options.service';
import { PersonSummaryDto } from '../person-summary-dto';
import { InputService } from '../input.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-person-id-select',
  templateUrl: './person-id-select.component.html',
  styleUrls: ['./person-id-select.component.scss'],
})
export class PersonIdSelectComponent implements OnInit, OnDestroy {
  private _personID: string;
  personNameSelected: string;
  // we must set to an empty array to avoid 'You provided 'undefined' where a stream was expected.' error
  summaryList: PersonSummaryDto[] = [];

  @Input()
  disabled = false;

  @Input()
  autofocus = false;

  @Output()
  personIDChange = new EventEmitter();

  clearInputListener: Subscription;

  constructor(
    private optionsService: OptionsService,
    private clearInputService: InputService
  ) {
    this.clearInputListener = clearInputService.clearInputRequest$.subscribe(
      () => {
        this.personID = null;
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
    if (!this._personID) {
      this.personNameSelected = null;
    } else if (this.summaryList) {
      const personSelected = this.summaryList.find(
        (ps) => ps.id === this._personID
      );
      if (personSelected) {
        this.personNameSelected = personSelected.personName;
      }
    }
  }

  get personID(): string {
    return this._personID;
  }

  @Input()
  set personID(value: string) {
    this._personID = value;
    this.refreshPersonNameSelected();
  }

  onTypeaheadOnSelect($event) {
    this._personID = $event.item.id;
    // we've got the user's new selection so fire an event with the next value
    this.personIDChange.next($event.item.id);
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
          this._personID = ps.id;
          this.personIDChange.next(this._personID);
        } else {
          // then set the text back to the name corresponding to the id
          this.refreshPersonNameSelected();
        }
      } else {
        this._personID = null;
        this.personIDChange.next(null);
      }
    }
  }
}

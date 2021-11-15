import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { OptionsService } from '../options.service';
import { LocationSummaryDto } from '../location-summary-dto';
import { InputService } from '../input.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-location-name-id-select',
  templateUrl: './location-name-id-select.component.html',
  styleUrls: ['./location-name-id-select.component.scss'],
})
export class LocationNameIdSelectComponent implements OnInit, OnDestroy {
  private _locationID;
  private _rawSummaryList: LocationSummaryDto[];
  private _island: string;
  locationNameSelected: string;
  // we must set to an empty array to avoid 'You provided 'undefined' where a stream was expected.' error
  summaryList: LocationSummaryDto[] = [];

  @Input()
  disabled = false;

  @Input()
  autofocus = false;

  @Output()
  locationIDChange = new EventEmitter();

  clearInputListener: Subscription;

  constructor(
    private optionsService: OptionsService,
    private clearInputService: InputService
  ) {
    this.clearInputListener = clearInputService.clearInputRequest$.subscribe(
      () => {
        this.locationID = null;
      }
    );
  }

  ngOnInit() {
    this.optionsService
      .getLocationSummaries()
      .subscribe((summaryList: LocationSummaryDto[]) => {
        this._rawSummaryList = summaryList;
        this.refreshLocationNameSelected();
        this.refreshSummaryList();
      });
  }

  private refreshSummaryList() {
    if (this._rawSummaryList) {
      // only use locations associated with the island (if island is not null)
      this.summaryList = this._rawSummaryList.filter((ls) =>
        this._island ? RegExp(`${this._island}.*`).test(ls.island) : true
      );
    }
  }

  private refreshLocationNameSelected() {
    if (!this._locationID) {
      this.locationNameSelected = null;
    } else if (this._rawSummaryList) {
      // use the raw summary list as this has all the ids in it
      const ls = this._rawSummaryList.find((i) => i.id === this._locationID);
      if (ls) {
        this.locationNameSelected = ls.locationName;
      }
    }
  }

  get locationID() {
    return this._locationID;
  }

  @Input()
  set locationID(value) {
    this._locationID = value;
    this.refreshLocationNameSelected();
  }

  @Input()
  set island(island: string) {
    this._island = island;
    this.refreshSummaryList();
  }

  get island(): string {
    return this._island;
  }

  onTypeaheadOnSelect($event) {
    this._locationID = $event.item.id;
    // we've got the user's new selection so fire an event with the next value
    this.locationIDChange.next($event.item.id);
  }

  ngOnDestroy() {
    this.clearInputListener.unsubscribe();
  }

  onInputBlur() {
    // if the summary list has been loaded
    // (meaning refreshLocationNameSelected() has hopefully been called for the initial value)
    if (this._rawSummaryList) {
      // if there is text in the input
      if (this.locationNameSelected) {
        const ls = this._rawSummaryList.find(
          (i) => i.locationName === this.locationNameSelected
        );
        if (ls) {
          this._locationID = ls.id;
          this.locationIDChange.next(this._locationID);
        } else {
          // then set the text back to the name corresponding to the id
          this.refreshLocationNameSelected();
        }
      } else {
        this._locationID = null;
        this.locationIDChange.next(null);
      }
    }
  }
}

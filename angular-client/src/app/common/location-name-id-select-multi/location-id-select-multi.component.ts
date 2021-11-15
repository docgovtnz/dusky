import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { OptionsService } from '../options.service';
import { LocationSummaryDto } from '../location-summary-dto';
import { Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';

@Component({
  selector: 'app-location-id-select-multi',
  templateUrl: './location-id-select-multi.component.html',
})
export class LocationIdSelectMultiComponent implements OnInit {
  summaryList: LocationSummaryDto[] = [];

  locationNameSelected: string = null;

  _selectedLocations: string[] = [];

  selectionSubject: Subject<string> = new Subject();

  @Input()
  autofocus = false;

  @Input()
  disabled = false;

  @Output()
  selectedLocationsChange: EventEmitter<string[]> = new EventEmitter<
    string[]
  >();

  constructor(private optionsService: OptionsService) {}

  ngOnInit() {
    this.optionsService
      .getLocationSummaries()
      .subscribe((summaryList: LocationSummaryDto[]) => {
        this.summaryList = summaryList;
      });

    this.selectionSubject.pipe(debounceTime(100)).subscribe((id) => {
      this._selectedLocations.push(id);
      this.selectedLocationsChange.emit(this._selectedLocations);
      this.locationNameSelected = null;
    });
  }

  @Input()
  get selectedLocations() {
    return this._selectedLocations;
  }

  set selectedLocations(selectedLocations: string[]) {
    if (selectedLocations) {
      this._selectedLocations = selectedLocations;
    } else {
      this._selectedLocations = [];
    }
  }

  onEnter(event) {
    event.preventDefault();
    event.stopPropagation();
  }

  onSelect(event) {
    if (event.item) {
      if (!this._selectedLocations.includes(event.item.id)) {
        this.selectionSubject.next(event.item.id);
      }
    }
  }

  onBlur() {
    const el = this.summaryList.find(
      (locationSummary) =>
        locationSummary.locationName.toLowerCase() === this.locationNameSelected
    );
    if (el && !this._selectedLocations.includes(el.id)) {
      this.selectionSubject.next(el.id);
    }
  }

  getLocationName(id: string): string {
    return this.summaryList.find((summary) => summary.id === id).locationName;
  }

  onRemoveOptionAction(index: number) {
    this._selectedLocations.splice(index, 1);
    this.selectedLocationsChange.emit(this._selectedLocations);
  }
}

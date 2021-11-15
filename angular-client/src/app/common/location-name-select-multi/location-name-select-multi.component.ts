import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { OptionsService } from '../options.service';

@Component({
  selector: 'app-location-name-select-multi',
  templateUrl: './location-name-select-multi.component.html',
})
export class LocationNameSelectMultiComponent implements OnInit {
  allLocationNames: string[] = [];

  _selectedLocations: string[] = [];

  @Output()
  selectedLocationsChange: EventEmitter<string[]> = new EventEmitter<
    string[]
  >();

  locationNameSelected: string = null;

  @Input()
  autofocus = false;

  @Input()
  disabled = false;

  @Input()
  set selectedLocations(selectedLocations: string[]) {
    if (selectedLocations) {
      if (typeof selectedLocations === 'string') {
        this._selectedLocations = [selectedLocations];
      } else {
        this._selectedLocations = selectedLocations;
      }
    } else {
      this._selectedLocations = [];
    }
  }

  constructor(private optionsService: OptionsService) {}

  ngOnInit() {
    // get the list from the options service function
    this.optionsService
      .getLocationNames()
      .subscribe((locationNames: string[]) => {
        this.allLocationNames = locationNames;
      });
  }

  onEscape() {
    if (this.locationNameSelected) {
      if (!this._selectedLocations.includes(this.locationNameSelected)) {
        this._selectedLocations.push(this.locationNameSelected);
        this.selectedLocationsChange.emit(this._selectedLocations);
      }
      this.locationNameSelected = null;
    }
  }

  onEnter(event) {
    event.preventDefault();
    event.stopPropagation();
  }

  onSelect(event) {
    if (event.item) {
      if (!this._selectedLocations.includes(event.item)) {
        this._selectedLocations.push(event.item);
        this.selectedLocationsChange.emit(this._selectedLocations);
      }
      this.locationNameSelected = null;
    }
  }

  onRemoveOptionAction(index: number) {
    this._selectedLocations.splice(index, 1);
    this.selectedLocationsChange.emit(this._selectedLocations);
  }
}

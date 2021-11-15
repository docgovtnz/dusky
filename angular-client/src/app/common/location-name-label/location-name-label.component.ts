import { Component, Input, OnInit } from '@angular/core';
import { LocationService } from '../../location/location.service';
import { map } from 'rxjs/operators';
import { LocationSummaryDto } from '../location-summary-dto';
import { OptionsService } from '../options.service';

@Component({
  selector: 'app-location-name-label',
  templateUrl: './location-name-label.component.html',
})
export class LocationNameLabelComponent implements OnInit {
  private _locationID: string;
  locationSummary: LocationSummaryDto;

  @Input()
  link = true;

  constructor(
    private locationService: LocationService,
    private optionsService: OptionsService
  ) {}

  ngOnInit() {}

  @Input()
  set locationID(value: string) {
    this._locationID = value;
    if (value !== null) {
      if (value !== null) {
        //this.birdService.findById(value).subscribe( (bird: BirdEntity) => this.birdEntity = bird );
        this.optionsService
          .getLocationSummaries()
          .pipe(
            map((dtoArray: LocationSummaryDto[]) =>
              dtoArray.find((dto) => dto.id === this._locationID)
            )
          )
          .subscribe((dto) => (this.locationSummary = dto));
      }
      //this.locationService.findById(value).subscribe( (location: LocationEntity) => this.locationEntity = location );
    } else {
      this.locationSummary = null;
    }
  }
}

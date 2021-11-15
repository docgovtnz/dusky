import { Component, OnInit } from '@angular/core';
import { LocationEntity } from '../../domain/location.entity';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { LocationCriteria } from '../../location/location.criteria';
import { LocationService } from '../../location/location.service';

@Component({
  selector: 'app-location-map',
  templateUrl: './location-map.component.html',
})
export class LocationMapComponent implements OnInit {
  locationCriteria: LocationCriteria;
  locationSearchResults: LocationEntity[];
  locationTypeSet: string[];
  firstIslandSearchResult: string;

  routeSubscription: Subscription;
  searchSubscription: Subscription = new Subscription();

  constructor(
    private service: LocationService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.routeSubscription = this.route.queryParams.subscribe((params) => {
      // run if the user refreshed the page or there was a call to route.navigate (not location.go())
      this.locationCriteria = new LocationCriteria();
      // set defaults before populating from params
      this.locationCriteria = this.service.setDefaults(this.locationCriteria);

      this.locationCriteria.populateFromParams(params);

      // Override the Page size for mapping because we want more data points
      this.locationCriteria.pageSize = 9999;

      this.doSearch();
    });
  }

  doSearch() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    this.searchSubscription = this.service
      .search(this.locationCriteria)
      .subscribe((response) => {
        this.locationSearchResults = response.results;
        this.locationTypeSet = this.getLocationTypeSet(
          this.locationSearchResults
        );

        console.log(
          'LocationMapComponent: locationTypeSet = ' +
            JSON.stringify(this.locationTypeSet)
        );

        if (this.locationSearchResults.length > 0) {
          this.firstIslandSearchResult = this.locationSearchResults[0].island;
        }
      });
  }

  getLocationTypeSet(locations: LocationEntity[]): string[] {
    const locationTypeSet = new Set<string>();

    if (locations) {
      locations.forEach((location) => {
        locationTypeSet.add(location.locationType);
      });
    }

    return Array.from(locationTypeSet.values());
  }
}

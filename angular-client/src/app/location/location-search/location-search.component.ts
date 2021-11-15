import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute, NavigationExtras } from '@angular/router';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs/index';

import { AbstractSearchComponent } from '../../common/abstract-search-component';
import { PagedResponse } from '../../domain/response/paged-response';
import { OptionsService } from '../../common/options.service';
import { InputService } from '../../common/input.service';
import { ExportService } from '../../common/export.service';
import { LocationService } from '../location.service';
import { LocationCriteria } from '../location.criteria';
import { SearchCacheService } from '../../common/searchcache.service';

@Component({
  selector: 'app-location-search',
  templateUrl: 'location-search.component.html',
})
export class LocationSearchComponent
  extends AbstractSearchComponent
  implements OnInit, OnDestroy {
  locationCriteria: LocationCriteria;
  lastLocationCriteria: LocationCriteria;
  pagedResponse: PagedResponse;
  routeSubscription: Subscription;
  searchSubscription: Subscription = new Subscription();

  constructor(
    private router: Router,
    private service: LocationService,
    public optionsService: OptionsService,
    private clearInputService: InputService,
    private exportService: ExportService,
    private route: ActivatedRoute,
    location: Location,
    private cacheService: SearchCacheService
  ) {
    super(location);
  }

  ngOnInit(): void {
    this.routeSubscription = this.route.queryParams.subscribe((params) => {
      // run if the user refreshed the page or there was a call to route.navigate (not location.go())
      this.locationCriteria = new LocationCriteria();
      // set defaults before populating from params
      this.locationCriteria = this.service.setDefaults(this.locationCriteria);
      this.locationCriteria.populateFromParams(params);
      // only do the search if the user clicks on search or navigates back to a previous search
      // don't do a search if the user just clicks on the nav bar item
      if (Object.keys(params).length !== 0) {
        this.doSearchWithCache();
      } else {
        // user clicked on the navbar item so
        if (this.service.locationCriteria) {
          // there is a previous criteria we can restore
          this.locationCriteria = this.service.locationCriteria;
          this.doSearchWithCache();
        } else {
          // clear the previous results as they won't match the criteria anymore
          this.pagedResponse = null;
        }
      }
    });
  }

  ngOnDestroy(): void {
    this.routeSubscription.unsubscribe();
    // cancel any search in progress so the url doesn't change if we have already navigated away
    this.searchSubscription.unsubscribe();
  }

  onPageChanged(event: any) {
    if (event.page) {
      this.locationCriteria.pageNumber = event.page;
      this.doSearchWithCache();
    }
  }

  onSearch() {
    // ensure we search for the first page of the results
    this.locationCriteria.pageNumber = 1;
    // clear the cached searches for the base url
    this.cacheService.clear('/location');
    this.doSearchWithCache();
    // save the criteria to the service so we can restore it if the user clicks on the navbar item
    this.service.locationCriteria = this.locationCriteria;
  }

  doSearchWithCache() {
    const cached = this.cacheService.get(
      this.locationCriteria.toUrl('/location')
    );
    if (cached) {
      // save a CLONE of the criteria
      const criteria = Object.assign(
        new LocationCriteria(),
        this.locationCriteria
      );
      this.pagedResponse = cached;
      // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
      this.lastLocationCriteria = criteria;
      // set the url to match to add to browser history (if required)
      this.goIfChanged('/location', criteria);
    } else {
      this.doSearch();
    }
  }

  doSearch() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    // save a CLONE of the criteria so we can use that once results come back (as this.locationCriteria may change before results come back)
    const criteria = Object.assign(
      new LocationCriteria(),
      this.locationCriteria
    );
    this.searchSubscription = this.service
      .search(this.locationCriteria)
      .subscribe((response) => {
        this.pagedResponse = response;
        // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
        this.lastLocationCriteria = criteria;
        // set the url to match to add to browser history (if required)
        this.goIfChanged('/location', criteria);
        this.cacheService.put(criteria.toUrl('/location'), response);
      });
  }

  onClear() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    this.locationCriteria = new LocationCriteria();
    this.clearInputService.clearInput(null);
  }

  onExport() {
    if (this.lastLocationCriteria) {
      // TODO this runs really slowly when proxied by the angular dev server as the proxy doesn't stream the response from the java spring boot backend
      window.location.href = this.exportService.getExportUrlPath(
        '/api/location/export',
        this.lastLocationCriteria
      );
    }
  }

  onMap() {
    const criteria = Object.assign(
      new LocationCriteria(),
      this.locationCriteria
    );
    console.log('onMap() ' + JSON.stringify(criteria.toParams()));

    const mapType = 'Location';

    const navigationExtras: NavigationExtras = {
      queryParams: criteria.toParams(),
    };

    this.router.navigate(['/map/' + mapType], navigationExtras);
  }

  onClick(item, $event) {
    super.navigate(this.router, 'location/' + item.id, $event.ctrlKey);
  }

  onLocationSelectionChanged() {
    this.doSearch();
  }

  onLocationNameSelectionChanged() {
    this.doSearch();
  }
}

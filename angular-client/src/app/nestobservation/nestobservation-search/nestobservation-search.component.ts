import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs/index';
import * as moment from 'moment';

import { AbstractSearchComponent } from '../../common/abstract-search-component';
import { PagedResponse } from '../../domain/response/paged-response';
import { OptionsService } from '../../common/options.service';
import { InputService } from '../../common/input.service';
import { ExportService } from '../../common/export.service';
import { NestObservationService } from '../nestobservation.service';
import { NestObservationCriteria } from '../nestobservation.criteria';
import { SearchCacheService } from '../../common/searchcache.service';

@Component({
  selector: 'app-nestobservation-search',
  templateUrl: 'nestobservation-search.component.html',
})
export class NestObservationSearchComponent
  extends AbstractSearchComponent
  implements OnInit, OnDestroy {
  nestobservationCriteria: NestObservationCriteria;
  lastNestObservationCriteria: NestObservationCriteria;
  pagedResponse: PagedResponse;
  routeSubscription: Subscription;
  searchSubscription: Subscription = new Subscription();

  constructor(
    private router: Router,
    private service: NestObservationService,
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
      this.nestobservationCriteria = new NestObservationCriteria();
      // set defaults before populating from params
      const fromDate = moment().startOf('day').subtract(1, 'years').toDate();
      this.nestobservationCriteria.fromDate = fromDate;
      this.nestobservationCriteria.populateFromParams(params);
      // only do the search if the user clicks on search or navigates back to a previous search
      // don't do a search if the user just clicks on the nav bar item
      if (Object.keys(params).length !== 0) {
        this.doSearchWithCache();
      } else {
        // user clicked on the navbar item so
        if (this.service.nestobservationCriteria) {
          // there is a previous criteria we can restore
          this.nestobservationCriteria = this.service.nestobservationCriteria;
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
      this.nestobservationCriteria.pageNumber = event.page;
      this.doSearchWithCache();
    }
  }

  onSearch() {
    // ensure we search for the first page of the results
    this.nestobservationCriteria.pageNumber = 1;
    // clear the cached searches for the base url
    this.cacheService.clear('/nestobservation');
    this.doSearchWithCache();
    // save the criteria to the service so we can restore it if the user clicks on the navbar item
    this.service.nestobservationCriteria = this.nestobservationCriteria;
  }

  doSearchWithCache() {
    const cached = this.cacheService.get(
      this.nestobservationCriteria.toUrl('/nestobservation')
    );
    if (cached) {
      // save a CLONE of the criteria
      const criteria = Object.assign(
        new NestObservationCriteria(),
        this.nestobservationCriteria
      );
      this.pagedResponse = cached;
      // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
      this.lastNestObservationCriteria = criteria;
      // set the url to match to add to browser history (if required)
      this.goIfChanged('/nestobservation', criteria);
    } else {
      this.doSearch();
    }
  }

  doSearch() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    // save a CLONE of the criteria so we can use that once results come back (as this.nestobservationCriteria may change before results come back)
    const criteria = Object.assign(
      new NestObservationCriteria(),
      this.nestobservationCriteria
    );
    this.searchSubscription = this.service
      .search(this.nestobservationCriteria)
      .subscribe((response) => {
        this.pagedResponse = response;
        console.log(response);
        // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
        this.lastNestObservationCriteria = criteria;
        // set the url to match to add to browser history (if required)
        this.goIfChanged('/nestobservation', criteria);
        this.cacheService.put(criteria.toUrl('/nestobservation'), response);
      });
  }

  onClear() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    this.nestobservationCriteria = new NestObservationCriteria();
    this.clearInputService.clearInput(null);
  }

  onExport() {
    if (this.lastNestObservationCriteria) {
      // TODO this runs really slowly when proxied by the angular dev server as the proxy doesn't stream the response from the java spring boot backend
      window.location.href = this.exportService.getExportUrlPath(
        '/api/nestobservation/export',
        this.lastNestObservationCriteria
      );
    }
  }

  onClick(item, $event) {
    super.navigate(this.router, 'nestobservation/' + item.id, $event.ctrlKey);
  }

  prevent($event): void {
    $event.preventDefault();
    $event.stopPropagation();
  }
}

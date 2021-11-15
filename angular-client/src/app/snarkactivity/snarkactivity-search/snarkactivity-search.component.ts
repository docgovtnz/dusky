import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs/index';

import { AbstractSearchComponent } from '../../common/abstract-search-component';
import { PagedResponse } from '../../domain/response/paged-response';
import { OptionsService } from '../../common/options.service';
import { InputService } from '../../common/input.service';
import { ExportService } from '../../common/export.service';
import { SnarkActivityService } from '../snarkactivity.service';
import { SnarkActivityCriteria } from '../snarkactivity.criteria';
import { SearchCacheService } from '../../common/searchcache.service';

@Component({
  selector: 'app-snarkactivity-search',
  templateUrl: 'snarkactivity-search.component.html',
})
export class SnarkActivitySearchComponent
  extends AbstractSearchComponent
  implements OnInit, OnDestroy {
  snarkactivityCriteria: SnarkActivityCriteria;
  lastSnarkActivityCriteria: SnarkActivityCriteria;
  pagedResponse: PagedResponse;
  routeSubscription: Subscription;
  searchSubscription: Subscription = new Subscription();

  constructor(
    private router: Router,
    private service: SnarkActivityService,
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
      this.snarkactivityCriteria = new SnarkActivityCriteria();
      // set defaults before populating from params
      this.snarkactivityCriteria.includeTrackAndBowl = true;
      this.snarkactivityCriteria.includeHopper = true;
      this.snarkactivityCriteria.includeNest = true;
      this.snarkactivityCriteria.includeRoost = true;
      this.snarkactivityCriteria.populateFromParams(params);
      // only do the search if the user clicks on search or navigates back to a previous search
      // don't do a search if the user just clicks on the nav bar item
      if (Object.keys(params).length !== 0) {
        this.doSearchWithCache();
      } else {
        // user clicked on the navbar item so
        if (this.service.snarkactivityCriteria) {
          // there is a previous criteria we can restore
          this.snarkactivityCriteria = this.service.snarkactivityCriteria;
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
      this.snarkactivityCriteria.pageNumber = event.page;
      this.doSearchWithCache();
    }
  }

  onSearch() {
    // ensure we search for the first page of the results
    this.snarkactivityCriteria.pageNumber = 1;
    // clear the cached searches for the base url
    this.cacheService.clear('/snarkactivity');
    this.doSearchWithCache();
    // save the criteria to the service so we can restore it if the user clicks on the navbar item
    this.service.snarkactivityCriteria = this.snarkactivityCriteria;
  }

  doSearchWithCache() {
    const cached = this.cacheService.get(
      this.snarkactivityCriteria.toUrl('/snarkactivity')
    );
    if (cached) {
      // save a CLONE of the criteria
      const criteria = Object.assign(
        new SnarkActivityCriteria(),
        this.snarkactivityCriteria
      );
      this.pagedResponse = cached;
      // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
      this.lastSnarkActivityCriteria = criteria;
      // set the url to match to add to browser history (if required)
      this.goIfChanged('/snarkactivity', criteria);
    } else {
      this.doSearch();
    }
  }

  doSearch() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    // save a CLONE of the criteria so we can use that once results come back (as this.snarkactivityCriteria may change before results come back)
    const criteria = Object.assign(
      new SnarkActivityCriteria(),
      this.snarkactivityCriteria
    );
    this.searchSubscription = this.service
      .search(this.snarkactivityCriteria)
      .subscribe((response) => {
        this.pagedResponse = response;
        // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
        this.lastSnarkActivityCriteria = criteria;
        // set the url to match to add to browser history (if required)
        this.goIfChanged('/snarkactivity', criteria);
        this.cacheService.put(criteria.toUrl('/snarkactivity'), response);
      });
  }

  onClear() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    this.snarkactivityCriteria = new SnarkActivityCriteria();
    this.clearInputService.clearInput(null);
  }

  onExport() {
    if (this.lastSnarkActivityCriteria) {
      // TODO this runs really slowly when proxied by the angular dev server as the proxy doesn't stream the response from the java spring boot backend
      window.location.href = this.exportService.getExportUrlPath(
        '/api/snarkactivity/export',
        this.lastSnarkActivityCriteria
      );
    }
  }

  onClick(item, $event) {
    super.navigate(
      this.router,
      'snarkactivity/' + item.snarkActivityId,
      $event.ctrlKey
    );
  }

  prevent($event): void {
    $event.preventDefault();
    $event.stopPropagation();
  }
}

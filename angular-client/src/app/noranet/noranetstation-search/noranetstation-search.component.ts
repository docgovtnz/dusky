import { Subscription } from 'rxjs/index';

import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';

import { AbstractSearchComponent } from '../../common/abstract-search-component';
import { PagedResponse } from '../../domain/response/paged-response';
import { OptionsService } from '../../common/options.service';
import { InputService } from '../../common/input.service';
import { ExportService } from '../../common/export.service';
import { NoraNetService } from '../noranet.service';
import { NoraNetStationCriteria } from '../noranetstation.criteria';
import { SearchCacheService } from '../../common/searchcache.service';
import { ValidationMessage } from '../../domain/response/validation-message';

@Component({
  selector: 'app-noranetstation-search',
  templateUrl: 'noranetstation-search.component.html',
})
export class NoraNetStationSearchComponent
  extends AbstractSearchComponent
  implements OnInit, OnDestroy {
  noranetStationCriteria: NoraNetStationCriteria;
  lastNoranetCriteria: NoraNetStationCriteria;
  pagedResponse: PagedResponse;
  routeSubscription: Subscription;
  searchSubscription: Subscription = new Subscription();
  messages: ValidationMessage[] = [];
  validationMessage: ValidationMessage;

  constructor(
    private router: Router,
    private service: NoraNetService,
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
      this.noranetStationCriteria = new NoraNetStationCriteria();
      this.noranetStationCriteria.island = 'Whenua Hou';
      this.noranetStationCriteria.populateFromParams(params);
      // only do the search if the user clicks on search or navigates back to a previous search
      // don't do a search if the user just clicks on the nav bar item
      if (Object.keys(params).length !== 0) {
        this.doSearchWithCache();
      } else {
        // user clicked on the navbar item so
        if (this.service.noranetStationCriteria) {
          // there is a previous criteria we can restore
          this.noranetStationCriteria = this.service.noranetStationCriteria;
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

  onNoraNetSelectionChanged() {
    this.onSearch();
  }

  onPageChanged(event: any) {
    if (event.page) {
      this.noranetStationCriteria.pageNumber = event.page;
      this.doSearchWithCache();
    }
  }

  onSearch() {
    // ensure we search for the first page of the results
    this.noranetStationCriteria.pageNumber = 1;
    // clear the cached searches for the base url
    this.cacheService.clear('/noranet');
    this.doSearchWithCache();
    // save the criteria to the service so we can restore it if the user clicks on the navbar item
    this.service.noranetStationCriteria = this.noranetStationCriteria;
  }

  doSearchWithCache() {
    const cached = this.cacheService.get(
      this.noranetStationCriteria.toUrl('/noranet')
    );
    if (cached) {
      // save a CLONE of the criteria
      const criteria = Object.assign(
        new NoraNetStationCriteria(),
        this.noranetStationCriteria
      );
      this.pagedResponse = cached;

      // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
      this.lastNoranetCriteria = criteria;
      // set the url to match to add to browser history (if required)
      this.goIfChanged('/noranet/station', criteria);
    } else {
      this.messages = [];
      if (
        this.noranetStationCriteria.island &&
        this.noranetStationCriteria.island !== ''
      ) {
        this.doSearch();
      } else {
        this.validationMessage = new ValidationMessage();
        this.validationMessage.messageText = 'Island is required.';
        this.messages.push(this.validationMessage);
      }
    }
  }

  doSearch() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    // save a CLONE of the criteria so we can use that once results come back (as this.noranetStationCriteria may change before results come back)
    const criteria = Object.assign(
      new NoraNetStationCriteria(),
      this.noranetStationCriteria
    );
    this.searchSubscription = this.service
      .findStationSearchDTOByCriteria(this.noranetStationCriteria)
      .subscribe((response) => {
        this.pagedResponse = response;
        // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
        this.lastNoranetCriteria = criteria;
        // set the url to match to add to browser history (if required)
        this.goIfChanged('/noranet/station', criteria);
        this.cacheService.put(criteria.toUrl('/noranet/station'), response);
      });
  }

  onBirdSelectionChanged() {
    this.onSearch();
  }

  onClear() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    this.noranetStationCriteria = new NoraNetStationCriteria();
    this.clearInputService.clearInput(null);
  }

  prevent($event) {
    $event.preventDefault();
    $event.stopPropagation();
  }

  onClick(item, $event) {
    const url = 'noranet/' + item.noraNetId;
    if ($event.ctrlKey) {
      window.open('app/' + url, '_blank');
    } else {
      this.router.navigate([url]);
    }
  }
}

import { forkJoin, Subscription } from 'rxjs/index';

import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';

import { AbstractSearchComponent } from '../../common/abstract-search-component';
import { PagedResponse } from '../../domain/response/paged-response';
import { OptionsService } from '../../common/options.service';
import { InputService } from '../../common/input.service';
import { ExportService } from '../../common/export.service';
import { NoraNetService } from '../noranet.service';
import { NoraNetCriteria } from '../noranet.criteria';
import { NoraNetDisplayCriteria } from '../noranet-display.criteria';
import { SearchCacheService } from '../../common/searchcache.service';
import { UndetectedBirdDTO } from '../undetected-bird.dto';
import { ValidationMessage } from '../../domain/response/validation-message';

@Component({
  selector: 'app-noranet-search',
  templateUrl: 'noranet-search.component.html',
})
export class NoraNetSearchComponent
  extends AbstractSearchComponent
  implements OnInit, OnDestroy {
  noranetCriteria: NoraNetCriteria;
  lastNoranetCriteria: NoraNetCriteria;
  noranetDisplayCriteria: NoraNetDisplayCriteria;
  detectedBirdsPagedResponse: PagedResponse;
  undetectedBirdsResponse: UndetectedBirdDTO[];
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
      this.noranetDisplayCriteria = new NoraNetDisplayCriteria();
      this.noranetCriteria = new NoraNetCriteria();
      // set defaults before populating from params
      this.noranetDisplayCriteria.display = 'All Birds';
      this.noranetCriteria.island = 'Whenua Hou';
      this.noranetCriteria.populateFromParams(params);
      // only do the search if the user clicks on search or navigates back to a previous search
      // don't do a search if the user just clicks on the nav bar item
      if (Object.keys(params).length !== 0) {
        this.doSearchWithCache();
      } else {
        // user clicked on the navbar item so
        if (this.service.noranetCriteria) {
          // there is a previous criteria we can restore
          this.noranetCriteria = this.service.noranetCriteria;
          this.doSearchWithCache();
        } else {
          // clear the previous results as they won't match the criteria anymore
          this.detectedBirdsPagedResponse = null;
          this.undetectedBirdsResponse = null;
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
      this.noranetCriteria.pageNumber = event.page;
      this.doSearchWithCache();
    }
  }

  onSearch() {
    // ensure we search for the first page of the results
    this.noranetCriteria.pageNumber = 1;
    // clear the cached searches for the base url
    this.cacheService.clear('/noranet');
    this.doSearchWithCache();
    // save the criteria to the service so we can restore it if the user clicks on the navbar item
    this.service.noranetCriteria = this.noranetCriteria;
  }

  doSearchWithCache() {
    const cached = this.cacheService.get(
      this.noranetCriteria.toUrl('/noranet')
    );
    if (cached) {
      // save a CLONE of the criteria
      const criteria = Object.assign(
        new NoraNetCriteria(),
        this.noranetCriteria
      );
      [this.detectedBirdsPagedResponse, this.undetectedBirdsResponse] = cached;

      // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
      this.lastNoranetCriteria = criteria;
      // set the url to match to add to browser history (if required)
      this.goIfChanged('/noranet', criteria);
    } else {
      this.messages = [];
      if (this.noranetCriteria.island && this.noranetCriteria.island !== '') {
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
    // save a CLONE of the criteria so we can use that once results come back (as this.noranetCriteria may change before results come back)
    const criteria = Object.assign(new NoraNetCriteria(), this.noranetCriteria);

    const detectedBirds = this.service.findSearchDTOByCriteria(
      this.noranetCriteria
    );
    const undetectedBirds = this.service.findUndetectedBirdsDTOByCriteria(
      this.noranetCriteria
    );

    this.searchSubscription = forkJoin([
      detectedBirds,
      undetectedBirds,
    ]).subscribe((response) => {
      [
        this.detectedBirdsPagedResponse,
        this.undetectedBirdsResponse,
      ] = response;

      // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
      this.lastNoranetCriteria = criteria;
      // set the url to match to add to browser history (if required)
      this.goIfChanged('/noranet', criteria);
      this.cacheService.put(criteria.toUrl('/noranet'), response);
    });
  }

  onBirdSelectionChanged() {
    this.onSearch();
  }

  onClear() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    this.noranetCriteria = new NoraNetCriteria();
    this.clearInputService.clearInput(null);
  }

  onExport() {
    if (this.lastNoranetCriteria) {
      // TODO this runs really slowly when proxied by the angular dev server as the proxy doesn't stream the response from the java spring boot backend
      window.location.href = this.exportService.getExportUrlPath(
        '/api/noranet/export',
        this.lastNoranetCriteria
      );
    }
  }

  prevent($event) {
    $event.preventDefault();
    $event.stopPropagation();
  }
}

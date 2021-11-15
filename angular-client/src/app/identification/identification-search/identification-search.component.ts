import { Subscription } from 'rxjs/index';

import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';

import * as moment from 'moment';

import { AbstractSearchComponent } from '../../common/abstract-search-component';
import { PagedResponse } from '../../domain/response/paged-response';
import { OptionsService } from '../../common/options.service';
import { InputService } from '../../common/input.service';
import { ExportService } from '../../common/export.service';
import { IdSearchCriteria } from './identification-search-criteria';
import { IdSearchService } from './identification-search.service';
import { SearchCacheService } from '../../common/searchcache.service';

@Component({
  selector: 'app-id-search',
  templateUrl: './identification-search.component.html',
})
export class IdSearchComponent
  extends AbstractSearchComponent
  implements OnInit, OnDestroy {
  idSearchCriteria: IdSearchCriteria;
  lastIdSearchCriteria: IdSearchCriteria;
  pagedResponse: PagedResponse;
  routeSubscription: Subscription;
  mortTypeList: string[];
  searchSubscription: Subscription = new Subscription();

  constructor(
    private router: Router,
    private service: IdSearchService,
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
      this.idSearchCriteria = new IdSearchCriteria();
      // set defaults before populating from params
      this.idSearchCriteria.aliveOnly = true;
      this.idSearchCriteria.latestOnly = true;
      this.idSearchCriteria.populateFromParams(params);
      // only do the search if the user clicks on search or navigates back to a previous search
      // don't do a search if the user just clicks on the nav bar item
      if (Object.keys(params).length !== 0) {
        this.doSearchWithCache();
      } else {
        // user clicked on the navbar item so
        if (this.service.idSearchCriteria) {
          // there is a previous criteria we can restore
          this.idSearchCriteria = this.service.idSearchCriteria;
          this.doSearchWithCache();
        } else {
          // clear the previous results as they won't match the criteria anymore
          this.pagedResponse = null;
        }
      }
    });

    this.optionsService.getTxMortalityTypes().subscribe((mortTypeList) => {
      this.mortTypeList = mortTypeList;
    });
  }

  ngOnDestroy(): void {
    this.routeSubscription.unsubscribe();
    // cancel any search in progress so the url doesn't change if we have already navigated away
    this.searchSubscription.unsubscribe();
  }

  onPageChanged(event: any) {
    if (event.page) {
      this.idSearchCriteria.pageNumber = event.page;
      this.doSearchWithCache();
    }
  }

  onSearch() {
    // ensure we search for the first page of the results
    this.idSearchCriteria.pageNumber = 1;
    // clear the cached searches for the base url
    this.cacheService.clear('/idsearch');
    this.doSearchWithCache();
    // save the criteria to the service so we can restore it if the user clicks on the navbar item
    this.service.idSearchCriteria = this.idSearchCriteria;
  }

  doSearchWithCache() {
    const cached = this.cacheService.get(
      this.idSearchCriteria.toUrl('/idsearch')
    );
    if (cached) {
      // save a CLONE of the criteria
      const criteria = Object.assign(
        new IdSearchCriteria(),
        this.idSearchCriteria
      );
      this.pagedResponse = cached;
      // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
      this.lastIdSearchCriteria = criteria;
      // set the url to match to add to browser history (if required)
      this.goIfChanged('/idsearch', criteria);
    } else {
      this.doSearch();
    }
  }

  doSearch() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    // save a CLONE of the criteria so we can use that once results come back (as this.birdCriteria may change before results come back)
    const criteria = Object.assign(
      new IdSearchCriteria(),
      this.idSearchCriteria
    );
    this.searchSubscription = this.service
      .search(this.idSearchCriteria)
      .subscribe((response) => {
        this.pagedResponse = response;
        // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
        this.lastIdSearchCriteria = criteria;
        // set the url to match to add to browser history (if required)
        this.goIfChanged('/idsearch', criteria);
        this.cacheService.put(criteria.toUrl('/idsearch'), response);
      });
  }

  onClear() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    this.idSearchCriteria = new IdSearchCriteria();
    this.idSearchCriteria.fromDate = moment().subtract(1, 'years').toDate();
    this.idSearchCriteria.toDate = moment().toDate();
    this.clearInputService.clearInput(null);
  }

  onExport() {
    if (this.lastIdSearchCriteria) {
      // TODO this runs really slowly when proxied by the angular dev server as the proxy doesn't stream the response from the java spring boot backend
      window.location.href = this.exportService.getExportUrlPath(
        '/api/id/export',
        this.lastIdSearchCriteria
      );
    }
  }

  onExportTransmitterList() {
    if (this.lastIdSearchCriteria) {
      // TODO this runs really slowly when proxied by the angular dev server as the proxy doesn't stream the response from the java spring boot backend
      window.location.href = this.exportService.getExportUrlPath(
        '/api/id/exportTransmitterList',
        this.lastIdSearchCriteria
      );
    }
  }

  onClick(item, idType: string) {
    if (item.birdName !== 'Unknown') {
      this.router.navigate(['bird/' + item.birdId + '/' + idType]);
    }
  }
}

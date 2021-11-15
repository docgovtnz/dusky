import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { Router, ActivatedRoute, NavigationExtras } from '@angular/router';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs/index';

import { AbstractSearchComponent } from '../../common/abstract-search-component';
import { PagedResponse } from '../../domain/response/paged-response';
import { OptionsService } from '../../common/options.service';
import { InputService } from '../../common/input.service';
import { ExportService } from '../../common/export.service';
import { RecordService } from '../record.service';
import { RecordCriteria } from '../record.criteria';
import { SearchCacheService } from '../../common/searchcache.service';
import { OptionSelectMultiComponent } from './option-select-multi/option-select-multi.component';

import * as moment from 'moment';
const WILDCARD = '%';

@Component({
  selector: 'app-record-search',
  templateUrl: 'record-search.component.html',
})
export class RecordSearchComponent
  extends AbstractSearchComponent
  implements OnInit, OnDestroy {
  @ViewChild('recordTypeBox') recordTypeBox: OptionSelectMultiComponent;
  @ViewChild('reasonBox') reasonBox: OptionSelectMultiComponent;
  recordCriteria: RecordCriteria;
  lastRecordCriteria: RecordCriteria;
  pagedResponse: PagedResponse;
  routeSubscription: Subscription;
  searchSubscription: Subscription = new Subscription();

  constructor(
    private router: Router,
    private service: RecordService,
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
      this.recordCriteria = new RecordCriteria();
      // set defaults before populating from params
      this.recordCriteria = this.service.setDefaults(this.recordCriteria);
      this.recordCriteria.fromDate = moment()
        .startOf('day')
        .subtract(3, 'years')
        .toDate();
      this.recordCriteria.populateFromParams(params);
      // only do the search if the user clicks on search or navigates back to a previous search
      // don't do a search if the user just clicks on the nav bar item
      if (Object.keys(params).length !== 0) {
        this.doSearchWithCache();
      } else {
        // user clicked on the navbar item so
        if (this.service.recordCriteria) {
          // there is a previous criteria we can restore
          this.recordCriteria = this.service.recordCriteria;
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
      this.recordCriteria.pageNumber = event.page;
      this.doSearchWithCache();
    }
  }

  onBirdSelectionChanged() {
    this.onSearch();
  }

  onSearch() {
    // ensure we search for the first page of the results
    this.recordCriteria.pageNumber = 1;
    // clear the cached searches for the base url
    this.cacheService.clear('/record');
    this.doSearchWithCache();
    // save the criteria to the service so we can restore it if the user clicks on the navbar item
    this.service.recordCriteria = this.recordCriteria;
  }

  doSearchWithCache() {
    const cached = this.cacheService.get(this.recordCriteria.toUrl('/record'));
    if (cached) {
      // save a CLONE of the criteria
      const criteria = Object.assign(new RecordCriteria(), this.recordCriteria);
      this.pagedResponse = cached;
      // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
      this.lastRecordCriteria = criteria;
      // set the url to match to add to browser history (if required)
      this.goIfChanged('/record', criteria);
    } else {
      this.doSearch();
    }
  }

  doSearch() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    // save a CLONE of the criteria so we can use that once results come back (as this.recordCriteria may change before results come back)
    const criteria = Object.assign(new RecordCriteria(), this.recordCriteria);
    //criteria may include wildcards
    let oldRecordTypes = [];
    if (criteria.recordTypes != null) {
oldRecordTypes = criteria.recordTypes.slice(0);
}
    let oldReasons = [];
    if (criteria.reasons != null) {
oldReasons = criteria.reasons.slice(0);
}
    criteria.recordTypes = oldRecordTypes.slice(0);
    criteria.reasons = oldReasons.slice(0);
    if (this.recordTypeBox && this.recordTypeBox.txtEntered.trim() !== '') {
      criteria.recordTypes.unshift(
        this.recordTypeBox.txtEntered.trim() + WILDCARD
      );
    }
    if (this.reasonBox && this.reasonBox.txtEntered.trim() !== '') {
      criteria.reasons.unshift(this.reasonBox.txtEntered.trim() + WILDCARD);
    }
    this.searchSubscription = this.service
      .findSearchDTOByCriteria(criteria)
      .subscribe((response) => {
        this.pagedResponse = response;
        // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
        criteria.recordTypes = oldRecordTypes;
        criteria.reasons = oldReasons;
        this.lastRecordCriteria = criteria;
        // set the url to match to add to browser history (if required)
        this.goIfChanged('/record', criteria);
        this.cacheService.put(criteria.toUrl('/record'), response);
      });
  }

  onClear() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    this.recordCriteria = new RecordCriteria();
    this.clearInputService.clearInput(null);
  }

  onExport() {
    if (this.lastRecordCriteria) {
      // TODO this runs really slowly when proxied by the angular dev server as the proxy doesn't stream the response from the java spring boot backend
      window.location.href = this.exportService.getExportUrlPath(
        '/api/record/export',
        this.lastRecordCriteria
      );
    }
  }

  onMap() {
    const criteria = Object.assign(new RecordCriteria(), this.recordCriteria);
    console.log('onMap() ' + JSON.stringify(criteria.toParams()));

    const mapType = 'Record';

    const navigationExtras: NavigationExtras = {
      queryParams: criteria.toParams(),
    };

    this.router.navigate(['/map/' + mapType], navigationExtras);
  }

  onClick(item, $event) {
    super.navigate(this.router, 'record/' + item.recordID, $event.ctrlKey);
  }

  prevent($event): void {
    $event.preventDefault();
    $event.stopPropagation();
  }
}

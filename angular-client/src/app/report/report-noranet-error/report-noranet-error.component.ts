import { Subscription } from 'rxjs/index';

import { Location } from '@angular/common';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import * as moment from 'moment';

import { AbstractSearchComponent } from '../../common/abstract-search-component';
import { OptionsService } from '../../common/options.service';
import { InputService } from '../../common/input.service';
import { ReportNoraNetErrorService } from './report-noranet-error.service';
import { ReportNoraNetErrorCriteria } from './report-noranet-error.criteria';
import { ReportNoraNetErrorDto } from './report-noranet-error-dto';
import { ValidationMessage } from '../../domain/response/validation-message';

@Component({
  selector: 'app-report-noranet-error',
  templateUrl: 'report-noranet-error.component.html',
})
export class ReportNoraNetErrorComponent
  extends AbstractSearchComponent
  implements OnInit, OnDestroy {
  noraneterrorCriteria: ReportNoraNetErrorCriteria = new ReportNoraNetErrorCriteria();
  lastReportErrorCriteria: ReportNoraNetErrorCriteria;
  routeSubscription: Subscription;
  searchSubscription: Subscription = new Subscription();
  report: ReportNoraNetErrorDto[];
  messages: ValidationMessage[] = [];

  constructor(
    private router: Router,
    private service: ReportNoraNetErrorService,
    public optionsService: OptionsService,
    private clearInputService: InputService,
    private route: ActivatedRoute,
    location: Location
  ) {
    super(location);
  }

  ngOnInit(): void {
    this.routeSubscription = this.route.queryParams.subscribe((params) => {
      // run if the user refreshed the page or there was a call to route.navigate (not location.go())
      this.noraneterrorCriteria = new ReportNoraNetErrorCriteria();
      // set defaults before populating from params
      this.noraneterrorCriteria.fromDateProcessed = moment()
        .add(-31, 'days')
        .toDate();
      this.noraneterrorCriteria.populateFromParams(params);
      this.doSearch();
    });
  }

  ngOnDestroy(): void {
    this.routeSubscription.unsubscribe();
    // cancel any search in progress so the url doesn't change if we have already navigated away
    this.searchSubscription.unsubscribe();
  }

  onPageChanged(event: any) {
    if (event.page) {
      this.noraneterrorCriteria.pageNumber = event.page;
      this.doSearch();
    }
  }

  onSearch() {
    // ensure we search for the first page of the results
    this.noraneterrorCriteria.pageNumber = 1;
    this.doSearch();
  }

  doSearch() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    // save a CLONE of the criteria so we can use that once results come back (as this.matingreportCriteria may change before results come back)
    const criteria = Object.assign(
      new ReportNoraNetErrorCriteria(),
      this.noraneterrorCriteria
    );
    this.messages = null;
    this.searchSubscription = this.service
      .search(this.noraneterrorCriteria)
      .subscribe((response) => {
        this.report = response.model;
        this.messages = response.messages;
        // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
        this.lastReportErrorCriteria = criteria;
        // set the url to match to add to browser history (if required)
        this.goIfChanged('/report/noraneterror/', criteria);
      });
  }

  onClear() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    this.noraneterrorCriteria = new ReportNoraNetErrorCriteria();
    this.noraneterrorCriteria.fromDateProcessed = moment()
      .add(-31, 'days')
      .toDate();
    this.clearInputService.clearInput(null);
  }

  set fromDateProcessed(value) {
    this.noraneterrorCriteria.fromDateProcessed = value;
  }

  get fromDateProcessed() {
    return this.noraneterrorCriteria.fromDateProcessed;
  }

  set toDateProcessed(value) {
    this.noraneterrorCriteria.toDateProcessed = value;
  }

  get toDateProcessed() {
    return this.noraneterrorCriteria.toDateProcessed;
  }
}

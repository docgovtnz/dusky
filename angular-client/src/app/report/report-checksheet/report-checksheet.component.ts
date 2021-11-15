import { Subscription } from 'rxjs/index';

import { Component, OnInit, OnDestroy } from '@angular/core';
import { Location } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';

import * as moment from 'moment';

import { AbstractSearchComponent } from '../../common/abstract-search-component';
import { OptionsService } from '../../common/options.service';
import { InputService } from '../../common/input.service';
import { ExportService } from '../../common/export.service';
import { ReportChecksheetService } from './report-checksheet.service';
import { ReportChecksheetCriteria } from './report-checksheet.criteria';
import { ReportChecksheet } from './report-checksheet';
import { ValidationMessage } from '../../domain/response/validation-message';

@Component({
  selector: 'app-report-checksheet',
  templateUrl: 'report-checksheet.component.html',
})
export class ReportChecksheetComponent
  extends AbstractSearchComponent
  implements OnInit, OnDestroy {
  checksheetreportCriteria: ReportChecksheetCriteria = new ReportChecksheetCriteria();
  lastReportChecksheetCriteria: ReportChecksheetCriteria;
  routeSubscription: Subscription;
  searchSubscription: Subscription = new Subscription();
  report: ReportChecksheet;
  messages: ValidationMessage[] = [];

  reportingPeriodOptions = [
    { value: 1, label: 'Day' },
    { value: 2, label: '2 days' },
    { value: 7, label: 'Week' },
    { value: 28, label: '4 weeks' },
  ];

  constructor(
    private router: Router,
    private service: ReportChecksheetService,
    public optionsService: OptionsService,
    private clearInputService: InputService,
    private exportService: ExportService,
    private route: ActivatedRoute,
    location: Location
  ) {
    super(location);
  }

  ngOnInit(): void {
    this.routeSubscription = this.route.queryParams.subscribe((params) => {
      // run if the user refreshed the page or there was a call to route.navigate (not location.go())
      this.checksheetreportCriteria = new ReportChecksheetCriteria();
      // set defaults before populating from params
      this.checksheetreportCriteria.toDate = moment().toDate();
      this.checksheetreportCriteria.populateFromParams(params);
      //this.doSearch();
    });
  }

  ngOnDestroy(): void {
    this.routeSubscription.unsubscribe();
    // cancel any search in progress so the url doesn't change if we have already navigated away
    this.searchSubscription.unsubscribe();
  }

  onPageChanged(event: any) {
    if (event.page) {
      this.checksheetreportCriteria.pageNumber = event.page;
      this.doSearch();
    }
  }

  onSearch() {
    // ensure we search for the first page of the results
    this.checksheetreportCriteria.pageNumber = 1;
    this.doSearch();
  }

  doSearch() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    // save a CLONE of the criteria so we can use that once results come back (as this.checksheetreportCriteria may change before results come back)
    const criteria = Object.assign(
      new ReportChecksheetCriteria(),
      this.checksheetreportCriteria
    );
    this.messages = null;
    this.searchSubscription = this.service
      .search(this.checksheetreportCriteria)
      .subscribe((response) => {
        this.report = response.model;
        this.messages = response.messages;
        // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
        this.lastReportChecksheetCriteria = criteria;
        // set the url to match to add to browser history (if required)
        this.goIfChanged('/report/checksheet/', criteria);
      });
  }

  onClear() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    this.checksheetreportCriteria = new ReportChecksheetCriteria();
    this.checksheetreportCriteria.toDate = moment().toDate();
    this.clearInputService.clearInput(null);
  }

  onExport() {
    if (this.lastReportChecksheetCriteria) {
      // TODO this runs really slowly when proxied by the angular dev server as the proxy doesn't stream the response from the java spring boot backend
      window.location.href = this.exportService.getExportUrlPath(
        '/api/report/checksheet/export',
        this.lastReportChecksheetCriteria
      );
    }
  }
}

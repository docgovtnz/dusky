import { Subscription } from 'rxjs/index';

import { Location } from '@angular/common';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import * as moment from 'moment';

import { AbstractSearchComponent } from '../../common/abstract-search-component';
import { OptionsService } from '../../common/options.service';
import { InputService } from '../../common/input.service';
import { ExportService } from '../../common/export.service';
import { ReportMatingService } from './report-mating.service';
import { ReportMatingCriteria } from './report-mating.criteria';
import { ReportMatingDTO } from './report-mating-dto';
import { ValidationMessage } from '../../domain/response/validation-message';

@Component({
  selector: 'app-report-mating',
  templateUrl: 'report-mating.component.html',
})
export class ReportMatingComponent
  extends AbstractSearchComponent
  implements OnInit, OnDestroy {
  seasonStartYear = -1; // previous year
  seasonStartMonth = 11; // November
  seasonStartDay = 1; // 1st

  matingreportCriteria: ReportMatingCriteria = new ReportMatingCriteria();
  lastReportMatingCriteria: ReportMatingCriteria;
  routeSubscription: Subscription;
  searchSubscription: Subscription = new Subscription();
  report: ReportMatingDTO[];
  messages: ValidationMessage[] = [];

  constructor(
    private router: Router,
    private service: ReportMatingService,
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
      this.matingreportCriteria = new ReportMatingCriteria();
      // set defaults before populating from params
      this.year = moment().year().toString();
      this.matingreportCriteria.populateFromParams(params);
      // TODO do we need this?
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
      this.matingreportCriteria.pageNumber = event.page;
      this.doSearch();
    }
  }

  onSearch() {
    // ensure we search for the first page of the results
    this.matingreportCriteria.pageNumber = 1;
    this.doSearch();
  }

  doSearch() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    // save a CLONE of the criteria so we can use that once results come back (as this.matingreportCriteria may change before results come back)
    const criteria = Object.assign(
      new ReportMatingCriteria(),
      this.matingreportCriteria
    );
    this.messages = null;
    this.searchSubscription = this.service
      .search(this.matingreportCriteria)
      .subscribe((response) => {
        this.report = response.model;
        this.messages = response.messages;
        // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
        this.lastReportMatingCriteria = criteria;
        // set the url to match to add to browser history (if required)
        this.goIfChanged('/report/mating/', criteria);
      });
  }

  onClear() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    this.matingreportCriteria = new ReportMatingCriteria();
    this.matingreportCriteria.toDate = moment().toDate();
    this.clearInputService.clearInput(null);
  }

  onExport() {
    if (this.lastReportMatingCriteria) {
      // TODO this runs really slowly when proxied by the angular dev server as the proxy doesn't stream the response from the java spring boot backend
      window.location.href = this.exportService.getExportUrlPath(
        '/api/report/mating/export',
        this.lastReportMatingCriteria
      );
    }
  }

  set year(value) {
    if (value) {
      const year = parseInt(value, 10);
      // moment months are 0-11
      const startMonth = this.seasonStartMonth - 1;
      const seasonStart = moment()
        .year(year + this.seasonStartYear)
        .month(startMonth)
        .date(this.seasonStartDay)
        .startOf('day');
      const seasonFinish = this.plusYearMinusDay(moment(seasonStart));
      if (seasonStart.isValid() && seasonFinish.isValid()) {
        this.matingreportCriteria.fromDate = seasonStart.toDate();
        this.matingreportCriteria.toDate = seasonFinish.toDate();
      }
    }
  }

  get year() {
    const season = this.getSeason(
      this.matingreportCriteria.fromDate,
      this.matingreportCriteria.toDate
    );
    if (season) {
      return season.toString();
    } else {
      return null;
    }
  }

  set fromDate(value) {
    this.matingreportCriteria.fromDate = value;
  }

  get fromDate() {
    return this.matingreportCriteria.fromDate;
  }

  set toDate(value) {
    this.matingreportCriteria.toDate = value;
  }

  get toDate() {
    return this.matingreportCriteria.toDate;
  }

  private getSeason(fromDate, toDate): number {
    const m = moment(fromDate);
    const day = m.date();
    const month = m.month();
    // moment months are 0-11
    const startMonth = this.seasonStartMonth - 1;
    if (
      day === this.seasonStartDay &&
      month === startMonth &&
      moment(toDate).isSame(this.plusYearMinusDay(moment(fromDate)))
    ) {
      return m.year() - this.seasonStartYear;
    }
    return null;
  }

  private plusYearMinusDay(value) {
    return value.add(1, 'years').subtract(1, 'days');
  }
}

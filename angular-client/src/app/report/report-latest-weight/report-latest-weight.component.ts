import { Subscription } from 'rxjs/index';

import { Location } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { AbstractSearchComponent } from '../../common/abstract-search-component';
import { ExportService } from '../../common/export.service';
import { OptionsService } from '../../common/options.service';
import { InputService } from '../../common/input.service';
import { BirdCriteria } from '../../bird/bird.criteria';
import { PagedResponse } from '../../domain/response/paged-response';
import { ReportLatestWeightService } from './report-latest-weight.service';

@Component({
  selector: 'app-report-latest-weight',
  templateUrl: 'report-latest-weight.component.html',
})
export class ReportLatestWeightComponent
  extends AbstractSearchComponent
  implements OnInit, OnDestroy {
  birdCriteria: BirdCriteria;
  routeSubscription: Subscription;
  searchSubscription: Subscription = new Subscription();
  pagedResponse: PagedResponse;
  messages: string[];

  constructor(
    private router: Router,
    private service: ReportLatestWeightService,
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
      this.birdCriteria = new BirdCriteria();
      this.birdCriteria.showAlive = true;

      // set defaults before populating from params
      this.birdCriteria.populateFromParams(params);

      this.doSearch();
    });
  }

  ngOnDestroy(): void {
    this.routeSubscription.unsubscribe();
    // cancel any search in progress so the url doesn't change if we have already navigated away
    this.searchSubscription.unsubscribe();
  }

  onBirdSelectionChanged() {
    this.onSearch();
  }

  onSearch(): void {
    // ensure we search for the first page of the results
    this.birdCriteria.pageNumber = 1;
    this.doSearch();
  }

  doSearch(): void {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    // save a CLONE of the criteria so we can use that once results come back (as this.matingreportCriteria may change before results come back)
    const criteria = Object.assign(new BirdCriteria(), this.birdCriteria);

    this.searchSubscription = this.service
      .search(this.birdCriteria)
      .subscribe((response) => {
        this.pagedResponse = response;

        // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
        this.birdCriteria = criteria;
        // set the url to match to add to browser history (if required)
        this.goIfChanged('/report/latestweight/', criteria);
      });
  }

  onClear(): void {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    this.birdCriteria = new BirdCriteria();
    this.birdCriteria.showAlive = true;
    this.clearInputService.clearInput(null);
  }

  onExport(): void {
    if (this.birdCriteria) {
      // TODO this runs really slowly when proxied by the angular dev server as the proxy doesn't stream the response from the java spring boot backend
      window.location.href = this.exportService.getExportUrlPath(
        '/api/report/latestweight/export',
        this.birdCriteria
      );
    }
  }

  onPageChanged($event) {
    if ($event.page) {
      this.birdCriteria.pageNumber = $event.page;
      this.doSearch();
    }
  }
}

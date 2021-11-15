import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs/index';

import { AbstractSearchComponent } from '../../common/abstract-search-component';
import { PagedResponse } from '../../domain/response/paged-response';
import { OptionsService } from '../../common/options.service';
import { InputService } from '../../common/input.service';
import { ExportService } from '../../common/export.service';
import { BirdService } from '../bird.service';
import { BirdCriteria } from '../bird.criteria';
import { BirdNameSelectMultiComponent } from '../bird-name-select-multi/bird-name-select-multi.component';
import { SearchCacheService } from '../../common/searchcache.service';

@Component({
  selector: 'app-bird-search',
  templateUrl: 'bird-search.component.html',
})
export class BirdSearchComponent
  extends AbstractSearchComponent
  implements OnInit, OnDestroy {
  birdCriteria: BirdCriteria;
  lastBirdCriteria: BirdCriteria;
  pagedResponse: PagedResponse;
  routeSubscription: Subscription;
  searchSubscription: Subscription = new Subscription();

  @ViewChild(BirdNameSelectMultiComponent)
  birdNameSelectMultiComponent: BirdNameSelectMultiComponent;

  constructor(
    private router: Router,
    private service: BirdService,
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
      this.birdCriteria = new BirdCriteria();
      // set defaults before populating from params
      this.birdCriteria.showAlive = true;
      this.birdCriteria.populateFromParams(params);
      // only do the search if the user clicks on search or navigates back to a previous search
      // don't do a search if the user just clicks on the nav bar item
      if (Object.keys(params).length !== 0) {
        this.doSearchWithCache();
      } else {
        // user clicked on the navbar item so
        if (this.service.birdCriteria) {
          // there is a previous criteria we can restore
          this.birdCriteria = this.service.birdCriteria;
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

  onBirdSelectionChanged() {
    this.onSearch();
  }

  onPageChanged(event: any) {
    if (event.page) {
      this.birdCriteria.pageNumber = event.page;
      this.doSearchWithCache();
    }
  }

  onSearch() {
    // ensure we search for the first page of the results
    this.birdCriteria.pageNumber = 1;
    // clear the cached searches for the base url
    this.cacheService.clear('/bird');
    this.doSearchWithCache();
    // save the criteria to the service so we can restore it if the user clicks on the navbar item
    this.service.birdCriteria = this.birdCriteria;
  }

  doSearchWithCache() {
    const cached = this.cacheService.get(this.birdCriteria.toUrl('/bird'));
    if (cached) {
      // save a CLONE of the criteria
      const criteria = Object.assign(new BirdCriteria(), this.birdCriteria);
      this.pagedResponse = cached;
      // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
      this.lastBirdCriteria = criteria;
      // set the url to match to add to browser history (if required)
      this.goIfChanged('/bird', criteria);
    } else {
      this.doSearch();
    }
  }

  doSearch() {
    if (
      this.birdNameSelectMultiComponent &&
      this.birdNameSelectMultiComponent.birdNameSelected
    ) {
      this.birdNameSelectMultiComponent.onEscape();
    }

    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    // save a CLONE of the criteria so we can use that once results come back (as this.birdCriteria may change before results come back)
    const criteria = Object.assign(new BirdCriteria(), this.birdCriteria);
    this.searchSubscription = this.service
      .search(this.birdCriteria)
      .subscribe((response) => {
        this.pagedResponse = response;
        // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
        this.lastBirdCriteria = criteria;
        // set the url to match to add to browser history (if required)
        this.goIfChanged('/bird', criteria);
        this.cacheService.put(criteria.toUrl('/bird'), response);
      });
  }

  onClear() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    this.birdCriteria = new BirdCriteria();
    this.clearInputService.clearInput(null);
  }

  onExport() {
    if (this.lastBirdCriteria) {
      // TODO this runs really slowly when proxied by the angular dev server as the proxy doesn't stream the response from the java spring boot backend
      window.location.href = this.exportService.getExportUrlPath(
        '/api/bird/export',
        this.lastBirdCriteria
      );
    }
  }

  onClick(item, $event) {
    super.navigate(this.router, 'bird/' + item.birdID, $event.ctrlKey);
  }

  birdWeightGraph($event) {
    if ($event.ctrlKey) {
      window.open(
        this.birdCriteria.toUrl('/app/bird/multiweightgraph'),
        '_blank'
      );
    } else {
      this.router.navigate(['bird/multiweightgraph'], {
        queryParams: this.birdCriteria.toParams(),
      });
    }
  }

  birdEggWeightGraph($event) {
    if ($event.ctrlKey) {
      window.open(
        this.birdCriteria.toUrl('/app/bird/multieggweightgraph'),
        '_blank'
      );
    } else {
      this.router.navigate(['bird/multieggweightgraph'], {
        queryParams: this.birdCriteria.toParams(),
      });
    }
  }

  prevent($event) {
    $event.preventDefault();
    $event.stopPropagation();
  }
}

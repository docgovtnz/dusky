import { Subscription } from 'rxjs/index';

import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';

import { AbstractSearchComponent } from '../../../common/abstract-search-component';
import { PagedResponse } from '../../../domain/response/paged-response';
import { OptionsService } from '../../../common/options.service';
import { InputService } from '../../../common/input.service';
import { ExportService } from '../../../common/export.service';
import { TxMortalityService } from '../txmortality.service';
import { TxMortalityCriteria } from '../txmortality.criteria';
import { SearchCacheService } from '../../../common/searchcache.service';
import { SettingService } from '../../../setting/setting.service';

@Component({
  selector: 'app-txmortality-search',
  templateUrl: 'txmortality-search.component.html',
})
export class TxMortalitySearchComponent
  extends AbstractSearchComponent
  implements OnInit, OnDestroy {
  txmortalityCriteria: TxMortalityCriteria;
  lastTxMortalityCriteria: TxMortalityCriteria;
  pagedResponse: PagedResponse;
  routeSubscription: Subscription;
  searchSubscription: Subscription = new Subscription();
  isAuthorized = false;

  constructor(
    private router: Router,
    private service: TxMortalityService,
    public optionsService: OptionsService,
    private clearInputService: InputService,
    private exportService: ExportService,
    private route: ActivatedRoute,
    location: Location,
    private cacheService: SearchCacheService,
    private settingService: SettingService
  ) {
    super(location);
  }

  ngOnInit(): void {
    this.routeSubscription = this.route.queryParams.subscribe((params) => {
      // run if the user refreshed the page or there was a call to route.navigate (not location.go())
      this.txmortalityCriteria = new TxMortalityCriteria();
      // set defaults before populating from params
      // no defaults
      this.txmortalityCriteria.populateFromParams(params);
      // only do the search if the user clicks on search or navigates back to a previous search
      // don't do a search if the user just clicks on the nav bar item
      if (Object.keys(params).length !== 0) {
        this.doSearchWithCache();
      } else {
        // user clicked on the navbar item so
        if (this.service.txmortalityCriteria) {
          // there is a previous criteria we can restore
          this.txmortalityCriteria = this.service.txmortalityCriteria;
          this.doSearchWithCache();
        } else {
          // clear the previous results as they won't match the criteria anymore
          this.pagedResponse = null;
        }
      }
      this.isAuthorized = this.settingService.isAuthorizedToAdd();
    });
  }

  ngOnDestroy(): void {
    this.routeSubscription.unsubscribe();
    // cancel any search in progress so the url doesn't change if we have already navigated away
    this.searchSubscription.unsubscribe();
  }

  onPageChanged(event: any) {
    if (event.page) {
      this.txmortalityCriteria.pageNumber = event.page;
      this.doSearchWithCache();
    }
  }

  onSearch() {
    // ensure we search for the first page of the results
    this.txmortalityCriteria.pageNumber = 1;
    // clear the cached searches for the base url
    this.cacheService.clear('/settings/txmortality');
    this.doSearchWithCache();
    // save the criteria to the service so we can restore it if the user clicks on the navbar item
    this.service.txmortalityCriteria = this.txmortalityCriteria;
  }

  doSearchWithCache() {
    const cached = this.cacheService.get(
      this.txmortalityCriteria.toUrl('/settings/txmortality')
    );
    if (cached) {
      // save a CLONE of the criteria
      const criteria = Object.assign(
        new TxMortalityCriteria(),
        this.txmortalityCriteria
      );
      this.pagedResponse = cached;
      // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
      this.lastTxMortalityCriteria = criteria;
      // set the url to match to add to browser history (if required)
      this.goIfChanged('/settings/txmortality', criteria);
    } else {
      this.doSearch();
    }
  }

  doSearch() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    // save a CLONE of the criteria so we can use that once results come back (as this.txmortalityCriteria may change before results come back)
    const criteria = Object.assign(
      new TxMortalityCriteria(),
      this.txmortalityCriteria
    );
    this.searchSubscription = this.service
      .search(this.txmortalityCriteria)
      .subscribe((response) => {
        this.pagedResponse = response;
        // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
        this.lastTxMortalityCriteria = criteria;
        // set the url to match to add to browser history (if required)
        this.goIfChanged('/settings/txmortality', criteria);
        this.cacheService.put(
          criteria.toUrl('/settings/txmortality'),
          response
        );
      });
  }

  onClear() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    this.txmortalityCriteria = new TxMortalityCriteria();
    this.clearInputService.clearInput(null);
  }

  onExport() {
    if (this.lastTxMortalityCriteria) {
      // TODO this runs really slowly when proxied by the angular dev server as the proxy doesn't stream the response from the java spring boot backend
      window.location.href = this.exportService.getExportUrlPath(
        '/api/txmortality/export',
        this.lastTxMortalityCriteria
      );
    }
  }

  onClick(item, $event) {
    super.navigate(
      this.router,
      'settings/txmortality/' + item.id,
      $event.ctrlKey
    );
  }

  onNew() {
    this.router.navigate(['settings/txmortality/edit/-1'], {
      queryParams: { newEntity: true },
    });
  }
}

import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs/index';

import { AbstractSearchComponent } from '../../common/abstract-search-component';
import { PagedResponse } from '../../domain/response/paged-response';
import { OptionsService } from '../../common/options.service';
import { InputService } from '../../common/input.service';
import { ExportService } from '../../common/export.service';
import { PersonService } from '../person.service';
import { PersonCriteria } from '../person.criteria';
import { SearchCacheService } from '../../common/searchcache.service';

@Component({
  selector: 'app-person-search',
  templateUrl: 'person-search.component.html',
})
export class PersonSearchComponent
  extends AbstractSearchComponent
  implements OnInit, OnDestroy {
  personCriteria: PersonCriteria;
  lastPersonCriteria: PersonCriteria;
  pagedResponse: PagedResponse;
  routeSubscription: Subscription;
  searchSubscription: Subscription = new Subscription();

  constructor(
    private router: Router,
    private service: PersonService,
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
      this.personCriteria = new PersonCriteria();
      this.personCriteria.populateFromParams(params);
      // only do the search if the user clicks on search or navigates back to a previous search
      // don't do a search if the user just clicks on the nav bar item
      if (Object.keys(params).length !== 0) {
        this.doSearchWithCache();
      } else {
        // user clicked on the navbar item so
        if (this.service.personCriteria) {
          // there is a previous criteria we can restore
          this.personCriteria = this.service.personCriteria;
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
      this.personCriteria.pageNumber = event.page;
      this.doSearchWithCache();
    }
  }

  onSearch() {
    // ensure we search for the first page of the results
    this.personCriteria.pageNumber = 1;
    // clear the cached searches for the base url
    this.cacheService.clear('/person');
    this.doSearchWithCache();
    // save the criteria to the service so we can restore it if the user clicks on the navbar item
    this.service.personCriteria = this.personCriteria;
  }

  doSearchWithCache() {
    const cached = this.cacheService.get(this.personCriteria.toUrl('/person'));
    if (cached) {
      // save a CLONE of the criteria
      const criteria = Object.assign(new PersonCriteria(), this.personCriteria);
      this.pagedResponse = cached;
      // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
      this.lastPersonCriteria = criteria;
      // set the url to match to add to browser history (if required)
      this.goIfChanged('/person', criteria);
    } else {
      this.doSearch();
    }
  }

  doSearch() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    // save a CLONE of the criteria so we can use that once results come back (as this.personCriteria may change before results come back)
    const criteria = Object.assign(new PersonCriteria(), this.personCriteria);
    this.searchSubscription = this.service
      .search(this.personCriteria)
      .subscribe((response) => {
        this.pagedResponse = response;
        // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
        this.lastPersonCriteria = criteria;
        // set the url to match to add to browser history (if required)
        this.goIfChanged('/person', criteria);
        this.cacheService.put(criteria.toUrl('/person'), response);
      });
  }

  onClear() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    this.personCriteria = new PersonCriteria();
    this.clearInputService.clearInput(null);
  }

  onExport() {
    if (this.lastPersonCriteria) {
      // TODO this runs really slowly when proxied by the angular dev server as the proxy doesn't stream the response from the java spring boot backend
      window.location.href = this.exportService.getExportUrlPath(
        '/api/person/export',
        this.lastPersonCriteria
      );
    }
  }

  onClick(item, $event) {
    super.navigate(this.router, 'person/' + item.id, $event.ctrlKey);
  }
}

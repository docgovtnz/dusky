import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs/index';
import * as moment from 'moment';
import { ModalDirective } from 'ngx-bootstrap/modal';

import { AbstractSearchComponent } from '../../common/abstract-search-component';
import { PagedResponse } from '../../domain/response/paged-response';
import { OptionsService } from '../../common/options.service';
import { InputService } from '../../common/input.service';
import { ExportService } from '../../common/export.service';
import { FeedOutService } from '../feedout.service';
import { FeedOutCriteria } from '../feedout.criteria';
import { FeedOutEntity } from '../../domain/feedout.entity';
import { ValidationMessage } from '../../domain/response/validation-message';
import { finalize } from 'rxjs/operators';
import { FoodTallyEntity } from 'src/app/domain/foodtally.entity';

@Component({
  selector: 'app-feedout-search',
  templateUrl: './feedout-search.component.html',
})
export class FeedOutSearchComponent
  extends AbstractSearchComponent
  implements OnInit, OnDestroy {
  feedoutCriteria: FeedOutCriteria;
  lastFeedOutCriteria: FeedOutCriteria;
  pagedResponse: PagedResponse;
  routeSubscription: Subscription;
  searchSubscription: Subscription = new Subscription();

  editMode = false;
  edited = {};
  saved = {};
  loading = {};
  deleted = {};
  added = [];
  messages = [];
  error = [];
  private _confirmDeleteIndex;

  @ViewChild('confirmDeleteModal', { static: true })
  public confirmDeleteModal: ModalDirective;

  constructor(
    private router: Router,
    private service: FeedOutService,
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
      this.feedoutCriteria = new FeedOutCriteria();
      // set defaults before populating from params
      this.feedoutCriteria.fromDate = moment()
        .startOf('day')
        .subtract(1, 'years')
        .toDate();
      this.feedoutCriteria.populateFromParams(params);
      // only do the search if the user clicks on search or navigates back to a previous search
      // don't do a search if the user just clicks on the nav bar item
      if (Object.keys(params).length !== 0) {
        this.doSearch();
      } else {
        // user clicked on the navbar item so clear the previous results as they won't match the criteria anymore
        this.pagedResponse = null;
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
      this.feedoutCriteria.pageNumber = event.page;
      this.doSearch();
    }
  }

  onSearch() {
    // ensure we search for the first page of the results
    this.feedoutCriteria.pageNumber = 1;
    this.doSearch();
  }

  doSearch() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    // save a CLONE of the criteria so we can use that once results come back (as this.feedoutCriteria may change before results come back)
    const criteria = Object.assign(new FeedOutCriteria(), this.feedoutCriteria);
    this.searchSubscription = this.service
      .search(this.feedoutCriteria)
      .subscribe((response) => {
        this.pagedResponse = response;
        // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
        this.lastFeedOutCriteria = criteria;
        // set the url to match to add to browser history (if required)
        this.goIfChanged('/feedout', criteria);
      });
  }

  onClear() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    this.feedoutCriteria = new FeedOutCriteria();
    this.clearInputService.clearInput(null);
  }

  onExport() {
    if (this.lastFeedOutCriteria) {
      // TODO this runs really slowly when proxied by the angular dev server as the proxy doesn't stream the response from the java spring boot backend
      window.location.href = this.exportService.getExportUrlPath(
        '/api/feedout/export',
        this.lastFeedOutCriteria
      );
    }
  }

  onEnableEditMode() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    this.editMode = true;
  }

  onDisableEditMode() {
    this.service.search(this.feedoutCriteria).subscribe((response) => {
      this.editMode = false;
      this.edited = {};
      this.saved = {};
      this.loading = {};
      this.deleted = {};
      this.pagedResponse = response;
    });
  }

  onAddClick() {
    const previous: FeedOutEntity | undefined = this.added.slice(-1)[0];

    // Copy/shuffle across certain properties (if defined) from the last added feed out value to facilite data entry
    const newFeedOut: FeedOutEntity = Object.assign(
      new FeedOutEntity(),
      previous?.locationID && { locationID: previous.locationID },
      previous?.dateIn && { dateOut: previous.dateIn },
      previous?.foodTallyList
        ? {
            foodTallyList: previous.foodTallyList.map((foodTally) =>
              Object.assign(new FoodTallyEntity(), foodTally)
            ),
          }
        : { foodTallyList: [] },
      { targetBirdList: [] }
    );

    this.added.push(newFeedOut);
    const index = 0 - this.added.length;
    this.edited[index] = newFeedOut;
    this.loading[index] = false;
    this.saved[index] = false;
  }

  onEditClick(index) {
    this.loading[index] = true;
    this.service
      .findById(this.pagedResponse.results[index].id)
      .pipe(
        finalize(() => {
          this.loading[index] = false;
        })
      )
      .subscribe((response) => {
        this.edited[index] = response;
        this.saved[index] = false;
      });
  }

  onSaveClick(index) {
    this.doSave(index);
  }

  doSave(index) {
    this.loading[index] = true;
    this.messages[index] = null;
    this.error[index] = false;
    // TODO we should cancel this subscription or at least prevent double clicks somehow to avoid unexpected results
    this.service.save(this.edited[index]).subscribe(
      (response) => {
        if (response.messages.length > 0) {
          //TODO do something with this
          //this.onCancel();
          this.loading[index] = false;
          this.messages[index] = response.messages;
          this.error[index] = true;
        } else {
          // update the view results with the saved edit result
          this.pagedResponse.results[index] = response.model;
          this.loading[index] = false;
          this.saved[index] = true;
        }
      },
      () => {
        this.loading[index] = false;
        this.error[index] = true;
      }
    );
  }

  onDeleteClick(index) {
    this._confirmDeleteIndex = index;
    this.confirmDeleteModal.show();
  }

  doDelete(index) {
    let id;
    if (index < 0) {
      // this is an added feed out
      id = this.edited[index].id;
    } else {
      id = this.pagedResponse.results[index].id;
    }
    if (id) {
      this.loading[index] = true;
      this.service.delete(id).subscribe(
        () => {
          this.loading[index] = false;
          this.deleted[index] = true;
        },
        () => {
          this.loading[index] = false;
          this.error[index] = true;
        }
      );
    } else {
      // must be a new feed out, just mark it as deleted
      this.deleted[index] = true;
    }
  }

  onCancelClick(index) {
    this.messages[index] = null;
    this.edited[index] = null;
    this.error[index] = false;
  }

  toAddIndex(index) {
    return 0 - 1 - index;
  }

  onConfirmDelete() {
    this.doDelete(this._confirmDeleteIndex);
    this.confirmDeleteModal.hide();
  }

  onCancelDelete() {
    this._confirmDeleteIndex = null;
    this.confirmDeleteModal.hide();
  }

  onSaveAll() {
    for (let i = 0; i < this.added.length; i++) {
      const index = this.toAddIndex(i);
      if (!this.saved[index]) {
        this.doSave(index);
      }
    }
    for (let i = 0; i < this.pagedResponse.results.length; i++) {
      const index = i;
      if (this.edited[index] && !this.saved[index]) {
        this.doSave(index);
      }
    }
  }

  get editing() {
    // return true for any new records being edited
    for (let i = 0; i < this.added.length; i++) {
      const index = this.toAddIndex(i);
      if (this.edited[index] && !this.saved[index] && !this.deleted[index]) {
        return true;
      }
    }
    // return true for any existing records being edited
    for (let i = 0; i < this.pagedResponse.results.length; i++) {
      const index = i;
      if (this.edited[index] && !this.saved[index] && !this.deleted[index]) {
        return true;
      }
    }
  }

  get allMessages(): ValidationMessage[] {
    let m: ValidationMessage[] = [];
    // get messages of all new records being edited
    for (let i = 0; i < this.added.length; i++) {
      const index = this.toAddIndex(i);
      if (this.edited[index] && !this.saved[index] && !this.deleted[index]) {
        if (this.messages[index]) {
          m = m.concat(this.messages[index]);
        }
      }
    }
    // get messages of all existing records being edited
    if (this.pagedResponse) {
      for (let i = 0; i < this.pagedResponse.results.length; i++) {
        const index = i;
        if (this.edited[index] && !this.saved[index] && !this.deleted[index]) {
          if (this.messages[index]) {
            m = m.concat(this.messages[index]);
          }
        }
      }
    }
    if (m.length > 0) {
      return m;
    } else {
      return null;
    }
  }

  prevent($event): void {
    $event.preventDefault();
    $event.stopPropagation();
  }
}

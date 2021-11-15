import { Subscription } from 'rxjs/index';

import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';

import { AbstractSearchComponent } from '../../../common/abstract-search-component';
import { PagedResponse } from '../../../domain/response/paged-response';
import { OptionsService } from '../../../common/options.service';
import { OptionListService } from '../../optionlist/option-list.service';
import { InputService } from '../../../common/input.service';
import { ExportService } from '../../../common/export.service';
import { SearchCacheService } from '../../../common/searchcache.service';

@Component({
  selector: 'app-option-list-search',
  templateUrl: 'optionlist-search.component.html',
})
export class OptionListSearchComponent
  extends AbstractSearchComponent
  implements OnInit, OnDestroy {
  pagedResponse: PagedResponse;
  routeSubscription: Subscription;
  listName = '';
  pageTitle = '';
  isAuthorized = false;

  constructor(
    private router: Router,
    public optionsService: OptionsService,
    public optionListService: OptionListService,
    private route: ActivatedRoute,
    location: Location
  ) {
    super(location);
  }

  ngOnInit(): void {
    this.pagedResponse = new PagedResponse();
    this.listName = this.route.snapshot.params['name'];
    this.optionsService.getOptionListTitles().subscribe((titles) => {
      //console.log(titles[this.listName]);
      this.pageTitle = titles[this.listName];
    });
    this.optionsService.getOptions(this.listName).subscribe((list) => {
      //console.log(list);
      list.shift();
      this.pagedResponse.results = list;
      this.isAuthorized = this.optionListService.isAuthorizedToAdd();
    });
  }

  ngOnDestroy(): void {}

  onNew() {
    this.router.navigate(['settings/optionlist/' + this.listName + '/create']);
  }
}

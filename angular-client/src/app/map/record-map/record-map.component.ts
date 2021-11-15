import { Component, OnInit } from '@angular/core';
import { RecordCriteria } from '../../record/record.criteria';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { RecordService } from '../../record/record.service';
import { RecordSearchDTO } from '../../record/record-search-dto';

@Component({
  selector: 'app-record-map',
  templateUrl: './record-map.component.html',
})
export class RecordMapComponent implements OnInit {
  recordCriteria: RecordCriteria;
  recordSearchResults: RecordSearchDTO[];

  firstIslandSearchResult: string = null;

  routeSubscription: Subscription;
  searchSubscription: Subscription = new Subscription();

  constructor(private service: RecordService, private route: ActivatedRoute) {}

  ngOnInit() {
    this.routeSubscription = this.route.queryParams.subscribe((params) => {
      // run if the user refreshed the page or there was a call to route.navigate (not location.go())
      this.recordCriteria = new RecordCriteria();
      // set defaults before populating from params
      this.recordCriteria = this.service.setDefaults(this.recordCriteria);

      this.recordCriteria.populateFromParams(params);

      // Override the Page size for mapping because we want more data points
      // Quick fix for KD-478
      this.recordCriteria.pageSize = 10000;

      this.doSearch();
    });
  }

  doSearch() {
    // cancel any search in progress
    this.searchSubscription.unsubscribe();
    this.searchSubscription = this.service
      .findByCriteriaAndMarkLatestLocation(this.recordCriteria)
      .subscribe((response) => {
        this.recordSearchResults = response.results;
        if (this.recordSearchResults.length > 0) {
          this.firstIslandSearchResult = this.recordSearchResults[0].island;
        }
      });
  }
}

import { Subscription } from 'rxjs/index';
import { map } from 'rxjs/operators';

import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import * as moment from 'moment';
import { TabsetComponent } from 'ngx-bootstrap/tabs';

import { ReportLocationCriteria } from './report-location.criteria';
import { ReportLocationService } from './report-location.service';
import { ExportService } from '../../common/export.service';
import { InputService } from '../../common/input.service';
import { OptionsService } from '../../common/options.service';

@Component({
  selector: 'app-report-location',
  templateUrl: './report-location.component.html',
})
export class ReportLocationComponent implements OnInit, OnDestroy {
  reportLocationCriteria: ReportLocationCriteria = new ReportLocationCriteria();
  lastReportLocationCriteria: ReportLocationCriteria;
  results;
  summary;
  routeSubscription: Subscription;
  detailFilter = '';

  @ViewChild('locationReportTabs', { static: true })
  reportTabs: TabsetComponent;

  constructor(
    private router: Router,
    private service: ReportLocationService,
    public optionsService: OptionsService,
    private clearInputService: InputService,
    private exportService: ExportService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.reportLocationCriteria = new ReportLocationCriteria();
    this.reportLocationCriteria.queryDate = moment().toDate();
    this.reportLocationCriteria.showUnknown = false;
    this.reportLocationCriteria.showEgg = false;
    this.reportLocationCriteria.showChick = false;
    this.reportLocationCriteria.showJuvenile = true;
    this.reportLocationCriteria.showAdult = true;

    this.routeSubscription = this.route.params.subscribe((params) => {
      // copy all criteria into the location criteria object
      Object.keys(params).forEach((key) => {
        if (key === 'pageNumber') {
          // we have to set the page number to an integer for the ngx-bootstrap pagination component to work
          this.reportLocationCriteria.pageNumber = parseInt(
            params.pageNumber,
            10
          );
        } else if (['queryDate'].includes(key)) {
          // Date that come from strings (i.e. queryParams) need to be revived back into Date objects
          this.reportLocationCriteria[key] = new Date(params[key]);
        } else if (
          [
            'showUnknown',
            'showEgg',
            'showChick',
            'showJuvenile',
            'showAdult',
          ].includes(key)
        ) {
          // booleans that come from strings (i.e. queryParams) need to be revived back into booleans
          this[key] = params[key] === 'true';
        } else {
          this.reportLocationCriteria[key] = params[key];
        }
      });

      // save a CLONE of the criteria
      const savedReportLocationCriteria = Object.assign(
        new ReportLocationCriteria(),
        this.reportLocationCriteria
      );
      this.service
        .search(this.reportLocationCriteria)
        .pipe(
          map((response) => {
            // we filter out results that don't match the selected age classes
            const filteredByAgeClass = response.filter(
              (element) =>
                (element.ageClass === 'Unknown' &&
                  savedReportLocationCriteria.showUnknown) ||
                (element.ageClass === 'Egg' &&
                  savedReportLocationCriteria.showEgg) ||
                (element.ageClass === 'Chick' &&
                  savedReportLocationCriteria.showChick) ||
                (element.ageClass === 'Juvenile' &&
                  savedReportLocationCriteria.showJuvenile) ||
                (element.ageClass === 'Adult' &&
                  savedReportLocationCriteria.showAdult)
            );
            return filteredByAgeClass;
          })
        )
        .subscribe((response) => {
          console.log('Got response raw', response);

          this.detailFilter = '';

          this.results = response.sort((t1: any, t2: any) => {
            if (t1.island > t2.island) {
              return 1;
            }
            if (t1.island < t2.island) {
              return -1;
            }
            return 0;
          });

          this.generateSummary(response);
          // now we know the search completed, save the clone so that the export function exports the same results displayed on the page
          this.lastReportLocationCriteria = savedReportLocationCriteria;
        });
    });
  }

  generateSummary(response: any) {
    // Generate the summary list from the results
    const islandSummary = {};
    const summary = {};
    let total = 0;
    response.forEach((element) => {
      if (!summary[element.island + element.ageClass + element.sex]) {
        summary[element.island + element.ageClass + element.sex] = {
          type: 'SUMMARY',
          island: element.island,
          ageClass: element.ageClass,
          sex: element.sex,
          count: 1,
        };
      } else {
        let newCount =
          summary[element.island + element.ageClass + element.sex].count;
        summary[
          element.island + element.ageClass + element.sex
        ].count = ++newCount;
      }
      if (!islandSummary[element.island]) {
        islandSummary[element.island] = { island: element.island, count: 1 };
      } else {
        let newCount = islandSummary[element.island].count;
        islandSummary[element.island].count = ++newCount;
      }
      total++;
    });
    console.log('Summary', summary);
    const summaryList: any[] = Object.values(summary);
    // sort summary list by island, age class, then sex
    summaryList.sort((a, b) => {
      if (!a.island && !b.island) {
        return 0;
      } else if (!a.island) {
        return -1;
      } else if (!b.island) {
        return 1;
      } else {
        const ic = a.island.localeCompare(b.island);
        if (ic !== 0) {
          return ic;
        } else if (!a.ageClass && !b.ageClass) {
          return 0;
        } else if (!a.ageClass) {
          return -1;
        } else if (!b.ageClass) {
          return 1;
        } else {
          const agc = a.ageClass.localeCompare(b.ageClass);
          if (agc !== 0) {
            return agc;
          } else if (!a.sex && !b.sex) {
            return 0;
          } else if (!a.sex) {
            return -1;
          } else if (!b.sex) {
            return 1;
          } else {
            return a.sex.localeCompare(b.sex);
          }
        }
      }
    });
    // iterate through the list in reverse to add in the island totals
    let currentIsland = '';
    for (let i = summaryList.length - 1; i >= 0; i--) {
      const element = summaryList[i];
      if (element.island !== currentIsland) {
        summaryList.splice(i + 1, 0, {
          type: 'ISLAND_SUMMARY',
          island: element.island,
          count: islandSummary[element.island].count,
        });
        currentIsland = element.island;
      }
    }
    // add the total at the bottom
    summaryList.push({ type: 'TOTAL_SUMMARY', count: total });

    this.summary = summaryList;
  }

  ngOnDestroy(): void {
    this.routeSubscription.unsubscribe();
  }

  onPageChanged(event: any) {
    if (event.page) {
      this.reportLocationCriteria.pageNumber = event.page;
      this.doSearch();
    }
  }

  onSearch() {
    // ensure we search for the first page of the results
    this.reportLocationCriteria.pageNumber = 1;
    this.doSearch();
  }

  doSearch() {
    const criteria = {};
    Object.keys(this.reportLocationCriteria).forEach((key) => {
      const value = this.reportLocationCriteria[key];
      if (value !== null) {
        if (value instanceof Date) {
          // Dates that go outbound as strings (i.e. queryParams) need to be in the right format or deleted from the object
          criteria[key] = moment(value).format('YYYY-MM-DD');
        } else {
          criteria[key] = value;
        }
      }
    });
    this.router.navigate(['/report/location', criteria]);
  }

  onClear() {
    this.reportLocationCriteria = new ReportLocationCriteria();
    this.reportLocationCriteria.queryDate = moment().toDate();
    this.clearInputService.clearInput(null);
  }

  onClick(item) {
    this.detailFilter = item.island;
    this.reportTabs.tabs[1].active = true;
  }
}

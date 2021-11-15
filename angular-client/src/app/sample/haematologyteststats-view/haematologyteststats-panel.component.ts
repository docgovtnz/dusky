import { Component, Input, OnInit } from '@angular/core';

import { HaematologyTestEntity } from '../../domain/haematologytest.entity';
import { SampleService } from '../../sample/sample.service';
import { TestStatsDto } from '../../sample/test-stats-dto';
import { ResultDto } from '../../sample/result-dto';
import { ResultRankDto } from '../../sample/result-rank-dto';

@Component({
  selector: 'app-haematologyteststats-panel',
  templateUrl: 'haematologyteststats-panel.component.html',
})
export class HaematologyTestStatsPanelComponent implements OnInit {
  private _type: string;
  private _testList: HaematologyTestEntity[];

  private allTestsStats: TestStatsDto[];
  private allTestsSub;
  private ranksSub;

  testStatsList: TestStatsDto[] = null;
  testRanksList: ResultRankDto[] = null;

  constructor(private service: SampleService) {}

  ngOnInit(): void {}

  @Input()
  set type(value: string) {
    // if the type has changed refresh all the test stats
    if (value !== this._type) {
      this.refreshAllTestsStats(value);
    }
    this.refreshStatsList(value, this.allTestsStats, this.testList);
    this.refreshRanksList(value, this.testList);
    this._type = value;
  }

  get type() {
    return this._type;
  }

  @Input()
  set testList(value: HaematologyTestEntity[]) {
    this.refreshStatsList(this.type, this.allTestsStats, value);
    this.refreshRanksList(this.type, value);
    this._testList = value;
  }

  get testList() {
    return this._testList;
  }

  refreshAllTestsStats(type) {
    if (type) {
      if (this.allTestsSub) {
        this.allTestsSub.unsubscribe();
      }
      this.allTestsSub = this.service
        .getHaematologyTestsStats(type)
        .subscribe((hts) => {
          this.allTestsStats = hts;
          this.refreshStatsList(type, hts, this.testList);
        });
    }
  }

  refreshRanksList(type, tests) {
    this.testRanksList = null;
    if (type && tests) {
      const results = [];
      tests.forEach((ht) => {
        const result = parseFloat(ht.result);
        if (!isNaN(result)) {
          const r = new ResultDto();
          r.test = ht.test;
          r.result = result;
          results.push(r);
        }
      });
      if (this.ranksSub) {
        this.ranksSub.unsubscribe();
      }
      this.ranksSub = this.service
        .getHaematologyTestsRanks(type, results)
        .subscribe((ranks) => {
          this.testRanksList = [];
          tests.forEach((ht) => {
            const rankDto = ranks.find((r) => r.test === ht.test);
            let rank = null;
            if (rankDto) {
              rank = rankDto.rank;
            }
            this.testRanksList.push(rank);
          });
        });
    }
  }

  refreshStatsList(type, stats, tests) {
    this.testStatsList = null;
    if (type && stats && tests) {
      this.testStatsList = [];
      const results = [];
      tests.forEach((t) => {
        let ts = stats.find((testStat) => testStat.test === t.test);
        if (!ts) {
          ts = new TestStatsDto();
        }
        this.testStatsList.push(ts);
        const result = parseFloat(t.result);
        if (!isNaN(result)) {
          const r = new ResultDto();
          r.test = ts.test;
          r.result = result;
          results.push(r);
        }
      });
    }
  }
}

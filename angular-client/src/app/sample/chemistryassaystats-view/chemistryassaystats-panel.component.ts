import { Component, Input, OnInit } from '@angular/core';

import { ChemistryAssayEntity } from '../../domain/chemistryassay.entity';
import { SampleService } from '../../sample/sample.service';
import { TestStatsDto } from '../../sample/test-stats-dto';
import { ResultDto } from '../../sample/result-dto';
import { ResultRankDto } from '../../sample/result-rank-dto';

@Component({
  selector: 'app-chemistryassaystats-panel',
  templateUrl: 'chemistryassaystats-panel.component.html',
})
export class ChemistryAssayStatsPanelComponent implements OnInit {
  private _type: string;
  private _assayList: ChemistryAssayEntity[];

  private allAssaysStats: TestStatsDto[];
  private allAssaysSub;
  private ranksSub;

  assayStatsList: TestStatsDto[] = null;
  assayRanksList: ResultRankDto[] = null;

  constructor(private service: SampleService) {}

  ngOnInit(): void {}

  @Input()
  set type(value: string) {
    // if the type has changed refresh all the assays stats
    if (value !== this._type) {
      this.refreshAllAssaysStats(value);
    }
    this.refreshStatsList(value, this.allAssaysStats, this.assayList);
    this.refreshRanksList(value, this.assayList);
    this._type = value;
  }

  get type() {
    return this._type;
  }

  @Input()
  set assayList(value: ChemistryAssayEntity[]) {
    this.refreshStatsList(this.type, this.allAssaysStats, value);
    this.refreshRanksList(this.type, value);
    this._assayList = value;
  }

  get assayList() {
    return this._assayList;
  }

  refreshAllAssaysStats(type) {
    this.allAssaysStats = null;
    if (type) {
      if (this.allAssaysSub) {
        this.allAssaysSub.unsubscribe();
      }
      this.allAssaysSub = this.service
        .getChemistryAssaysStats(type)
        .subscribe((hts) => {
          this.allAssaysStats = hts;
          this.refreshStatsList(type, hts, this.assayList);
        });
    }
  }

  refreshRanksList(type, assays) {
    this.assayRanksList = null;
    if (type && assays) {
      const results = [];
      assays.forEach((ca) => {
        const result = parseFloat(ca.result);
        if (!isNaN(result)) {
          const r = new ResultDto();
          r.test = ca.chemistryAssay;
          r.result = result;
          results.push(r);
        }
      });
      if (this.ranksSub) {
        this.ranksSub.unsubscribe();
      }
      this.ranksSub = this.service
        .getChemistryAssaysRanks(type, results)
        .subscribe((ranks) => {
          this.assayRanksList = [];
          assays.forEach((ca) => {
            const rankDto = ranks.find((r) => r.test === ca.chemistryAssay);
            let rank = null;
            if (rankDto) {
              rank = rankDto.rank;
            }
            this.assayRanksList.push(rank);
          });
        });
    }
  }

  refreshStatsList(type, stats, assays) {
    this.assayStatsList = null;
    if (type && stats && assays) {
      this.assayStatsList = [];
      const results = [];
      assays.forEach((ca) => {
        let ts = stats.find((cts) => cts.test === ca.chemistryAssay);
        if (!ts) {
          ts = new TestStatsDto();
        }
        this.assayStatsList.push(ts);
        const result = parseFloat(ca.result);
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

import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { RecordService } from '../../../record/record.service';
import { RecordCriteria } from '../../../record/record.criteria';
import { EsriMapComponent } from '../../../map/esri-map.component';
import { RecordSearchDTO } from '../../../record/record-search-dto';

@Component({
  selector: 'app-bird-location-map',
  templateUrl: './bird-location-map.component.html',
})
export class BirdLocationMapComponent implements OnInit {
  private _birdID: string;
  private _currentIsland: string;

  recordList: RecordSearchDTO[];

  @ViewChild('esriMapComponent', { static: true })
  esriMapComponent: EsriMapComponent;

  constructor(private recordService: RecordService) {}

  ngOnInit() {}

  get birdID(): string {
    return this._birdID;
  }

  @Input()
  set birdID(value: string) {
    this._birdID = value;
    this.loadBirdLocations();
    console.log('BirdLocationMap: birdID = ' + value);
  }

  get currentIsland(): string {
    return this._currentIsland;
  }

  @Input()
  set currentIsland(value: string) {
    this._currentIsland = value;
    this.esriMapComponent.selectedIslandName = value;
  }

  loadBirdLocations() {
    if (this._birdID) {
      const criteria = new RecordCriteria();
      criteria.birdIDs = [this._birdID];
      criteria.pageSize = 100;
      this.recordService
        .findByCriteriaAndMarkLatestLocation(criteria)
        .subscribe((recordList) => {
          this.recordList = recordList.results;
          this.esriMapComponent.birdLocations = this.recordList;
        });
    }
  }
}

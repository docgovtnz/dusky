import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RecordSearchDTO } from './../record-search-dto';
import { RecordEntity } from '../../domain/record.entity';
import { RecordService } from '../record.service';
import { DeleteByIdCheckDto } from '../../domain/response/delete-by-id-check-dto';
import { BirdEntity } from '../../domain/bird.entity';
import { BirdService } from '../../bird/bird.service';
import { LocationEntity } from '../../domain/location.entity';
import { LocationService } from '../../location/location.service';
import { EsriMapComponent } from '../../map/esri-map.component';

@Component({
  selector: 'app-record-view',
  templateUrl: 'record-view.component.html',
})
export class RecordViewComponent implements OnInit {
  isCollapsed = false;

  recordEntity: RecordEntity = new RecordEntity();
  deleteCheck: DeleteByIdCheckDto;

  // the current revision displayed (may be the current entity or an older revision)
  revision: RecordEntity;

  // the bird and location of the record (does not change with revision)
  birdEntity: BirdEntity;
  locationEntity: LocationEntity;

  _mapComponent: EsriMapComponent;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: RecordService,
    private birdService: BirdService,
    private locationService: LocationService
  ) {}

  ngOnInit(): void {
    this.service.findById(this.route.snapshot.params.id).subscribe((entity) => {
      this.recordEntity = entity;
      // start by using the current record as the current revision
      this.revision = entity;
      this.loadDeleteCheck();

      this.birdService.findById(this.revision.birdID).subscribe((response) => {
        this.birdEntity = response;
        this.refreshMap();
      });

      // locationID can be null in the case of transmitter records
      if (this.revision.locationID) {
        this.locationService
          .findById(this.revision.locationID)
          .subscribe((response) => {
            this.locationEntity = response;
            this.refreshMap();
          });
      }

      this.refreshMap();
    });
  }

  loadDeleteCheck() {
    this.service
      .deleteByIdCheck(this.recordEntity.id)
      .subscribe((deleteCheck) => (this.deleteCheck = deleteCheck));
  }

  isDeleteOk() {
    return this.deleteCheck && this.deleteCheck.deleteOk;
  }

  onEdit() {
    this.router.navigate(['/record/edit/' + this.recordEntity.id]);
  }

  onDelete() {
    this.service.delete(this.recordEntity.id).subscribe(() => {
      this.router.navigate(['/record']);
    });
  }

  onRevisionEvent(revision) {
    this.revision = revision;
    if (this.revision.birdID) {
      this.birdService.findById(this.revision.birdID).subscribe((response) => {
        this.birdEntity = response;
      });
    }
  }

  refreshMap() {
    if (this._mapComponent) {
      let dto: RecordSearchDTO = null;
      if (this.recordEntity) {
        dto = new RecordSearchDTO();
        dto.dateTime = this.recordEntity.dateTime;
        dto.easting = this.recordEntity.easting;
        dto.northing = this.recordEntity.northing;
        dto.island = this.recordEntity.island;
        dto.mapFeatureType = 'Bird';
        dto.recordType = this.recordEntity.recordType;
        if (this.birdEntity) {
          dto.birdName = this.birdEntity.birdName;
        }
        if (this.locationEntity) {
          dto.locationEasting = this.locationEntity.easting;
          dto.locationNorthing = this.locationEntity.northing;
          dto.locationName = this.locationEntity.locationName;
        }
      }
      if (dto) {
        this._mapComponent.selectedIslandName = dto.island;
        this._mapComponent.selectedRecord = dto;
      } else {
        this._mapComponent.selectedIslandName = null;
        this._mapComponent.selectedRecord = null;
      }
    }
  }

  @ViewChild(EsriMapComponent)
  set mapComponent(mapComponent: EsriMapComponent) {
    this._mapComponent = mapComponent;
    this.refreshMap();
  }
}
